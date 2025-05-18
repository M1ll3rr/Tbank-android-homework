package com.example.domain.usecases

import com.example.domain.entities.LibraryItem
import com.example.domain.repository.LibraryRepository
import kotlinx.coroutines.flow.StateFlow

class ObserveItems(
    private val repository: LibraryRepository
) {
    operator fun invoke(): StateFlow<List<LibraryItem>> = repository.items
}

class GetLocalItems(
    private val repository: LibraryRepository
) {
    suspend operator fun invoke() = repository.loadLocalItems()
}

class LoadMoreLocal(
    private val repository: LibraryRepository
) {
    suspend operator fun invoke(isForward: Boolean) = repository.loadMoreLocal(isForward)
}

class AddItem(
    private val repository: LibraryRepository
) {
    suspend operator fun invoke(item: LibraryItem) = repository.addItem(item)
}

class AddItemToLocal(
    private val repository: LibraryRepository
) {
    suspend operator fun invoke(item: LibraryItem) = repository.addItemToLocal(item)
}

class RemoveItem(
    private val repository: LibraryRepository
) {
    suspend operator fun invoke(position: Int) = repository.removeItem(position)
}

class UpdateItemAccess(
    private val repository: LibraryRepository
) {
    suspend operator fun invoke(position: Int) = repository.updateItemAccess(position)
}

class CheckIdExists(
    private val repository: LibraryRepository
) {
    suspend operator fun invoke(id: Int) = repository.isIdExists(id)
}

class SearchBooks(
    private val repository: LibraryRepository
) {
    suspend operator fun invoke(title: String, author: String) = repository.loadApiItems(title, author)
}

class LoadMoreApi(
    private val repository: LibraryRepository
) {
    suspend operator fun invoke(title: String, author: String, isForward: Boolean) =
        repository.loadMoreApi(title, author, isForward)
}

class ClearLibrary(
    private val repository: LibraryRepository
) {
    operator fun invoke() = repository.clearItems()
}

class InitializeLibrary(
    private val repository: LibraryRepository
) {
    operator fun invoke() = repository.initRepository()
}

class GetCurrentOffset(
    private val repository: LibraryRepository
) {
    operator fun invoke(): Int = repository.getCurrentOffset
}

class GetCurrentApiOffset(
    private val repository: LibraryRepository
) {
    operator fun invoke(): Int = repository.getCurrentApiOffset
}

data class LibraryUseCases(
    val observeItems: ObserveItems,
    val getLocalItems: GetLocalItems,
    val loadMoreLocal: LoadMoreLocal,
    val addItem: AddItem,
    val addItemToLocal: AddItemToLocal,
    val removeItem: RemoveItem,
    val updateItemAccess: UpdateItemAccess,
    val checkIdExists: CheckIdExists,
    val searchBooks: SearchBooks,
    val loadMoreApi: LoadMoreApi,
    val clearLibrary: ClearLibrary,
    val initializeLibrary: InitializeLibrary,
    val getCurrentOffset: GetCurrentOffset,
    val getCurrentApiOffset: GetCurrentApiOffset
)