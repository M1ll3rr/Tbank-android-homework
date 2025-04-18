package com.example.myfirstapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.myfirstapp.library.LibraryItem
import com.example.myfirstapp.viewmodels.LibraryRepository

class ItemViewModel(private val repository: LibraryRepository) : ViewModel() {
    val items: LiveData<List<LibraryItem>> = repository.items
    fun isIdExists(id: Int): Boolean = repository.isIdExists(id)
    fun updateItemAccess(position: Int, newAccess: Boolean) = repository.updateItemAccess(position, newAccess)
    fun addItem(item: LibraryItem) = repository.addItem(item)
    fun getNewItemPosition(item: LibraryItem) = repository.getNewItemPosition(item)
}
