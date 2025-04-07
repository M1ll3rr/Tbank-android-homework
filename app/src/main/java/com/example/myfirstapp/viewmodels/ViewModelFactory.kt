package com.example.myfirstapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> MainViewModel(LibraryRepositorySingleton.repository) as T
            modelClass.isAssignableFrom(ItemActivityViewModel::class.java) -> ItemActivityViewModel(LibraryRepositorySingleton.repository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}