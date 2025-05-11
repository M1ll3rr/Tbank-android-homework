package com.example.myfirstapp.viewmodels

import androidx.lifecycle.ViewModel
import com.example.data.repository.LibraryRepositoryImpl
import com.example.domain.entities.LibraryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.coroutines.cancellation.CancellationException

class ItemViewModel(private val repository: LibraryRepositoryImpl) : ViewModel() {
    val items: StateFlow<List<LibraryItem>> = repository.items

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private var _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    suspend fun isIdExists(id: Int): Boolean = repository.isIdExists(id)

    suspend fun updateItemAccess(position: Int) : Boolean {
        try {
            _error.value = null
            _isLoading.value = true
            val result = repository.updateItemAccess(position)
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
            return repository.addItem(item)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            _error.value = e.message
            return -1
        } finally {
            _isLoading.value = false
        }
    }
}