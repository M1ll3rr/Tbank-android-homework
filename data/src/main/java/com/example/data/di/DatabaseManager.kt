package com.example.data.di

import android.content.Context
import androidx.room.Room
import com.example.data.database.LibraryDatabase
import javax.inject.Inject

@DataScope
class DatabaseManager @Inject constructor(
    private val appContext: Context
) {

    val database: LibraryDatabase by lazy {
        Room.databaseBuilder(
            appContext,
            LibraryDatabase::class.java,
            "library_database"
        ).build()
    }

    fun getLibraryDao() = database.libraryDao()
}