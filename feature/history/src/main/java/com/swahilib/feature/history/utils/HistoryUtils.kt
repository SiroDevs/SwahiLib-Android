package com.swahilib.feature.history.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

sealed class HistoryBucket(val label: String) {
    object Leo : HistoryBucket("Leo")
    object Jana : HistoryBucket("Jana")
    object WikiHii : HistoryBucket("Wiki Hii")
    object MweziHuu : HistoryBucket("Mwezi Huu")
    object Zamani : HistoryBucket("Zamani")
}

/** A flat, display-ready row: either a sticky-header bucket label or a data item. */
sealed class HistoryRow<out T> {
    data class Header(val bucket: HistoryBucket) : HistoryRow<Nothing>()
    data class Item<T>(val data: T, val timestamp: String) : HistoryRow<T>()
}

object HistoryGrouper {

    /** "Sasa hivi", "Dakika 3 zilizopita", or a plain "HH:mm" for anything older. */
    fun relativeTime(epochMillis: Long): String {
        val diffMs = System.currentTimeMillis() - epochMillis
        val diffMin = diffMs / 60_000
        return when {
            diffMs < 60_000 -> "Sasa hivi"
            diffMin == 1L -> "Dakika 1 iliyopita"
            diffMin < 60 -> "Dakika $diffMin zilizopita"
            diffMin < 120 -> "Saa 1 iliyopita"
            else -> SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(epochMillis))
        }
    }

    /**
     * Groups [items] into Leo / Jana / Wiki Hii / Mwezi Huu / Zamani buckets,
     * newest first, with a [HistoryRow.Header] inserted whenever the bucket changes.
     */
    fun <T> group(items: List<T>, epochMillis: (T) -> Long?): List<HistoryRow<T>> {
        val now = Calendar.getInstance()

        fun bucketFor(ms: Long): HistoryBucket {
            val cal = Calendar.getInstance().apply { timeInMillis = ms }
            val daysDiff = daysBetween(cal, now)
            return when {
                daysDiff == 0 -> HistoryBucket.Leo
                daysDiff == 1 -> HistoryBucket.Jana
                daysDiff < 7 -> HistoryBucket.WikiHii
                daysDiff < 30 -> HistoryBucket.MweziHuu
                else -> HistoryBucket.Zamani
            }
        }

        val sorted = items.sortedByDescending { epochMillis(it) ?: 0L }
        val rows = mutableListOf<HistoryRow<T>>()
        var lastLabel: String? = null

        for (item in sorted) {
            val ms = epochMillis(item) ?: continue
            val bucket = bucketFor(ms)
            if (bucket.label != lastLabel) {
                rows += HistoryRow.Header(bucket)
                lastLabel = bucket.label
            }
            rows += HistoryRow.Item(item, relativeTime(ms))
        }
        return rows
    }

    private fun daysBetween(from: Calendar, to: Calendar): Int {
        val f = from.clone() as Calendar
        val t = to.clone() as Calendar
        f.set(Calendar.HOUR_OF_DAY, 0); f.set(Calendar.MINUTE, 0)
        f.set(Calendar.SECOND, 0); f.set(Calendar.MILLISECOND, 0)
        t.set(Calendar.HOUR_OF_DAY, 0); t.set(Calendar.MINUTE, 0)
        t.set(Calendar.SECOND, 0); t.set(Calendar.MILLISECOND, 0)
        val diff = t.timeInMillis - f.timeInMillis
        return (diff / 86_400_000).toInt()
    }
}
