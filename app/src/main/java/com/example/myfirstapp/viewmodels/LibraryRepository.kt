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

    private val errorFrequency = 5

    suspend fun loadItems() {
        val result = withContext(Dispatchers.IO) {
            delay(Random.nextLong(100, 2000))
            databaseAccess(ERROR_DATABASE_LOAD)
            LibraryData.items.sortedBy { it.id }
        }
        _items.value = result
    }

    suspend fun addItem(item: LibraryItem) : Int {
        val result = withContext(Dispatchers.IO) {
            databaseAccess(ERROR_DATABASE_ADD)
            val oldList = _items.value.toMutableList()
            oldList.add(item)
            oldList.sortedBy { it.id }
        }
        _items.value = result
        return getItemPosition(item)
    }

    suspend fun removeItem(position: Int) {
        val result = withContext(Dispatchers.IO) {
            databaseAccess(ERROR_DATABASE_REMOVE)
            val oldList = _items.value.toMutableList()
            oldList.removeAt(position)
            oldList.sortedBy { it.id }
        }
        _items.value = result
    }

    suspend fun updateItemAccess(position: Int) : Boolean {
        val result = withContext(Dispatchers.IO) {
            databaseAccess(ERROR_DATABASE_UPDATE)
            val oldList = _items.value.toMutableList()
            val newAccess = !oldList[position].access
            oldList[position].access = newAccess
            oldList
        }
        _items.value = result
        return true
    }


    fun isIdExists(id: Int): Boolean {
        return _items.value.any { it.id == id }
    }

    fun getItemPosition(item: LibraryItem?): Int {
        return _items.value.indexOfFirst { it.id == item?.id }
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