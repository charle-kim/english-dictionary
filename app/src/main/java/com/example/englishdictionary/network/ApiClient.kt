package com.example.englishdictionary.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    val dictionaryApi: DictionaryApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.dictionaryapi.dev/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DictionaryApiService::class.java)
    }

    val translationApi: TranslationApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.mymemory.translated.net/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TranslationApiService::class.java)
    }
}
