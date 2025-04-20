package com.example.myfirstapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfirstapp.library.LibraryItem
import com.example.myfirstapp.viewmodels.LibraryRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ItemViewModel(private val repository: LibraryRepository) : ViewModel() {
    val items: StateFlow<List<LibraryItem>> = repository.items

    fun isIdExists(id: Int): Boolean = repository.isIdExists(id)

    fun updateItemAccess(position: Int) {
        viewModelScope.launch {
            repository.updateItemAccess(position)
        }
    }

    suspend fun addItem(item: LibraryItem) {
        repository.addItem(item)
    }
}