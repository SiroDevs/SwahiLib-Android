package com.swahilib.core.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/** Creates the `daily_content` table used for the daily word/proverb highlight. */
object Migration3To4 : Migration(3, 4) {
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
