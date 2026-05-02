package com.swahilib.data.sources.local

import androidx.room.*
import com.swahilib.data.models.*
import com.swahilib.data.sources.local.daos.*

@Database(
    entities = [
        History::class,
        Idiom::class,
        Proverb::class,
        Saying::class,
        Search::class,
        Word::class,],
    version = 2,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
    abstract fun idiomDao(): IdiomDao
    abstract fun proverbDao(): ProverbDao
    abstract fun sayingDao(): SayingDao
    abstract fun searchDao(): SearchDao
    abstract fun wordDao(): WordDao
}