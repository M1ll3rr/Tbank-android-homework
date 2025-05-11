package com.example.domain.repository

import com.example.domain.types.SortType
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val sortTypeFlow: Flow<SortType>

    fun getSortType(): Flow<SortType>

    suspend fun saveSortType(sortType: SortType)
} 