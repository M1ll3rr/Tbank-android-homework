package com.example.myfirstapp.viewmodels

import android.content.Context
import com.example.myfirstapp.R
import com.example.myfirstapp.data.LibraryData
import com.example.myfirstapp.data.SortType
import com.example.myfirstapp.data.UserPreferencesRepository
import com.example.myfirstapp.data.database.EntityMapper
import com.example.myfirstapp.data.database.LibraryDatabase
import com.example.myfirstapp.data.database.LibraryItemEntity
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

    private var sortTypeJob: Job? = null

    val getCurrentOffset: Int
        get() = currentOffset

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
                loadItems()
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
            loadItems()
        }
    }

    private suspend fun loadItems() {
        val result = withContext(Dispatchers.IO) {
            val entities = loadItemsPage(currentOffset, pageSize)
            val loadedItems = entities.map { EntityMapper.fromEntity(it) }
            loadedItems
        }
        _items.value = result
    }

    private suspend fun loadItemsPage(offset: Int, limit: Int): List<LibraryItemEntity> {
        return when (sortType) {
            SortType.BY_NAME -> dao.getItemsSortedByName(offset, limit)
            SortType.BY_DATE -> dao.getItemsSortedByDate(offset, limit)
        }
    }

    suspend fun loadMoreItems(isForward: Boolean) {
        if (_isLoadingMore.value) return
        _isLoadingMore.value = true

        val result = withContext(Dispatchers.IO) {
            val currentItems = _items.value.toMutableList()
            if (isForward) {
                val newOffset = currentOffset + currentItems.size
                if (newOffset < totalItems) {
                    val loadCount = (pageSize / 2).coerceAtMost(totalItems - newOffset)

                    val newEntities = loadItemsPage(newOffset, loadCount)
                    val newItems = newEntities.map { EntityMapper.fromEntity(it) }

                    if (currentItems.size > pageSize) {
                        repeat(loadCount) {
                            currentItems.removeAt(0)
                        }
                        currentOffset += loadCount
                    }
                    currentItems.addAll(newItems)
                }
            } else {
                if (currentOffset > 0) {
                    val loadCount = (pageSize / 2).coerceAtMost(currentOffset)
                    val newOffset = currentOffset - loadCount

                    val newEntities = loadItemsPage(newOffset, loadCount)
                    val newItems = newEntities.map { EntityMapper.fromEntity(it) }

                    if (currentItems.size > pageSize) {
                        repeat(loadCount) {
                            currentItems.removeAt(currentItems.size - 1)
                        }
                    }

                    currentItems.addAll(0, newItems)
                    currentOffset = newOffset
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
                loadItems()
            }
        }
        return getItemPosition(item)
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