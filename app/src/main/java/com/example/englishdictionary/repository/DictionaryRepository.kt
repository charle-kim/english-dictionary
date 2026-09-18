package com.example.englishdictionary.repository

import com.example.englishdictionary.model.DictionaryEntry
import com.example.englishdictionary.network.ApiClient
import retrofit2.HttpException

sealed class LookupResult {
    data class Success(
        val entry: DictionaryEntry,
        val koreanMeaning: String
    ) : LookupResult()

    object NotFound : LookupResult()

    data class Error(val message: String) : LookupResult()
}

class DictionaryRepository {

    suspend fun lookup(word: String): LookupResult {
        return try {
            val entries = ApiClient.dictionaryApi.getDefinition(word.trim().lowercase())
            val entry = entries.firstOrNull() ?: return LookupResult.NotFound
            val firstDefinition = entry.meanings?.firstOrNull()?.definitions?.firstOrNull()?.definition
                ?: return LookupResult.NotFound

            val korean = try {
                ApiClient.translationApi.translate(firstDefinition).responseData?.translatedText
                    ?: "번역 결과를 가져올 수 없습니다."
            } catch (e: Exception) {
                "번역 결과를 가져올 수 없습니다."
            }

            LookupResult.Success(entry, korean)
        } catch (e: HttpException) {
            if (e.code() == 404) {
                LookupResult.NotFound
            } else {
                LookupResult.Error("서버 오류가 발생했습니다. (코드 ${e.code()})")
            }
        } catch (e: Exception) {
            LookupResult.Error("네트워크 연결을 확인해주세요.")
        }
    }
}
