package com.example.domain.repository


import com.example.domain.entities.LibraryItem
import kotlinx.coroutines.flow.StateFlow

interface LibraryRepository {
    val userPreferencesRepository: UserPreferencesRepository
    val items: StateFlow<List<LibraryItem>>
    val isLoadingMore: StateFlow<Boolean>

    val getCurrentOffset: Int
    val getCurrentApiOffset: Int

    suspend fun loadLocalItems()
    suspend fun loadMoreLocal(isForward: Boolean)
    suspend fun addItem(item: LibraryItem): Int
    suspend fun addItemToLocal(item: LibraryItem): Boolean
    suspend fun removeItem(position: Int)
    suspend fun updateItemAccess(position: Int): Boolean
    suspend fun isIdExists(id: Int): Boolean

    suspend fun loadApiItems(title: String, author: String)
    suspend fun loadMoreApi(title: String, author: String, isForward: Boolean)

    fun clearItems()
    fun initRepository()
}