package com.swahilib.core.data.repos

import android.content.Context
import androidx.core.content.edit
import com.swahilib.core.common.utils.NotifConstants
import com.swahilib.core.common.utils.PrefConstants
import com.swahilib.core.network.api.KamusiApi
import dagger.hilt.android.qualifiers.ApplicationContext
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
        return System.currentTimeMillis() - lastTime >= hours * 60 * 60 * 1000L
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
        if (installDate == 0L || now - installDate < oneDayMs) return false
        val donated = donationDoneAt
        return donated == 0L || now - donated > 60 * oneDayMs
    }

    fun recordDonation() {
        donationDoneAt = System.currentTimeMillis()
        donationRemindNextOpen = false
    }

    var lastSyncedAt: Long
        get() = prefs.getLong(PrefConstants.LAST_SYNCED_AT, 0L)
        set(value) = prefs.edit { putLong(PrefConstants.LAST_SYNCED_AT, value) }

    var wordNotifEnabled: Boolean
        get() = prefs.getBoolean(PrefConstants.NOTIF_WORD_ENABLED, true)
        set(value) = prefs.edit { putBoolean(PrefConstants.NOTIF_WORD_ENABLED, value) }

    var wordNotifHour: Int
        get() = prefs.getInt(PrefConstants.NOTIF_WORD_HOUR, NotifConstants.DEFAULT_HOUR)
        set(value) = prefs.edit { putInt(PrefConstants.NOTIF_WORD_HOUR, value) }

    var wordNotifMinute: Int
        get() = prefs.getInt(PrefConstants.NOTIF_WORD_MINUTE, NotifConstants.DEFAULT_MINUTE)
        set(value) = prefs.edit { putInt(PrefConstants.NOTIF_WORD_MINUTE, value) }

    var proverbNotifEnabled: Boolean
        get() = prefs.getBoolean(PrefConstants.NOTIF_PROVERB_ENABLED, true)
        set(value) = prefs.edit { putBoolean(PrefConstants.NOTIF_PROVERB_ENABLED, value) }

    var proverbNotifHour: Int
        get() = prefs.getInt(PrefConstants.NOTIF_PROVERB_HOUR, NotifConstants.DEFAULT_HOUR)
        set(value) = prefs.edit { putInt(PrefConstants.NOTIF_PROVERB_HOUR, value) }

    var proverbNotifMinute: Int
        get() = prefs.getInt(PrefConstants.NOTIF_PROVERB_MINUTE, NotifConstants.DEFAULT_MINUTE)
        set(value) = prefs.edit { putInt(PrefConstants.NOTIF_PROVERB_MINUTE, value) }

    var notifBannerDismissed: Boolean
        get() = prefs.getBoolean(PrefConstants.NOTIF_BANNER_DISMISSED, false)
        set(value) = prefs.edit { putBoolean(PrefConstants.NOTIF_BANNER_DISMISSED, value) }

    fun getETag(endpoint: KamusiApi.Endpoint): String? =
        prefs.getString(endpoint.prefKey, null)

    fun setETag(endpoint: KamusiApi.Endpoint, etag: String) =
        prefs.edit { putString(endpoint.prefKey, etag) }

    fun clearETags() = prefs.edit {
        KamusiApi.Endpoint.entries.forEach { remove(it.prefKey) }
    }
}
