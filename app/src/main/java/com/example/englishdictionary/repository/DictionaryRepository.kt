package com.example.englishdictionary.repository

import android.util.Log
import com.example.englishdictionary.data.CacheDao
import com.example.englishdictionary.data.CachedWord
import com.example.englishdictionary.network.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

sealed class LookupResult {
    data class Success(
        val word: String,
        val phonetic: String?,
        val partOfSpeech: String?,
        val definitionEn: String,
        val example: String?,
        val koreanMeaning: String
    ) : LookupResult()

    object NotFound : LookupResult()

    data class Error(val message: String) : LookupResult()
}

class DictionaryRepository(private val cacheDao: CacheDao) {

    suspend fun lookup(rawWord: String): LookupResult = withContext(Dispatchers.IO) {
        val word = rawWord.trim().lowercase()
        if (word.isEmpty()) return@withContext LookupResult.NotFound

        // 1) 이미 찾아본 단어라면 캐시에서 바로 반환 (네트워크 호출 없이 즉시 표시)
        cacheDao.get(word)?.let { cached ->
            return@withContext LookupResult.Success(
                word = cached.word,
                phonetic = cached.phonetic,
                partOfSpeech = cached.partOfSpeech,
                definitionEn = cached.definitionEn,
                example = cached.example,
                koreanMeaning = cached.koreanMeaning
            )
        }

        try {
            val entries = ApiClient.dictionaryApi.getDefinition(word)
            val entry = entries.firstOrNull() ?: return@withContext LookupResult.NotFound
            val meaning = entry.meanings?.firstOrNull()
            val definition = meaning?.definitions?.firstOrNull()
            val definitionEn = definition?.definition ?: return@withContext LookupResult.NotFound

            val korean = try {
                ApiClient.translationApi.translate(definitionEn).responseData?.translatedText
                    ?: "번역 결과를 가져올 수 없습니다."
            } catch (e: Exception) {
                Log.e("DictionaryRepository", "translation failed", e)
                "번역 결과를 가져올 수 없습니다."
            }

            val phonetic = entry.phonetic
                ?: entry.phonetics?.firstOrNull { !it.text.isNullOrBlank() }?.text

            val result = LookupResult.Success(
                word = entry.word ?: word,
                phonetic = phonetic,
                partOfSpeech = meaning?.partOfSpeech,
                definitionEn = definitionEn,
                example = definition.example,
                koreanMeaning = korean
            )

            // 2) 다음에 같은 단어를 검색할 때 바로 뜨도록 로컬에 저장
            cacheDao.insert(
                CachedWord(
                    word = word,
                    phonetic = result.phonetic,
                    partOfSpeech = result.partOfSpeech,
                    definitionEn = result.definitionEn,
                    example = result.example,
                    koreanMeaning = result.koreanMeaning
                )
            )

            result
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
