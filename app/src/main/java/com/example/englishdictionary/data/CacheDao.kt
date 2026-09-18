package com.example.englishdictionary.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CacheDao {

    @Query("SELECT * FROM cached_words WHERE word = :word LIMIT 1")
    suspend fun get(word: String): CachedWord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(word: CachedWord)
}
