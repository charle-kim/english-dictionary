package com.example.englishdictionary.network

import com.example.englishdictionary.model.TranslationResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Free, no-key translation API: https://mymemory.translated.net/
 */
interface TranslationApiService {
    @GET("get")
    suspend fun translate(
        @Query("q") text: String,
        @Query("langpair") langPair: String = "en|ko"
    ): TranslationResponse
}
