package com.example.myfirstapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private var scrollPosition: Int = 0

    init {
        loadItems()
    }


    val getScrollPosition: Int
        get() = scrollPosition

    fun setScrollPosition(position: Int) {
        scrollPosition = position
    }


    fun loadItems() = viewModelScope.launch {
        var startTime = 0L
        val isLoadingJob = launch {
            val remainingTime = 1000 - (System.currentTimeMillis() - startTime)
            if (remainingTime > 0) delay(remainingTime)
            _isLoading.value = false
        }

        try {
            _error.value = null
            _isLoading.value = true
            startTime = System.currentTimeMillis()
            repository.loadItems()
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