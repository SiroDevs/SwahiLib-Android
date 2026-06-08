package com.swahilib.core.data.repos

import android.content.Context
import com.swahilib.core.common.utils.NotifConstants
import com.swahilib.core.common.utils.PrefConstants
import dagger.hilt.android.qualifiers.ApplicationContext
import androidx.core.content.edit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrefsRepo @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs =
        context.getSharedPreferences(PrefConstants.PREFERENCE_FILE, Context.MODE_PRIVATE)

    var isDataLoaded: Boolean
        get() = prefs.getBoolean(PrefConstants.IS_DATA_LOADED, false)
        set(value) = prefs.edit { putBoolean(PrefConstants.IS_DATA_LOADED, value) }

    var appThemeMode: ThemeMode
        get() = ThemeMode.valueOf(
            prefs.getString(PrefConstants.THEME_MODE, ThemeMode.SYSTEM.name)
                ?: ThemeMode.SYSTEM.name
        )
        set(value) = prefs.edit { putString(PrefConstants.THEME_MODE, value.name) }

    var installDate: Long
        get() = prefs.getLong(PrefConstants.INSTALL_DATE, 0L)
        set(value) = prefs.edit { putLong(PrefConstants.INSTALL_DATE, value) }

    var lastHomeTab: Int
        get() = prefs.getInt(PrefConstants.LAST_HOME_TAB, 0)
        set(value) = prefs.edit { putInt(PrefConstants.LAST_HOME_TAB, value) }

    var lastAppOpenTime: Long
        get() = prefs.getLong(PrefConstants.LAST_APP_OPEN_TIME, 0L)
        set(value) = prefs.edit { putLong(PrefConstants.LAST_APP_OPEN_TIME, value) }

    fun hasTimeExceeded(hours: Int = 5): Boolean {
        val lastTime = lastAppOpenTime
        if (lastTime == 0L) return false
        val currentTime = System.currentTimeMillis()
        val timeDifference = currentTime - lastTime
        val hoursInMillis = hours * 60 * 60 * 1000L
        return timeDifference >= hoursInMillis
    }

    fun updateAppOpenTime() { lastAppOpenTime = System.currentTimeMillis() }

    fun getTimeSinceLastOpen(): Long {
        val lastTime = lastAppOpenTime
        if (lastTime == 0L) return 0L
        return System.currentTimeMillis() - lastTime
    }

    var donationDoneAt: Long
        get() = prefs.getLong(PrefConstants.DONATION_DONE_AT, 0L)
        set(value) = prefs.edit { putLong(PrefConstants.DONATION_DONE_AT, value) }

    var donationRemindNextOpen: Boolean
        get() = prefs.getBoolean(PrefConstants.DONATION_REMIND_NEXT_OPEN, false)
        set(value) = prefs.edit { putBoolean(PrefConstants.DONATION_REMIND_NEXT_OPEN, value) }

    fun shouldShowDonation(): Boolean {
        val now = System.currentTimeMillis()
        val oneDayMs = 24 * 60 * 60 * 1000L
        val sixtyDaysMs = 60 * oneDayMs
        if (installDate == 0L || now - installDate < oneDayMs) return false
        val donated = donationDoneAt
        return donated == 0L || now - donated > sixtyDaysMs
    }

    fun recordDonation() {
        donationDoneAt = System.currentTimeMillis()
        donationRemindNextOpen = false
    }

    var lastSyncedAt: Long
        get() = prefs.getLong(PrefConstants.LAST_SYNCED_AT, 0L)
        set(value) = prefs.edit { putLong(PrefConstants.LAST_SYNCED_AT, value) }

    fun needsDailySync(): Boolean {
        val last = lastSyncedAt
        if (last == 0L) return false
        val elapsed = System.currentTimeMillis() - last
        val oneDayMs = 24 * 60 * 60 * 1000L
        return elapsed >= oneDayMs
    }

    // ── Neno la Siku notification ──────────────────────────────────────────
    var nenoNotifEnabled: Boolean
        get() = prefs.getBoolean(PrefConstants.NOTIF_WORD_ENABLED, true)
        set(value) = prefs.edit { putBoolean(PrefConstants.NOTIF_WORD_ENABLED, value) }

    var nenoNotifHour: Int
        get() = prefs.getInt(PrefConstants.NOTIF_WORD_HOUR, NotifConstants.DEFAULT_HOUR)
        set(value) = prefs.edit { putInt(PrefConstants.NOTIF_WORD_HOUR, value) }

    var nenoNotifMinute: Int
        get() = prefs.getInt(PrefConstants.NOTIF_WORD_MINUTE, NotifConstants.DEFAULT_MINUTE)
        set(value) = prefs.edit { putInt(PrefConstants.NOTIF_WORD_MINUTE, value) }

    // ── Methali ya Siku notification ───────────────────────────────────────
    var methaliNotifEnabled: Boolean
        get() = prefs.getBoolean(PrefConstants.NOTIF_PROVERB_ENABLED, true)
        set(value) = prefs.edit { putBoolean(PrefConstants.NOTIF_PROVERB_ENABLED, value) }

    var methaliNotifHour: Int
        get() = prefs.getInt(PrefConstants.NOTIF_PROVERB_HOUR, NotifConstants.DEFAULT_HOUR)
        set(value) = prefs.edit { putInt(PrefConstants.NOTIF_PROVERB_HOUR, value) }

    var methaliNotifMinute: Int
        get() = prefs.getInt(PrefConstants.NOTIF_PROVERB_MINUTE, NotifConstants.DEFAULT_MINUTE)
        set(value) = prefs.edit { putInt(PrefConstants.NOTIF_PROVERB_MINUTE, value) }
}
