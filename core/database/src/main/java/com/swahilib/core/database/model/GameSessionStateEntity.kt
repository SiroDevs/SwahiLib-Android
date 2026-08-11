package com.swahilib.core.database.model

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Autosaved "mwendelezo" (progress/continuation) for one in-flight game
 * session, keyed by [gameType] alone - a player can only have one
 * in-progress attempt per game at a time, mirroring how "Hifadhi na Rudi
 * Nyuma" (save & go back) is offered per-game, not per-level.
 *
 * [contentSeed] lets every generator (`WordDao`-backed, all deterministic
 * given the same seed) rebuild the identical round/puzzle content on resume
 * without persisting the full generated payload. [snapshotJson] is a small,
 * game-specific `@Serializable` blob (see each feature's `*Snapshot` model)
 * holding whatever mutable progress that game needs to fast-forward back to
 * where the player left off (completed round results, typed answers, found
 * words, etc).
 */
@Keep
@Entity(tableName = "game_session_state")
data class GameSessionStateEntity(
    @PrimaryKey @ColumnInfo(name = "gameType") val gameType: String,
    @ColumnInfo(name = "level") val level: Int,
    @ColumnInfo(name = "contentSeed") val contentSeed: Long,
    @ColumnInfo(name = "stepIndex") val stepIndex: Int,
    @ColumnInfo(name = "livePoints") val livePoints: Int,
    @ColumnInfo(name = "snapshotJson") val snapshotJson: String,
    @ColumnInfo(name = "updatedAt") val updatedAt: Long = 0L,
)
