package com.swahilib.core.database.migrations

import androidx.room.migration.Migration

/**
 * Every [Migration] the app has ever shipped, in ascending version order.
 * Add a new `MigrationNToN+1` class alongside these when bumping the
 * database version, then append it here.
 */
val ALL_MIGRATIONS: Array<Migration> = arrayOf(
    Migration2To3,
    Migration3To4,
    Migration4To5,
    Migration5To6,
)
