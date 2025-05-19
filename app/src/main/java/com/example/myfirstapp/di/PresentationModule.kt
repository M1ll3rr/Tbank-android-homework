package com.example.myfirstapp.di

import android.app.Application
import com.example.domain.repository.LibraryRepository
import com.example.domain.usecases.AddItem
import com.example.domain.usecases.AddItemToLocal
import com.example.domain.usecases.CheckIdExists
import com.example.domain.usecases.ClearLibrary
import com.example.domain.usecases.GetCurrentApiOffset
import com.example.domain.usecases.GetCurrentOffset
import com.example.domain.usecases.GetCurrentSortType
import com.example.domain.usecases.GetLocalItems
import com.example.domain.usecases.InitializeLibrary
import com.example.domain.usecases.LibraryUseCases
import com.example.domain.usecases.LoadMoreApi
import com.example.domain.usecases.LoadMoreLocal
import com.example.domain.usecases.ObserveItems
import com.example.domain.usecases.ObserveSortType
import com.example.domain.usecases.PreferencesUseCases
import com.example.domain.usecases.RemoveItem
import com.example.domain.usecases.SaveSortType
import com.example.domain.usecases.SearchBooks
import com.example.domain.usecases.UpdateItemAccess
import dagger.Module
import dagger.Provides

@Module
class PresentationModule(private val application: Application) {
    @Provides
    @PresentationScope
    fun provideApplication(): Application {
        return application
    }

    @Provides
    @PresentationScope
    fun provideLibraryUseCases(
        libraryRepository: LibraryRepository
    ): LibraryUseCases {
        return LibraryUseCases(
            observeItems = ObserveItems(libraryRepository),
            getLocalItems = GetLocalItems(libraryRepository),
            loadMoreLocal = LoadMoreLocal(libraryRepository),
            addItem = AddItem(libraryRepository),
            addItemToLocal = AddItemToLocal(libraryRepository),
            removeItem = RemoveItem(libraryRepository),
            updateItemAccess = UpdateItemAccess(libraryRepository),
            checkIdExists = CheckIdExists(libraryRepository),
            searchBooks = SearchBooks(libraryRepository),
            loadMoreApi = LoadMoreApi(libraryRepository),
            clearLibrary = ClearLibrary(libraryRepository),
            initializeLibrary = InitializeLibrary(libraryRepository),
            getCurrentOffset = GetCurrentOffset(libraryRepository),
            getCurrentApiOffset = GetCurrentApiOffset(libraryRepository)
        )
    }

    @Provides
    @PresentationScope
    fun providePreferencesUseCases(
        libraryRepository: LibraryRepository
    ): PreferencesUseCases {
        val userPrefs = libraryRepository.userPreferencesRepository
        return PreferencesUseCases(
            observeSortType = ObserveSortType(userPrefs),
            getCurrentSortType = GetCurrentSortType(userPrefs),
            saveSortType = SaveSortType(userPrefs)
        )
    }
}