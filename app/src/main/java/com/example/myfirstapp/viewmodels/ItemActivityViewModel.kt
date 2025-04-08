package com.example.myfirstapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.myfirstapp.library.LibraryItem

class ItemActivityViewModel(private val repository: LibraryRepository) : ViewModel() {
    val items: LiveData<List<LibraryItem>> = repository.items
    fun isIdExists(id: Int): Boolean = repository.isIdExists(id)
    fun updateItemAccess(position: Int, newAccess: Boolean) = repository.updateItemAccess(position, newAccess)
}
