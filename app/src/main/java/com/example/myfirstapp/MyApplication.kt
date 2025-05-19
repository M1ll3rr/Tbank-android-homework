package com.example.myfirstapp

import android.app.Application
import com.example.data.di.DaggerDataComponent
import com.example.data.di.DataComponent
import com.example.domain.di.DaggerDomainComponent
import com.example.domain.di.DomainComponent
import com.example.myfirstapp.di.DaggerPresentationComponent
import com.example.myfirstapp.di.PresentationComponent
import com.example.myfirstapp.di.PresentationModule


class MyApplication : Application() {
    lateinit var presentationComponent: PresentationComponent
        private set

    private lateinit var dataComponent: DataComponent

    private lateinit var domainComponent: DomainComponent

    override fun onCreate() {
        super.onCreate()
        initDagger()
    }

    private fun initDagger() {
        domainComponent = DaggerDomainComponent.builder()
            .build()

        dataComponent = DaggerDataComponent.builder()
            .domainComponent(domainComponent)
            .context(applicationContext)
            .build()

        presentationComponent = DaggerPresentationComponent.builder()
            .dataComponent(dataComponent)
            .presentationModule(PresentationModule(this))
            .build()
    }

    companion object {
        fun from(application: Application): MyApplication {
            return application as MyApplication
        }
    }
}