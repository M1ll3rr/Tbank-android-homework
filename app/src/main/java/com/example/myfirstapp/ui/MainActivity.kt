package com.example.myfirstapp.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.data.repository.LibraryRepositoryImpl
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
import com.example.myfirstapp.R
import com.example.myfirstapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val navController by lazy {
        (supportFragmentManager.findFragmentById(R.id.mainFragmentContainer) as NavHostFragment).navController
    }
    private val appRepository  by lazy { LibraryRepositoryImpl(this) }

    private val libraryUseCases by lazy {
        LibraryUseCases(
            observeItems = ObserveItems(appRepository),
            getLocalItems = GetLocalItems(appRepository),
            loadMoreLocal = LoadMoreLocal(appRepository),
            addItem = AddItem(appRepository),
            addItemToLocal = AddItemToLocal(appRepository),
            removeItem = RemoveItem(appRepository),
            updateItemAccess = UpdateItemAccess(appRepository),
            checkIdExists = CheckIdExists(appRepository),
            searchBooks = SearchBooks(appRepository),
            loadMoreApi = LoadMoreApi(appRepository),
            clearLibrary = ClearLibrary(appRepository),
            initializeLibrary = InitializeLibrary(appRepository),
            getCurrentOffset = GetCurrentOffset(appRepository),
            getCurrentApiOffset = GetCurrentApiOffset(appRepository)
        )
    }
    private val preferencesUseCases by lazy {
        PreferencesUseCases(
            observeSortType = ObserveSortType(appRepository.userPreferencesRepository),
            getCurrentSortType = GetCurrentSortType(appRepository.userPreferencesRepository),
            saveSortType = SaveSortType(appRepository.userPreferencesRepository)
        )
    }
    val getLibraryUseCases: LibraryUseCases
        get() = libraryUseCases
    val getPreferencesUseCases: PreferencesUseCases
        get() = preferencesUseCases


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }

}