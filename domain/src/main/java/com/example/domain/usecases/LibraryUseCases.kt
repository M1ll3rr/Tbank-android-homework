package com.example.domain.usecases

import com.example.domain.entities.LibraryItem
import com.example.domain.repository.LibraryRepository
import javax.inject.Inject

class GetLocalItems @Inject constructor(
    private val repository: LibraryRepository
) {
    suspend operator fun invoke() = repository.loadLocalItems()
}

class LoadMoreLocal @Inject constructor(
    private val repository: LibraryRepository
) {
    suspend operator fun invoke(isForward: Boolean) = repository.loadMoreLocal(isForward)
}

class AddItem @Inject constructor(
    private val repository: LibraryRepository
) {
    suspend operator fun invoke(item: LibraryItem) = repository.addItem(item)
}

class RemoveItem @Inject constructor(
    private val repository: LibraryRepository
) {
    suspend operator fun invoke(position: Int) = repository.removeItem(position)
}

class UpdateItemAccess @Inject constructor(
    private val repository: LibraryRepository
) {
    suspend operator fun invoke(position: Int) = repository.updateItemAccess(position)
}

class CheckIdExists @Inject constructor(
    private val repository: LibraryRepository
) {
    suspend operator fun invoke(id: Int) = repository.isIdExists(id)
}

class SearchBooks @Inject constructor(
    private val repository: LibraryRepository
) {
    suspend operator fun invoke(title: String, author: String) = repository.loadApiItems(title, author)
}

class LoadMoreApi @Inject constructor(
    private val repository: LibraryRepository
) {
    suspend operator fun invoke(title: String, author: String, isForward: Boolean) =
        repository.loadMoreApi(title, author, isForward)
}

class ClearLibrary @Inject constructor(
    private val repository: LibraryRepository
) {
    operator fun invoke() = repository.clearItems()
}

class InitializeLibrary @Inject constructor(
    private val repository: LibraryRepository
) {
    operator fun invoke() = repository.initRepository()
}