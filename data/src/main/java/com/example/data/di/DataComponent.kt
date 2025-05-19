package com.example.data.di

import android.content.Context
import com.example.domain.di.DomainComponent
import com.example.domain.repository.LibraryRepository
import dagger.BindsInstance
import dagger.Component

@DataScope
@Component(dependencies = [DomainComponent::class], modules = [DataModule::class])
interface DataComponent {
    fun provideNetworkManager(): NetworkManager
    fun provideDatabaseManager(): DatabaseManager
    fun provideLibraryRepository(): LibraryRepository

    @Component.Builder
    interface Builder {
        fun domainComponent(component: DomainComponent): Builder

        @BindsInstance
        fun context(context: Context): Builder

        fun build(): DataComponent
    }
}