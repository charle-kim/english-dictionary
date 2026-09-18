package com.example.englishdictionary.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_words")
data class FavoriteWord(
    @PrimaryKey val word: String,
    val meaningKo: String,
    val meaningEn: String,
    val timestamp: Long = System.currentTimeMillis()
)
