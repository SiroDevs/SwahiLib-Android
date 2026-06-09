package com.swahilib.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.swahilib.core.database.daos.HistoryDao
import com.swahilib.core.database.daos.IdiomDao
import com.swahilib.core.database.daos.ProverbDao
import com.swahilib.core.database.daos.SayingDao
import com.swahilib.core.database.daos.SearchDao
import com.swahilib.core.database.daos.WordDao
import com.swahilib.core.database.model.HistoryEntity
import com.swahilib.core.database.model.IdiomEntity
import com.swahilib.core.database.model.ProverbEntity
import com.swahilib.core.database.model.SayingEntity
import com.swahilib.core.database.model.SearchEntity
import com.swahilib.core.database.model.WordEntity

@Database(
    entities = [
        HistoryEntity::class,
        IdiomEntity::class,
        ProverbEntity::class,
        SayingEntity::class,
        SearchEntity::class,
        WordEntity::class,
    ],
    version = 3,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historiesDao(): HistoryDao
    abstract fun idiomsDao(): IdiomDao
    abstract fun proverbsDao(): ProverbDao
    abstract fun sayingsDao(): SayingDao
    abstract fun searchesDao(): SearchDao
    abstract fun wordsDao(): WordDao

    companion object {
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE words ADD COLUMN english TEXT")
            }
        }

        @Volatile private var widgetInstance: AppDatabase? = null

        fun getInstanceForWidget(context: Context): AppDatabase =
            widgetInstance ?: synchronized(this) {
                widgetInstance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "SwahiliLibrary"
                )
                    .addMigrations(MIGRATION_2_3)
                    .build()
                    .also { widgetInstance = it }
            }
    }
}
