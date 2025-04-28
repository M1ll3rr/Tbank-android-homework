package com.example.myfirstapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfirstapp.data.SortType
import com.example.myfirstapp.library.LibraryItem
import com.example.myfirstapp.viewmodels.LibraryRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class MainViewModel(private val repository: LibraryRepository): ViewModel() {
    val items: StateFlow<List<LibraryItem>> = repository.items

    private var _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    val isLoadingMore: StateFlow<Boolean> = repository.isLoadingMore

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private var scrollPosition: Int = 0
    private val loadMoreThreshold = 10


    init {
        loadDatabase()
    }


    val getScrollPosition: Int
        get() = scrollPosition

    fun setScrollPosition(position: Int) {
        scrollPosition = position
        checkIfNeedToLoadMore(position)
    }

    fun setSortType(sortType: SortType) {
        viewModelScope.launch {
            repository.userPreferencesRepository.saveSortType(sortType)
        }
    }

    private fun checkIfNeedToLoadMore(position: Int) = viewModelScope.launch {
        val itemCount = items.value.size

        if (itemCount - position <= loadMoreThreshold && itemCount > 0) {
            try {
                _error.value = null
                repository.loadMoreItems(isForward = true)
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _error.value = e.message
            }
        }

        if (position in 1..loadMoreThreshold) {
            try {
                _error.value = null
                repository.loadMoreItems(isForward = false)
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _error.value = e.message
            }
        }
    }

    fun loadDatabase() = viewModelScope.launch {
        val isLoadingJob = launch {
            delay(1000)
            _isLoading.value = false
        }

        try {
            _error.value = null
            _isLoading.value = true
            repository.initRepository()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            _error.value = e.message
        } finally {
            isLoadingJob.join()
        }
    }


    fun removeItem(position: Int) = viewModelScope.launch {
        try {
            _error.value = null
            repository.removeItem(position)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            _error.value = e.message
        }
    }
}