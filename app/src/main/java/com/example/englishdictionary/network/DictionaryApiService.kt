package com.example.englishdictionary.network

import com.example.englishdictionary.model.DictionaryEntry
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Free, no-key dictionary API: https://dictionaryapi.dev/
 */
interface DictionaryApiService {
    @GET("api/v2/entries/en/{word}")
    suspend fun getDefinition(@Path("word") word: String): List<DictionaryEntry>
}
