package com.example.myfirstapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfirstapp.library.LibraryItem
import com.example.myfirstapp.viewmodels.LibraryRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(private val repository: LibraryRepository): ViewModel() {
    val items: StateFlow<List<LibraryItem>> = repository.items
    val isLoading: StateFlow<Boolean> = repository.isLoading
    val error: StateFlow<String?> = repository.error

    private var scrollPosition: Int = 0
    private var isDataLoaded = false

    val getIsDataLoaded: Boolean
        get() = isDataLoaded

    val lastActionType: LibraryRepository.ActionType?
        get() = repository.getLastAction

    val lastUpdatedPosition: Int?
        get() = repository.getLastUpdatedPosition

    val getScrollPosition: Int
        get() = scrollPosition

    fun setScrollPosition(position: Int) {
        scrollPosition = position
    }

    fun loadItems() {
        if (!isDataLoaded) {
            viewModelScope.launch {
                repository.loadItems()
                isDataLoaded = error.value == null
            }
        }
    }

    fun removeItem(position: Int) {
        viewModelScope.launch {
            repository.removeItem(position)
        }
    }

    fun repeatLastAction() {
        viewModelScope.launch {
            repository.repeatLastAction()
        }
    }

    fun setNewItemScrollPosition() {
        scrollPosition = repository.getItemPosition(repository.getLastAddedItem)
    }

    fun clearError() = repository.clearError()
}