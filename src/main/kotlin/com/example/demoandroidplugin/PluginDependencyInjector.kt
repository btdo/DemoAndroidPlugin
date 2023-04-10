package com.example.demoandroidplugin

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

interface PluginDependencyInjector {
    val api: ChatGptApi
    val repository: PluginRepository
}

class PluginDependencyInjectorImpl : PluginDependencyInjector {

    override val api: ChatGptApi by lazy {
        val httpClient =
            OkHttpClient.Builder().connectTimeout(60, TimeUnit.SECONDS).addInterceptor(HttpLoggingInterceptor().apply {
                level =
                    HttpLoggingInterceptor.Level.BODY
            }).build()
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        Retrofit.Builder().client(httpClient)
            .baseUrl("https://api.openai.com/v1/chat/completions/")
            .addConverterFactory(MoshiConverterFactory.create(moshi)).build()
            .create(ChatGptApi::class.java)
    }

    override val repository: PluginRepository by lazy {
        PluginRepositoryImpl(api)
    }
}


