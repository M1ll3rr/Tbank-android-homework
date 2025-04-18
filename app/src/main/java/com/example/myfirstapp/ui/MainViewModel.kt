package com.example.myfirstapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.myfirstapp.library.LibraryItem
import com.example.myfirstapp.viewmodels.LibraryRepository

class MainViewModel(private val repository: LibraryRepository): ViewModel() {
    val items: LiveData<List<LibraryItem>> = repository.items
    fun removeItem(position: Int) = repository.removeItem(position)
}