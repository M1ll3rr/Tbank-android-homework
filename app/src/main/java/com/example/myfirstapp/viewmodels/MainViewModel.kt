package com.example.myfirstapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.entities.LibraryItem
import com.example.domain.types.SortType
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class MainViewModel(private val repository: com.example.data.repository.LibraryRepositoryImpl): ViewModel() {
    val items: StateFlow<List<LibraryItem>> = repository.items

    private var _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private var _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> =  _isLoadingMore

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private var scrollPosition: Int = 0
    private val loadMoreThreshold = 10
    private var lastTitle = ""
    private var lastAuthor = ""

    private var loadJob: Job? = null


    init {
        loadDatabase()
    }

    val getScrollPosition: Int
        get() = scrollPosition

    fun setScrollPosition(position: Int) {
        scrollPosition = position
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            delay(300)
            checkIfNeedToLoadMore(position)
        }
    }

    fun setSortType(sortType: SortType) {
        viewModelScope.launch {
            repository.userPreferencesRepository.saveSortType(sortType)
        }
    }

    private fun checkIfNeedToLoadMore(position: Int) = viewModelScope.launch {

        val isLoadingJob = launch {
            delay(1000)
            _isLoadingMore.value = false
        }
        val itemCount = items.value.size
        if (itemCount - position <= loadMoreThreshold  && itemCount > 0 ) {
            try {
                _error.value = null
                _isLoadingMore.value = true
                if (isLoadMoreApi()) {
                    repository.loadMoreApi(lastTitle, lastAuthor, isForward = true)
                }
                else {
                    repository.loadMoreLocal(isForward = true)
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _error.value = e.message
            } finally {
                isLoadingJob.join()
            }
        }
        else if (position in 0..loadMoreThreshold && (repository.getCurrentOffset != 0 || repository.getCurrentApiOffset != 0)) {
            try {
                _error.value = null
                _isLoadingMore.value = true
                if (isLoadMoreApi()) {
                    repository.loadMoreApi(lastTitle, lastAuthor, isForward = false)
                }
                else {
                    repository.loadMoreLocal(isForward = false)
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _error.value = e.message
            } finally {
                isLoadingJob.join()
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

    fun loadLocalItems() = viewModelScope.launch {
        val isLoadingJob = launch {
            delay(1000)
            _isLoading.value = false
        }

        try {
            _error.value = null
            _isLoading.value = true
            lastTitle = ""
            lastAuthor = ""
            clearItems()
            repository.loadLocalItems()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            _error.value = e.message
        } finally {
            isLoadingJob.join()
        }
    }

    fun searchGoogleBooks(title: String, author: String) = viewModelScope.launch {
        lastTitle = title
        lastAuthor = author
        val isLoadingJob = launch {
            delay(1000)
            _isLoading.value = false
        }

        try {
            _error.value = null
            _isLoading.value = true
            repository.loadApiItems(title, author)
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

    suspend fun addItem(item: LibraryItem) : Boolean {
        return try {
            _error.value = null
            val success = repository.addItemToLocal(item)
            success
        }
        catch (e: Exception) {
            if (e is CancellationException) throw e
            _error.value = e.message
            false
        }
    }

    fun clearItems() = repository.clearItems()

    private fun isLoadMoreApi() : Boolean {
        return lastTitle.isNotEmpty() || lastAuthor.isNotEmpty()
    }
}