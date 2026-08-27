package com.swahilib.core.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/** Adds one table per Library collection, plus the punctuation usage child table + FK. */
object Migration6To7 : Migration(6, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
                    CREATE TABLE IF NOT EXISTS `family` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `rid` TEXT NOT NULL,
                        `title` TEXT NOT NULL,
                        `meaning` TEXT,
                        `orderIndex` INTEGER NOT NULL
                    )
                    """.trimIndent()
        )
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_family_rid` ON `family` (`rid`)")

        db.execSQL(
            """
                    CREATE TABLE IF NOT EXISTS `caps` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `rid` TEXT NOT NULL,
                        `title` TEXT NOT NULL,
                        `meaning` TEXT,
                        `orderIndex` INTEGER NOT NULL
                    )
                    """.trimIndent()
        )
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_caps_rid` ON `caps` (`rid`)")

        db.execSQL(
            """
                    CREATE TABLE IF NOT EXISTS `fish` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `rid` TEXT NOT NULL,
                        `title` TEXT NOT NULL,
                        `orderIndex` INTEGER NOT NULL
                    )
                    """.trimIndent()
        )
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_fish_rid` ON `fish` (`rid`)")

        db.execSQL(
            """
                    CREATE TABLE IF NOT EXISTS `insects` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `rid` TEXT NOT NULL,
                        `category` TEXT NOT NULL,
                        `title` TEXT NOT NULL,
                        `orderIndex` INTEGER NOT NULL
                    )
                    """.trimIndent()
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_insects_category` ON `insects` (`category`)")

        db.execSQL(
            """
                    CREATE TABLE IF NOT EXISTS `seas` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `rid` TEXT NOT NULL,
                        `title` TEXT NOT NULL,
                        `size` TEXT,
                        `depth` TEXT,
                        `orderIndex` INTEGER NOT NULL
                    )
                    """.trimIndent()
        )
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_seas_rid` ON `seas` (`rid`)")

        db.execSQL(
            """
                    CREATE TABLE IF NOT EXISTS `kid_games` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `rid` TEXT NOT NULL,
                        `title` TEXT NOT NULL,
                        `meaning` TEXT,
                        `reason` TEXT,
                        `orderIndex` INTEGER NOT NULL
                    )
                    """.trimIndent()
        )
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_kid_games_rid` ON `kid_games` (`rid`)")

        db.execSQL(
            """
                    CREATE TABLE IF NOT EXISTS `greetings` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `rid` TEXT NOT NULL,
                        `greeting` TEXT NOT NULL,
                        `answer` TEXT,
                        `person1` TEXT,
                        `person2` TEXT,
                        `time` TEXT,
                        `orderIndex` INTEGER NOT NULL
                    )
                    """.trimIndent()
        )
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_greetings_rid` ON `greetings` (`rid`)")

        db.execSQL(
            """
                    CREATE TABLE IF NOT EXISTS `countries` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `rid` TEXT NOT NULL,
                        `continent` TEXT NOT NULL,
                        `countries` TEXT NOT NULL,
                        `english` TEXT,
                        `nationality` TEXT,
                        `capital` TEXT,
                        `language` TEXT,
                        `currency` TEXT,
                        `currCode` TEXT,
                        `code` TEXT,
                        `orderIndex` INTEGER NOT NULL
                    )
                    """.trimIndent()
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_countries_continent` ON `countries` (`continent`)")

        db.execSQL(
            """
                    CREATE TABLE IF NOT EXISTS `punctuation` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `rid` TEXT NOT NULL,
                        `sign` TEXT NOT NULL,
                        `title` TEXT NOT NULL,
                        `orderIndex` INTEGER NOT NULL
                    )
                    """.trimIndent()
        )
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_punctuation_rid` ON `punctuation` (`rid`)")

        db.execSQL(
            """
                    CREATE TABLE IF NOT EXISTS `punctuation_usage` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `punctuationId` INTEGER NOT NULL,
                        `usage` TEXT NOT NULL,
                        `example` TEXT,
                        `orderIndex` INTEGER NOT NULL,
                        FOREIGN KEY(`punctuationId`) REFERENCES `punctuation`(`id`) ON DELETE CASCADE
                    )
                    """.trimIndent()
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_punctuation_usage_punctuationId` ON `punctuation_usage` (`punctuationId`)")
    }
}