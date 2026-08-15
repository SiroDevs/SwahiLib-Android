package com.swahilib.core.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/** Adds per-game level progress and resumable session-state tables. */
object Migration5To6 : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `game_level_progress` (
                `gameType` TEXT NOT NULL,
                `highestUnlockedLevel` INTEGER NOT NULL,
                `totalPoints` INTEGER NOT NULL,
                `updatedAt` INTEGER NOT NULL,
                PRIMARY KEY(`gameType`)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `game_session_state` (
                `gameType` TEXT NOT NULL,
                `level` INTEGER NOT NULL,
                `contentSeed` INTEGER NOT NULL,
                `stepIndex` INTEGER NOT NULL,
                `livePoints` INTEGER NOT NULL,
                `snapshotJson` TEXT NOT NULL,
                `updatedAt` INTEGER NOT NULL,
                PRIMARY KEY(`gameType`)
            )
            """.trimIndent()
        )
    }
}
