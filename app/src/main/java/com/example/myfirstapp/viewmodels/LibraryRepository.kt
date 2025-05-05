package com.example.myfirstapp.viewmodels

import android.content.Context
import com.example.myfirstapp.R
import com.example.myfirstapp.data.LibraryData
import com.example.myfirstapp.data.SortType
import com.example.myfirstapp.data.UserPreferencesRepository
import com.example.myfirstapp.data.database.EntityMapper
import com.example.myfirstapp.data.database.LibraryDatabase
import com.example.myfirstapp.data.database.LibraryItemEntity
import com.example.myfirstapp.data.googlebooks.GoogleBookItem
import com.example.myfirstapp.data.googlebooks.RetrofitClient
import com.example.myfirstapp.library.Book
import com.example.myfirstapp.library.LibraryItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LibraryRepository(private val context: Context) {
    val userPreferencesRepository = UserPreferencesRepository(context)
    private var sortType: SortType = SortType.BY_NAME

    private val _items = MutableStateFlow<List<LibraryItem>>(emptyList())
    val items: StateFlow<List<LibraryItem>> = _items

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore

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

    val getCurrentOffset: Int
        get() = currentOffset

    val getCurrentApiOffset: Int
        get() = currentApiOffset

    fun initRepository() {
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


    suspend fun loadLocalItems() {
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

    suspend fun loadMoreLocal(isForward: Boolean) {
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


    suspend fun loadApiItems(title: String, author: String) {
        val result = withContext(Dispatchers.IO) {
            val googleBooks = loadApiPage(title, author, currentApiOffset, apiPageSize)
            googleBooks
        }
        _items.value = result
    }

    private suspend fun loadApiPage(title: String, author: String, offset: Int, limit: Int): List<LibraryItem> {
        val query = buildQuery(title, author)

        val result = withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.apiService.searchBooks(
                    query = query,
                    startIndex = offset,
                    maxResults = limit
                )

                if (response.isSuccessful) {
                    val books = response.body()?.items?.map { item ->
                        Book(
                            name = item.volumeInfo.title,
                            author = item.volumeInfo.authors?.joinToString(", ")
                                ?: context.getString(R.string.unknown),
                            numOfPage = item.volumeInfo.pageCount ?: 0,
                            id = item.volumeInfo.industryIdentifiers
                                ?.find { it.type == idType }
                                ?.identifier?.toIntOrNull()
                                ?: getHashCode(item)
                        )
                    } ?: emptyList()
                    canLoadMoreApi = books.size == limit
                    books
                } else {
                    val errorMsg = when (response.code()) {
                        400 -> context.getString(R.string.error_400)
                        403 -> context.getString(R.string.error_403)
                        404 -> context.getString(R.string.error_404)
                        500 -> context.getString(R.string.error_500)
                        502 -> context.getString(R.string.error_502)
                        503 -> context.getString(R.string.error_503)
                        else -> "${context.getString(R.string.error)}: ${response.message()}"
                    }
                    throw Exception(errorMsg)
                }
            } catch (e: Exception) {
                when (e) {
                    is java.net.UnknownHostException -> {
                        throw Exception(context.getString(R.string.error_connection))
                    }
                    is java.net.SocketTimeoutException -> {
                        throw Exception(context.getString(R.string.error_timeout))
                    }
                    is retrofit2.HttpException -> {
                        val errorMessage = "${context.getString(R.string.error_500)}: ${e.message()}"
                        throw Exception(errorMessage)
                    }
                    else -> {
                        throw Exception("${context.getString(R.string.error_unknown)}: ${e.localizedMessage}")
                    }
                }
            }
        }
        return result
    }

    suspend fun loadMoreApi(title: String, author: String, isForward: Boolean) {
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

    suspend fun addItem(item: LibraryItem) : Int {
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

    suspend fun addItemToLocal(item: LibraryItem) : Boolean {
        val entity = EntityMapper.toEntity(item)
        withContext(Dispatchers.IO) {
            if (dao.isIdExists(item.id)) throw Exception(context.getString(R.string.error_id))
            dao.insert(entity)
            totalItems++
        }
        return true
    }

    suspend fun removeItem(position: Int) {
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

    suspend fun updateItemAccess(position: Int) : Boolean {
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


    suspend fun isIdExists(id: Int): Boolean {
        val localExists = _items.value.any { it.id == id }
        if (localExists) return true
        return withContext(Dispatchers.IO) {
            dao.isIdExists(id)
        }
    }

    private fun getItemPosition(item: LibraryItem): Int {
        return _items.value.indexOfFirst { it.id == item.id }
    }

    fun clearItems() {
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
            else -> throw IllegalArgumentException(context.getString(R.string.empty_search))
        }
    }

    private fun getHashCode(item: GoogleBookItem) : Int {
        val title = item.volumeInfo.title.orEmpty()
        val author = item.volumeInfo.authors.orEmpty()
        return (title + author).hashCode().and(0x7FFFFFFF)
    }

    companion object {
        const val ERROR_DATABASE_LOAD = "111"
        const val ERROR_DATABASE_ADD = "222"
        const val ERROR_DATABASE_REMOVE = "333"
        const val ERROR_DATABASE_UPDATE= "444"
        const val ERROR_UNKNOWN = "555"

        val errorMessages = mapOf(
            ERROR_DATABASE_LOAD to R.string.error_db_load,
            ERROR_DATABASE_ADD to R.string.error_db_add,
            ERROR_DATABASE_REMOVE to R.string.error_db_remove,
            ERROR_DATABASE_UPDATE to R.string.error_db_update,
            ERROR_UNKNOWN to R.string.error_unknown
        )
    }
}