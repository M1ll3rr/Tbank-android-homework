package com.example.data.repository

import android.content.Context
import com.example.data.database.LibraryDatabase
import com.example.data.database.LibraryItemEntity
import com.example.data.googlebooks.GoogleBookItem
import com.example.data.localdata.LibraryData
import com.example.data.mappers.AppError
import com.example.data.mappers.EntityMapper
import com.example.domain.entities.Book
import com.example.domain.entities.LibraryItem
import com.example.domain.repository.LibraryRepository
import com.example.domain.types.SortType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class LibraryRepositoryImpl(private val context: Context) : LibraryRepository {
    override val userPreferencesRepository = UserPreferencesRepositoryImpl(context)
    private var sortType: SortType = SortType.BY_NAME

    private val _items = MutableStateFlow<List<LibraryItem>>(emptyList())
    override val items: StateFlow<List<LibraryItem>> = _items

    private val _isLoadingMore = MutableStateFlow(false)
    override val isLoadingMore: StateFlow<Boolean> = _isLoadingMore

    private val database = LibraryDatabase.getDatabase(context)
    private val dao = database.libraryDao()

    private val pageSize = 30

    private var currentOffset = 0
    private var totalItems = 0

    private val apiPageSize = 20
    private var currentApiOffset = 0
    private var canLoadMoreApi = true

    private var sortTypeJob: Job? = null
    private val idType = "ISBN_10"

    override val getCurrentOffset: Int
        get() = currentOffset

    override val getCurrentApiOffset: Int
        get() = currentApiOffset

    override fun initRepository() {
        CoroutineScope(Dispatchers.IO).launch {
            initDatabase()
        }
        observeSortType()
    }

    private fun observeSortType() {
        sortTypeJob?.cancel()
        sortTypeJob = CoroutineScope(Dispatchers.IO).launch {
            userPreferencesRepository.sortTypeFlow.collect { newSortType ->
                sortType = newSortType
                loadLocalItems()
            }
        }
    }

    private suspend fun initDatabase() {
        withContext(Dispatchers.IO) {
            if (dao.getItemsCount() == 0) {
                val entities = LibraryData.items.map { EntityMapper.toEntity(it) }
                dao.insertAll(entities)
            }
            totalItems = dao.getItemsCount()
            loadLocalItems()
        }
    }

    override suspend fun loadLocalItems() {
        val result = withContext(Dispatchers.IO) {
            val entities = loadLocalPage(currentOffset, pageSize)
            val loadedItems = entities.map { EntityMapper.fromEntity(it) }
            loadedItems
        }
        _items.value = result
    }

    private suspend fun loadLocalPage(offset: Int, limit: Int): List<LibraryItemEntity> {
        return when (sortType) {
            SortType.BY_NAME -> dao.getItemsSortedByName(offset, limit)
            SortType.BY_DATE -> dao.getItemsSortedByDate(offset, limit)
        }
    }

    override suspend fun loadMoreLocal(isForward: Boolean) {
        if (_isLoadingMore.value) return
        _isLoadingMore.value = true

        val result = withContext(Dispatchers.IO) {
            val currentItems = _items.value.toMutableList()
            if (isForward) {
                val newOffset = currentOffset + currentItems.size
                if (newOffset < totalItems) {
                    val loadCount = (pageSize / 2).coerceAtMost(totalItems - newOffset)

                    val newEntities = loadLocalPage(newOffset, loadCount)
                    val newItems = newEntities.map { EntityMapper.fromEntity(it) }

                    currentItems.addAll(newItems)
                    if (currentItems.size > pageSize) {
                        repeat(loadCount) {
                            currentItems.removeAt(0)
                        }
                    }

                    currentOffset += loadCount
                }
            } else {
                if (currentOffset > 0) {
                    val loadCount = (pageSize / 2).coerceAtMost(currentOffset)
                    val newOffset = currentOffset - loadCount

                    val newEntities = loadLocalPage(newOffset, loadCount)
                    val newItems = newEntities.map { EntityMapper.fromEntity(it) }

                    currentItems.addAll(0, newItems)
                    if (currentItems.size > pageSize) {
                        repeat(loadCount) {
                            currentItems.removeAt(currentItems.size - 1)
                        }
                    }

                    currentOffset = newOffset
                }
            }
            currentItems
        }
        _items.value = result
        _isLoadingMore.value = false
    }

    override suspend fun loadApiItems(title: String, author: String) {
        val result = withContext(Dispatchers.IO) {
            val googleBooks = loadApiPage(title, author, currentApiOffset, apiPageSize)
            googleBooks
        }
        _items.value = result
    }

    private suspend fun loadApiPage(title: String, author: String, offset: Int, limit: Int): List<LibraryItem> {
        val query = buildQuery(title, author)

        val result =
            try {
                val response = com.example.data.googlebooks.RetrofitClient.apiService.searchBooks(
                    query = query,
                    startIndex = offset,
                    maxResults = limit
                )

                if (response.isSuccessful) {
                    val books = withContext(Dispatchers.Default) {
                        response.body()?.items?.map { item ->
                            Book(
                                name = item.volumeInfo.title,
                                author = item.volumeInfo.authors?.joinToString(", ")
                                    ?: AppError.UnknownError.errorCode,
                                numOfPage = item.volumeInfo.pageCount ?: 0,
                                id = item.volumeInfo.industryIdentifiers
                                    ?.find { it.type == idType }
                                    ?.identifier?.toIntOrNull()
                                    ?: getHashCode(item)
                            )
                        } ?: emptyList()
                    }
                    canLoadMoreApi = books.size == limit
                    books
                } else {
                    val errorMsg = when (response.code()) {
                        400 -> AppError.Error400.errorCode
                        403 -> AppError.Error403.errorCode
                        404 -> AppError.Error404.errorCode
                        500 -> AppError.Error500.errorCode
                        502 -> AppError.Error502.errorCode
                        503 -> AppError.Error503.errorCode
                        else -> "Error: ${response.message()}"
                    }
                    throw Exception(errorMsg)
                }
            } catch (e: UnknownHostException) {
                throw Exception(AppError.ConnectionError.errorCode)
            } catch (e: SocketTimeoutException) {
                throw Exception(AppError.TimeoutError.errorCode)
            } catch (e: HttpException) {
                throw Exception(AppError.Error500.errorCode)
            } catch (e: Exception) {
                throw Exception("Error: ${e.localizedMessage}")
            }
        return result
    }

    override suspend fun loadMoreApi(title: String, author: String, isForward: Boolean) {
        if (_isLoadingMore.value) return
        _isLoadingMore.value = true

        val result = withContext(Dispatchers.IO) {
            val currentItems = _items.value.toMutableList()
            if (isForward) {
                if (canLoadMoreApi) {
                    var loadCount = apiPageSize / 2
                    val newOffset = currentApiOffset + currentItems.size

                    val newItems = loadApiPage(title, author, newOffset, loadCount)
                    loadCount = newItems.size

                    currentItems.addAll(newItems)
                    if (currentItems.size > pageSize) {
                        repeat(loadCount) {
                            currentItems.removeAt(0)
                        }
                    }

                    currentApiOffset += loadCount
                }
            } else {
                if (currentApiOffset > 0) {
                    var loadCount = apiPageSize / 2
                    val newOffset = currentApiOffset - loadCount

                    val newItems = loadApiPage(title, author, newOffset, loadCount)
                    loadCount = newItems.size

                    currentItems.addAll(0, newItems)
                    if (currentItems.size > pageSize) {
                        repeat(loadCount) {
                            currentItems.removeAt(currentItems.size - 1)
                        }
                    }

                    currentApiOffset = newOffset
                }
            }
            currentItems
        }
        _items.value = result
        _isLoadingMore.value = false
    }

    override suspend fun addItem(item: LibraryItem) : Int {
        val entity = EntityMapper.toEntity(item)
        withContext(Dispatchers.IO) {
            dao.insert(entity)
            totalItems++

            val globalPosition = when (sortType) {
                SortType.BY_NAME -> dao.getPositionByName(item.name)
                SortType.BY_DATE -> dao.getPositionByDate(entity.created)
            }
            val isOnCurrentPage = globalPosition in currentOffset until (currentOffset + pageSize)

            if (isOnCurrentPage) {
                val oldList = _items.value.toMutableList()
                oldList.add(item)
                if (sortType == SortType.BY_NAME) {
                    oldList.sortBy { it.name }
                }
                _items.value = oldList
            }
            else {
                val targetPage = globalPosition / pageSize
                val newOffset = targetPage * pageSize
                currentOffset = newOffset.coerceAtMost(dao.getItemsCount() - pageSize)
                loadLocalItems()
            }
        }
        return getItemPosition(item)
    }

    override suspend fun addItemToLocal(item: LibraryItem) : Boolean {
        val entity = EntityMapper.toEntity(item)
        withContext(Dispatchers.IO) {
            if (dao.isIdExists(item.id))
                throw Exception(AppError.IdError.errorCode)
            dao.insert(entity)
            totalItems++
        }
        return true
    }

    override suspend fun removeItem(position: Int) {
        val result = withContext(Dispatchers.IO) {
            val idToRemove = _items.value[position].id
            dao.deleteById(idToRemove)
            totalItems--
            val oldList = _items.value.toMutableList()
            oldList.removeAt(position)
            oldList
        }
        _items.value = result
    }

    override suspend fun updateItemAccess(position: Int) : Boolean {
        val result = withContext(Dispatchers.IO) {
            val oldList = _items.value.toMutableList()
            val newAccess = !oldList[position].access
            oldList[position].access = newAccess
            val item = oldList[position]
            val entity = EntityMapper.toEntity(item)
            dao.update(entity)
            oldList
        }
        _items.value = result
        return true
    }

    override suspend fun isIdExists(id: Int): Boolean {
        val localExists = _items.value.any { it.id == id }
        if (localExists) return true
        return withContext(Dispatchers.IO) {
            dao.isIdExists(id)
        }
    }

    private fun getItemPosition(item: LibraryItem): Int {
        return _items.value.indexOfFirst { it.id == item.id }
    }

    override fun clearItems() {
        currentOffset = 0
        currentApiOffset = 0
        canLoadMoreApi = true
        _items.value = emptyList()
    }

    private fun buildQuery(title: String, author: String): String {
        val processedTitle = title.replace(" ", "+")
        val processedAuthor = author.replace(" ", "+")
        return when {
            title.isNotEmpty() && author.isNotEmpty() ->
                "intitle:$processedTitle+inauthor:$processedAuthor"
            title.isNotEmpty() -> "intitle:$processedTitle"
            author.isNotEmpty() -> "inauthor:$processedAuthor"
            else -> throw IllegalArgumentException(AppError.EmptySearchError.errorCode)
        }
    }

    private fun getHashCode(item: GoogleBookItem) : Int {
        val title = item.volumeInfo.title
        val author = item.volumeInfo.authors.orEmpty()
        return (title + author).hashCode().and(0x7FFFFFFF)
    }

}