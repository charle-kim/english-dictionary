package com.example.englishdictionary.repository

import android.util.Log
import com.example.englishdictionary.model.DictionaryEntry
import com.example.englishdictionary.network.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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

    suspend fun lookup(word: String): LookupResult = withContext(Dispatchers.IO) {
        try {
            val entries = ApiClient.dictionaryApi.getDefinition(word.trim().lowercase())
            val entry = entries.firstOrNull() ?: return@withContext LookupResult.NotFound
            val firstDefinition = entry.meanings?.firstOrNull()?.definitions?.firstOrNull()?.definition
                ?: return@withContext LookupResult.NotFound

            val korean = try {
                ApiClient.translationApi.translate(firstDefinition).responseData?.translatedText
                    ?: "번역 결과를 가져올 수 없습니다."
            } catch (e: Exception) {
                Log.e("DictionaryRepository", "translation failed", e)
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
            // 원인을 화면에서 바로 확인할 수 있도록 예외 종류와 메시지를 함께 표시합니다.
            Log.e("DictionaryRepository", "lookup failed for word=$word", e)
            LookupResult.Error(
                "네트워크 오류가 발생했습니다.\n[${e.javaClass.simpleName}] ${e.message}"
            )
        }
    }
}
