package com.swahilib.core.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.swahilib.core.database.model.XpEventEntity

@Dao
interface XpEventDao {

    @Insert
    suspend fun insert(event: XpEventEntity): Long

    @Query("SELECT COALESCE(SUM(amount), 0) FROM xp_events")
    suspend fun totalXp(): Long

    @Query("SELECT COALESCE(SUM(amount), 0) FROM xp_events WHERE date = :date")
    suspend fun xpOn(date: String): Long

    @Query("SELECT COALESCE(SUM(amount), 0) FROM xp_events WHERE date BETWEEN :from AND :to")
    suspend fun xpBetween(from: String, to: String): Long

    @Query("SELECT * FROM xp_events ORDER BY createdAt DESC LIMIT :limit")
    suspend fun recent(limit: Int = 50): List<XpEventEntity>

    @Query("DELETE FROM xp_events")
    suspend fun deleteAll()
}
