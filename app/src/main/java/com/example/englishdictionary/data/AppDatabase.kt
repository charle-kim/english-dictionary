package com.example.englishdictionary.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [FavoriteWord::class, CachedWord::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun favoriteDao(): FavoriteDao
    abstract fun cacheDao(): CacheDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "dictionary_db"
                )
                    // 즐겨찾기 스키마에 캐시 테이블이 새로 추가되어 버전이 올라갔습니다.
                    // 별도 마이그레이션 없이 기존 로컬 DB를 새로 만듭니다(즐겨찾기 재설치 시 초기화될 수 있음).
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
