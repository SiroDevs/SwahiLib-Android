package com.swahilib.data.sources.local

import android.content.Context
import androidx.room.*
import com.swahilib.data.models.*

@Database(entities = [Word::class, Song::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun booksDao(): BookDao
    abstract fun songsDao(): SongDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(
                            context.applicationContext,
                            AppDatabase::class.java, "SwahiLib"
                        ).fallbackToDestructiveMigration().build()
                    }
                }
            }
            return INSTANCE
        }
    }
}