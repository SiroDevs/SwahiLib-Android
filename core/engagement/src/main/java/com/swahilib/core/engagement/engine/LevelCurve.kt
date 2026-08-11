package com.swahilib.core.engagement.engine

import kotlin.math.max
import kotlin.math.sqrt

object LevelCurve {
    private const val BASE = 50.0

    fun xpForLevel(level: Int): Long {
        val n = max(1, level).toLong()
        return (BASE * n * (n - 1)).toLong()
    }

    fun levelForXp(xp: Long): Int {
        if (xp <= 0L) return 1
        val disc = 1.0 + 8.0 * xp / BASE
        val level = (1.0 + sqrt(disc)) / 2.0
        return level.toInt().coerceAtLeast(1)
    }

    fun progressWithin(xp: Long): Pair<Long, Long> {
        val currentLevel = levelForXp(xp)
        val floorXp = xpForLevel(currentLevel)
        val ceilXp = xpForLevel(currentLevel + 1)
        return (xp - floorXp) to (ceilXp - floorXp)
    }
}
