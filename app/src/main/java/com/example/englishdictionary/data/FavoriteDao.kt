package com.example.englishdictionary.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavoriteDao {

    @Query("SELECT * FROM favorite_words ORDER BY timestamp DESC")
    suspend fun getAll(): List<FavoriteWord>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(word: FavoriteWord)

    @Delete
    suspend fun delete(word: FavoriteWord)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_words WHERE word = :word)")
    suspend fun isFavorite(word: String): Boolean
}
