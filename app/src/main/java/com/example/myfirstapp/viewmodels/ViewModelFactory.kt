package com.example.myfirstapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.domain.usecases.LibraryUseCases
import com.example.domain.usecases.PreferencesUseCases

class ViewModelFactory(private val libraryUseCases: LibraryUseCases,
                       private val preferencesUseCases: PreferencesUseCases? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) ->
                MainViewModel(libraryUseCases, preferencesUseCases!!) as T
            modelClass.isAssignableFrom(ItemViewModel::class.java) ->
                ItemViewModel(libraryUseCases) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}