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
import com.swahilib.core.database.daos.DailyContentDao
import com.swahilib.core.database.model.HistoryEntity
import com.swahilib.core.database.model.IdiomEntity
import com.swahilib.core.database.model.ProverbEntity
import com.swahilib.core.database.model.SayingEntity
import com.swahilib.core.database.model.SearchEntity
import com.swahilib.core.database.model.WordEntity
import com.swahilib.core.database.model.DailyContentEntity

@Database(
    entities = [
        HistoryEntity::class,
        IdiomEntity::class,
        ProverbEntity::class,
        SayingEntity::class,
        SearchEntity::class,
        WordEntity::class,
        DailyContentEntity::class,
    ],
    version = 4,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historiesDao(): HistoryDao
    abstract fun idiomsDao(): IdiomDao
    abstract fun proverbsDao(): ProverbDao
    abstract fun sayingsDao(): SayingDao
    abstract fun searchesDao(): SearchDao
    abstract fun wordsDao(): WordDao
    abstract fun dailyContentDao(): DailyContentDao

    companion object {
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE words ADD COLUMN english TEXT")
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `daily_content` (
                        `id` INTEGER NOT NULL,
                        `date` TEXT NOT NULL,
                        `wordRid` INTEGER NOT NULL,
                        `wordMeaning` TEXT NOT NULL,
                        `proverbRid` INTEGER NOT NULL,
                        `proverbMeaning` TEXT NOT NULL,
                        PRIMARY KEY(`id`)
                    )
                    """.trimIndent()
                )
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {

                db.execSQL("ALTER TABLE words RENAME TO words_old")
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `words` (
                        `rid` INTEGER NOT NULL,
                        `title` TEXT,
                        `synonyms` TEXT,
                        `meaning` TEXT,
                        `conjugation` TEXT,
                        `english` TEXT,
                        `liked` INTEGER NOT NULL DEFAULT 0,
                        PRIMARY KEY(`rid`)
                    )
                """.trimIndent()
                )
                db.execSQL(
                    """
                    INSERT INTO words (rid, title, synonyms, meaning, conjugation, english, liked)
                    SELECT rid, title, synonyms, meaning, conjugation, english, liked
                    FROM words_old
                """.trimIndent()
                )
                db.execSQL("DROP TABLE words_old")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_words_rid` ON `words` (`rid`)")

                db.execSQL("ALTER TABLE idioms RENAME TO idioms_old")
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `idioms` (
                        `rid` INTEGER NOT NULL,
                        `title` TEXT,
                        `meaning` TEXT,
                        `liked` INTEGER NOT NULL DEFAULT 0,
                        PRIMARY KEY(`rid`)
                    )
                """.trimIndent()
                )
                db.execSQL(
                    """
                    INSERT INTO idioms (rid, title, meaning, liked)
                    SELECT rid, title, meaning, liked FROM idioms_old
                """.trimIndent()
                )
                db.execSQL("DROP TABLE idioms_old")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_idioms_rid` ON `idioms` (`rid`)")

                db.execSQL("ALTER TABLE proverbs RENAME TO proverbs_old")
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `proverbs` (
                        `rid` INTEGER NOT NULL,
                        `title` TEXT,
                        `synonyms` TEXT,
                        `meaning` TEXT,
                        `conjugation` TEXT,
                        `liked` INTEGER NOT NULL DEFAULT 0,
                        PRIMARY KEY(`rid`)
                    )
                """.trimIndent()
                )
                db.execSQL(
                    """
                    INSERT INTO proverbs (rid, title, synonyms, meaning, conjugation, liked)
                    SELECT rid, title, synonyms, meaning, conjugation, liked FROM proverbs_old
                """.trimIndent()
                )
                db.execSQL("DROP TABLE proverbs_old")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_proverbs_rid` ON `proverbs` (`rid`)")

                db.execSQL("ALTER TABLE sayings RENAME TO sayings_old")
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `sayings` (
                        `rid` INTEGER NOT NULL,
                        `title` TEXT,
                        `meaning` TEXT,
                        `liked` INTEGER NOT NULL DEFAULT 0,
                        PRIMARY KEY(`rid`)
                    )
                """.trimIndent()
                )
                db.execSQL(
                    """
                    INSERT INTO sayings (rid, title, meaning, liked)
                    SELECT rid, title, meaning, liked FROM sayings_old
                """.trimIndent()
                )
                db.execSQL("DROP TABLE sayings_old")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_sayings_rid` ON `sayings` (`rid`)")
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
                    .addMigrations(MIGRATION_2_3, MIGRATION_3_4)
                    .build()
                    .also { widgetInstance = it }
            }
    }
}
