package com.example.domain.usecases

import com.example.domain.repository.UserPreferencesRepository
import com.example.domain.types.SortType
import kotlinx.coroutines.flow.Flow

class ObserveSortType(
    private val repository: UserPreferencesRepository
) {
    operator fun invoke(): Flow<SortType> = repository.sortTypeFlow
}

class GetCurrentSortType(
    private val repository: UserPreferencesRepository
) {
    operator fun invoke(): Flow<SortType> = repository.getSortType()
}

class SaveSortType(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke(sortType: SortType) = repository.saveSortType(sortType)
}

data class PreferencesUseCases(
    val observeSortType: ObserveSortType,
    val getCurrentSortType: GetCurrentSortType,
    val saveSortType: SaveSortType
)