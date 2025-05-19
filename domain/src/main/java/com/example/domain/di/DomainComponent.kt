package com.example.domain.di

import dagger.Component

@DomainScope
@Component(modules = [DomainModule::class])
interface DomainComponent {

}