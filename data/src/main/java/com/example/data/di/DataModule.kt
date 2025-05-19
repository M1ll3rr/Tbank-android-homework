package com.example.data.di

import android.content.Context
import com.example.data.repository.LibraryRepositoryImpl
import com.example.domain.repository.LibraryRepository
import dagger.Module
import dagger.Provides

@Module
class DataModule {
    @Provides
    @DataScope
    fun provideNetworkManager(): NetworkManager {
        return NetworkManager()
    }

    @Provides
    @DataScope
    fun provideDatabaseManager(context: Context): DatabaseManager {
        return DatabaseManager(context)
    }

    @Provides
    @DataScope
    fun provideLibraryRepository(context: Context): LibraryRepository {
        return LibraryRepositoryImpl(context)
    }
}