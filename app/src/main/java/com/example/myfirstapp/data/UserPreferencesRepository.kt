package com.example.myfirstapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class UserPreferencesRepository(context: Context) {
    private val dataStore = context.dataStore

    companion object {
        private val SORT_TYPE_KEY = intPreferencesKey("sort_type")
    }

    val sortTypeFlow: Flow<SortType> = dataStore.data
        .map { preferences ->
            val index = preferences[SORT_TYPE_KEY] ?: SortType.BY_NAME.ordinal
            SortType.entries[index]
        }

    suspend fun saveSortType(sortType: SortType) {
        dataStore.edit { preferences ->
            preferences[SORT_TYPE_KEY] = sortType.ordinal
        }
    }


}