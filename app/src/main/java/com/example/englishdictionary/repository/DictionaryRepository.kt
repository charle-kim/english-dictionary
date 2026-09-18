package com.example.englishdictionary.repository

import android.util.Log
<<<<<<< HEAD
import com.example.englishdictionary.data.CacheDao
import com.example.englishdictionary.data.CachedWord
=======
import com.example.englishdictionary.model.DictionaryEntry
>>>>>>> a7afefab511e6bd94175d6d85e386c0e669d380f
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
