package com.swahilib.data.sources.local

import androidx.room.*
import com.swahilib.data.models.*
import com.swahilib.data.sources.local.daos.*

@Database(
    entities = [History::class, Idiom::class, Proverb::class, Saying::class, Search::class, Word::class,],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historiesDao(): HistoryDao
    abstract fun idiomsDao(): IdiomDao
    abstract fun proverbsDao(): ProverbDao
    abstract fun sayingsDao(): SayingDao
    abstract fun searchesDao(): SearchDao
    abstract fun wordsDao(): WordDao
}