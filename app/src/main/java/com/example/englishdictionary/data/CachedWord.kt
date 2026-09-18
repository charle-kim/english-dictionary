package com.example.englishdictionary.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A previously looked-up word, stored locally so a repeat search doesn't need
 * to hit the network again.
 */
@Entity(tableName = "cached_words")
data class CachedWord(
    @PrimaryKey val word: String,
    val phonetic: String?,
    val partOfSpeech: String?,
    val definitionEn: String,
    val example: String?,
    val koreanMeaning: String,
    val timestamp: Long = System.currentTimeMillis()
)
