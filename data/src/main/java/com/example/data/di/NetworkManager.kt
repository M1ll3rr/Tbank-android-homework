package com.example.data.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject


@DataScope
class NetworkManager @Inject constructor() {
    private val BASE_URL = "https://www.googleapis.com/books/v1/"

    private val gson: Gson by lazy {
        GsonBuilder()
            .create()
    }

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    inline fun <reified T> createService(): T {
        return retrofit.create(T::class.java)
    }
}