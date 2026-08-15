/*
 * Copyright 2026 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.swahilib.core.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/** Adds one table per Maktaba collection, plus the uakifishaji usage child table + FK. */
object Migration6To7 : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
                    CREATE TABLE IF NOT EXISTS `familia` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `rid` TEXT NOT NULL,
                        `title` TEXT NOT NULL,
                        `meaning` TEXT,
                        `orderIndex` INTEGER NOT NULL
                    )
                    """.trimIndent()
        )
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_familia_rid` ON `familia` (`rid`)")

        db.execSQL(
            """
                    CREATE TABLE IF NOT EXISTS `kofia` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `rid` TEXT NOT NULL,
                        `title` TEXT NOT NULL,
                        `meaning` TEXT,
                        `orderIndex` INTEGER NOT NULL
                    )
                    """.trimIndent()
        )
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_kofia_rid` ON `kofia` (`rid`)")

        db.execSQL(
            """
                    CREATE TABLE IF NOT EXISTS `samaki` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `rid` TEXT NOT NULL,
                        `title` TEXT NOT NULL,
                        `orderIndex` INTEGER NOT NULL
                    )
                    """.trimIndent()
        )
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_samaki_rid` ON `samaki` (`rid`)")

        db.execSQL(
            """
                    CREATE TABLE IF NOT EXISTS `wadudu` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `rid` TEXT NOT NULL,
                        `category` TEXT NOT NULL,
                        `title` TEXT NOT NULL,
                        `orderIndex` INTEGER NOT NULL
                    )
                    """.trimIndent()
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_wadudu_category` ON `wadudu` (`category`)")

        db.execSQL(
            """
                    CREATE TABLE IF NOT EXISTS `bahari` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `rid` TEXT NOT NULL,
                        `title` TEXT NOT NULL,
                        `size` TEXT,
                        `depth` TEXT,
                        `orderIndex` INTEGER NOT NULL
                    )
                    """.trimIndent()
        )
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_bahari_rid` ON `bahari` (`rid`)")

        db.execSQL(
            """
                    CREATE TABLE IF NOT EXISTS `michezo_ya_watoto` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `rid` TEXT NOT NULL,
                        `title` TEXT NOT NULL,
                        `meaning` TEXT,
                        `reason` TEXT,
                        `orderIndex` INTEGER NOT NULL
                    )
                    """.trimIndent()
        )
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_michezo_ya_watoto_rid` ON `michezo_ya_watoto` (`rid`)")

        db.execSQL(
            """
                    CREATE TABLE IF NOT EXISTS `salamu` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `rid` TEXT NOT NULL,
                        `salamu` TEXT NOT NULL,
                        `answer` TEXT,
                        `person1` TEXT,
                        `person2` TEXT,
                        `time` TEXT,
                        `orderIndex` INTEGER NOT NULL
                    )
                    """.trimIndent()
        )
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_salamu_rid` ON `salamu` (`rid`)")

        db.execSQL(
            """
                    CREATE TABLE IF NOT EXISTS `nchi` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `rid` TEXT NOT NULL,
                        `continent` TEXT NOT NULL,
                        `nchi` TEXT NOT NULL,
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
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_nchi_continent` ON `nchi` (`continent`)")

        db.execSQL(
            """
                    CREATE TABLE IF NOT EXISTS `uakifishaji` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `rid` TEXT NOT NULL,
                        `sign` TEXT NOT NULL,
                        `title` TEXT NOT NULL,
                        `orderIndex` INTEGER NOT NULL
                    )
                    """.trimIndent()
        )
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_uakifishaji_rid` ON `uakifishaji` (`rid`)")

        db.execSQL(
            """
                    CREATE TABLE IF NOT EXISTS `uakifishaji_usage` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `uakifishajiId` INTEGER NOT NULL,
                        `usage` TEXT NOT NULL,
                        `example` TEXT,
                        `orderIndex` INTEGER NOT NULL,
                        FOREIGN KEY(`uakifishajiId`) REFERENCES `uakifishaji`(`id`) ON DELETE CASCADE
                    )
                    """.trimIndent()
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_uakifishaji_usage_uakifishajiId` ON `uakifishaji_usage` (`uakifishajiId`)")
    }
}