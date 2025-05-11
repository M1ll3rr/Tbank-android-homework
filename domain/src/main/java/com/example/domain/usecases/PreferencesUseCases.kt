package com.example.domain.usecases

import com.example.domain.repository.UserPreferencesRepository
import com.example.domain.types.SortType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveSortType @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    operator fun invoke(): Flow<SortType> = repository.sortTypeFlow
}

class GetCurrentSortType @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    operator fun invoke(): Flow<SortType> = repository.getSortType()
}

class SaveSortType @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke(sortType: SortType) = repository.saveSortType(sortType)
}