package com.example.myfirstapp.viewmodels

import com.example.myfirstapp.R
import com.example.myfirstapp.data.LibraryData
import com.example.myfirstapp.library.LibraryItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import kotlin.random.Random

class LibraryRepository {
    private val _items = MutableStateFlow<List<LibraryItem>>(emptyList())
    val items: StateFlow<List<LibraryItem>> = _items

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val errorFrequency = 5

    private var lastAddedItem: LibraryItem? = null
    private var lastRemovedItemPosition: Int? = null
    private var lastUpdatedItemPosition: Int? = null
    private var lastAction: ActionType? = null

    val getLastAction: ActionType?
        get() = lastAction

    val getLastUpdatedPosition: Int?
        get() = lastUpdatedItemPosition

    val getLastAddedItem: LibraryItem?
        get() = lastAddedItem

    enum class ActionType {
        LOAD, ADD, REMOVE, UPDATE
    }

    suspend fun loadItems() {
        _isLoading.value = true
        _error.value = null
        try {
            lastAction = ActionType.LOAD
            val result = withContext(Dispatchers.IO) {
                delay(Random.nextLong(100, 2000))
                databaseAccess(ERROR_DATABASE_LOAD)
                LibraryData.items.sortedBy { it.id }
            }
            _items.value = result
        } catch (e: Exception) {
            _error.value = e.message
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun addItem(item: LibraryItem) {
        _error.value = null
        try {
            lastAction = ActionType.ADD
            lastAddedItem = item
            val result = withContext(Dispatchers.IO) {
                databaseAccess(ERROR_DATABASE_ADD)
                val oldList = _items.value.toMutableList()
                oldList.add(item)
                oldList.sortedBy { it.id }
            }
            _items.value = result
        } catch (e: Exception) {
            _error.value = e.message
        }
    }

    suspend fun removeItem(position: Int) {
        _error.value = null
        try {
            lastAction = ActionType.REMOVE
            lastRemovedItemPosition = position
            val result = withContext(Dispatchers.IO) {
                databaseAccess(ERROR_DATABASE_REMOVE)
                val oldList = _items.value.toMutableList()
                oldList.removeAt(position)
                oldList.sortedBy { it.id }
            }
            _items.value = result
        } catch (e: Exception) {
            _error.value = e.message
        }
    }

    suspend fun updateItemAccess(position: Int) {
        _error.value = null
        try {
            lastAction = ActionType.UPDATE
            lastUpdatedItemPosition = position
            val result = withContext(Dispatchers.IO) {
                databaseAccess(ERROR_DATABASE_UPDATE)
                val oldList = _items.value.toMutableList()
                val newAccess = !oldList[position].access
                oldList[position].access = newAccess
                oldList
            }
            _items.value = result
        } catch (e: Exception) {
            _error.value = e.message
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun isIdExists(id: Int): Boolean {
        return _items.value.any { it.id == id }
    }

    fun getItemPosition(item: LibraryItem?): Int {
        return _items.value.indexOfFirst { it.id == item?.id }
    }

    suspend fun repeatLastAction() {
        when (lastAction) {
            ActionType.LOAD -> loadItems()
            ActionType.ADD -> addItem(lastAddedItem!!)
            ActionType.REMOVE -> removeItem(lastRemovedItemPosition!!)
            ActionType.UPDATE -> updateItemAccess(lastUpdatedItemPosition!!)
            null -> _error.value = ERROR_UNKNOWN
        }
    }

    private fun databaseAccess(message: String) {
        if (Random.nextInt(errorFrequency) == 0) {
            throw Exception(message)
        }
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