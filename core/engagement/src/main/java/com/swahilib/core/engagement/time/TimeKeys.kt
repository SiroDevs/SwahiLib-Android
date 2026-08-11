package com.swahilib.core.engagement.time

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

interface Clock {
    fun now(): Long
    fun timeZone(): TimeZone = TimeZone.getDefault()
}

object SystemClock : Clock {
    override fun now(): Long = System.currentTimeMillis()
}

object TimeKeys {
    private val DAY_FMT get() = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
        timeZone = TimeZone.getDefault()
    }

    fun today(clock: Clock = SystemClock): String = format(clock.now())

    fun format(epochMillis: Long): String = DAY_FMT.format(Date(epochMillis))

    fun weekKey(clock: Clock = SystemClock): String {
        val cal = Calendar.getInstance(clock.timeZone(), Locale.US).apply {
            timeInMillis = clock.now()
            minimalDaysInFirstWeek = 4
            firstDayOfWeek = Calendar.MONDAY
        }
        val year = cal.getWeekYear()
        val week = cal.get(Calendar.WEEK_OF_YEAR)
        return "%d-W%02d".format(year, week)
    }

    fun monthKey(clock: Clock = SystemClock): String {
        val cal = Calendar.getInstance(clock.timeZone(), Locale.US).apply {
            timeInMillis = clock.now()
        }
        return "%d-%02d".format(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1)
    }

    fun endOfDay(clock: Clock = SystemClock): Long {
        val cal = Calendar.getInstance(clock.timeZone()).apply {
            timeInMillis = clock.now()
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        return cal.timeInMillis
    }

    fun endOfWeek(clock: Clock = SystemClock): Long {
        val cal = Calendar.getInstance(clock.timeZone()).apply {
            timeInMillis = clock.now()
            firstDayOfWeek = Calendar.MONDAY
            set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        if (cal.timeInMillis < clock.now()) cal.add(Calendar.WEEK_OF_YEAR, 1)
        return cal.timeInMillis
    }

    fun endOfMonth(clock: Clock = SystemClock): Long {
        val cal = Calendar.getInstance(clock.timeZone()).apply {
            timeInMillis = clock.now()
            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        return cal.timeInMillis
    }

    fun daysAgoKey(days: Int, clock: Clock = SystemClock): String {
        val cal = Calendar.getInstance(clock.timeZone()).apply {
            timeInMillis = clock.now()
            add(Calendar.DAY_OF_YEAR, -days)
        }
        return DAY_FMT.format(cal.time)
    }
}
