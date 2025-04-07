package com.example.myfirstapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.myfirstapp.library.LibraryItem

class MainViewModel(private val repository: LibraryRepository): ViewModel() {
    val items: LiveData<List<LibraryItem>> = repository.items
    fun addItem(item: LibraryItem) = repository.addItem(item)
    fun removeItem(position: Int) = repository.removeItem(position)
}