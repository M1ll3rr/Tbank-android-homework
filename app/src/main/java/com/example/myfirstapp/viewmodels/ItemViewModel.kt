package com.example.myfirstapp.viewmodels

import androidx.lifecycle.ViewModel
import com.example.domain.entities.LibraryItem
import com.example.domain.usecases.LibraryUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.coroutines.cancellation.CancellationException

class ItemViewModel(private val libraryUseCases: LibraryUseCases) : ViewModel() {
    val items: StateFlow<List<LibraryItem>> = libraryUseCases.observeItems()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private var _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    suspend fun isIdExists(id: Int): Boolean = libraryUseCases.checkIdExists(id)

    suspend fun updateItemAccess(position: Int) : Boolean {
        try {
            _error.value = null
            _isLoading.value = true
            val result = libraryUseCases.updateItemAccess(position)
            return result
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            _error.value = e.message
            return false
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun addItem(item: LibraryItem) : Int {
        try {
            _error.value = null
            _isLoading.value = true
            return libraryUseCases.addItem(item)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            _error.value = e.message
            return -1
        } finally {
            _isLoading.value = false
        }
    }
}