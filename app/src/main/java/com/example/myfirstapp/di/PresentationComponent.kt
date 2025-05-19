package com.example.myfirstapp.di

import com.example.data.di.DataComponent
import com.example.domain.usecases.LibraryUseCases
import com.example.domain.usecases.PreferencesUseCases
import com.example.myfirstapp.ui.MainActivity
import dagger.Component

@PresentationScope
@Component(dependencies = [DataComponent::class], modules = [PresentationModule::class])
interface PresentationComponent {
    fun inject(activity: MainActivity)
    fun provideLibraryUseCases(): LibraryUseCases
    fun providePreferencesUseCases(): PreferencesUseCases
}