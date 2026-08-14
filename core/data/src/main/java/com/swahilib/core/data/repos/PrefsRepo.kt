package com.swahilib.core.data.repos

import android.content.Context
import androidx.core.content.edit
import com.swahilib.core.common.utils.NotifConstants
import com.swahilib.core.common.utils.PrefConstants
import com.swahilib.core.network.api.KamusiApi
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
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

    // ── Engagement notifications ──

    var challengeNotifEnabled: Boolean
        get() = prefs.getBoolean(PrefConstants.NOTIF_CHALLENGE_ENABLED, true)
        set(value) = prefs.edit { putBoolean(PrefConstants.NOTIF_CHALLENGE_ENABLED, value) }

    var challengeNotifHour: Int
        get() = prefs.getInt(PrefConstants.NOTIF_CHALLENGE_HOUR, NotifConstants.DEFAULT_CHALLENGE_HOUR)
        set(value) = prefs.edit { putInt(PrefConstants.NOTIF_CHALLENGE_HOUR, value) }

    var challengeNotifMinute: Int
        get() = prefs.getInt(PrefConstants.NOTIF_CHALLENGE_MINUTE, NotifConstants.DEFAULT_CHALLENGE_MINUTE)
        set(value) = prefs.edit { putInt(PrefConstants.NOTIF_CHALLENGE_MINUTE, value) }

    // ── Game audio ──

    var gameMusicEnabled: Boolean
        get() = prefs.getBoolean(PrefConstants.GAME_MUSIC_ENABLED, true)
        set(value) = prefs.edit { putBoolean(PrefConstants.GAME_MUSIC_ENABLED, value) }

    var gameSfxEnabled: Boolean
        get() = prefs.getBoolean(PrefConstants.GAME_SFX_ENABLED, true)
        set(value) = prefs.edit { putBoolean(PrefConstants.GAME_SFX_ENABLED, value) }

    // ── Notification permission nag ──
    // Shown on app open while notifications are OS-disabled. Backs off to at most
    // once every 3 days so it nudges without becoming an annoyance, and can be
    // silenced for good from the banner itself.

    var notifNagLastShownAt: Long
        get() = prefs.getLong(PrefConstants.NOTIF_NAG_LAST_SHOWN_AT, 0L)
        set(value) = prefs.edit { putLong(PrefConstants.NOTIF_NAG_LAST_SHOWN_AT, value) }

    var notifNagDismissedPermanently: Boolean
        get() = prefs.getBoolean(PrefConstants.NOTIF_NAG_DISMISSED_PERMANENTLY, false)
        set(value) = prefs.edit { putBoolean(PrefConstants.NOTIF_NAG_DISMISSED_PERMANENTLY, value) }

    fun shouldShowNotifNag(notificationsGranted: Boolean): Boolean {
        if (notificationsGranted || notifNagDismissedPermanently) return false
        val threeDaysMs = 3 * 24 * 60 * 60 * 1000L
        return System.currentTimeMillis() - notifNagLastShownAt >= threeDaysMs
    }

    fun recordNotifNagShown() {
        notifNagLastShownAt = System.currentTimeMillis()
    }

    var weeklySummaryNotifEnabled: Boolean
        get() = prefs.getBoolean(PrefConstants.NOTIF_WEEKLY_SUMMARY_ENABLED, true)
        set(value) = prefs.edit { putBoolean(PrefConstants.NOTIF_WEEKLY_SUMMARY_ENABLED, value) }

    var weeklySummaryNotifHour: Int
        get() = prefs.getInt(PrefConstants.NOTIF_WEEKLY_SUMMARY_HOUR, NotifConstants.DEFAULT_SUMMARY_HOUR)
        set(value) = prefs.edit { putInt(PrefConstants.NOTIF_WEEKLY_SUMMARY_HOUR, value) }

    var weeklySummaryNotifMinute: Int
        get() = prefs.getInt(PrefConstants.NOTIF_WEEKLY_SUMMARY_MINUTE, NotifConstants.DEFAULT_SUMMARY_MINUTE)
        set(value) = prefs.edit { putInt(PrefConstants.NOTIF_WEEKLY_SUMMARY_MINUTE, value) }

    /**
     * Date-key of the last day we granted the daily-login reward. Callers
     * pass this to RewardsEngine.grantDailyLogin() to keep the reward
     * idempotent per calendar day.
     */
    var lastDailyLoginDate: String
        get() = prefs.getString(PrefConstants.DAILY_LOGIN_LAST_DATE, "") ?: ""
        set(value) = prefs.edit { putString(PrefConstants.DAILY_LOGIN_LAST_DATE, value) }

    fun hasClaimedDailyLoginToday(): Boolean = lastDailyLoginDate == todayKey()

    fun markDailyLoginClaimed() { lastDailyLoginDate = todayKey() }

    var streakCount: Int
        get() = prefs.getInt(PrefConstants.STREAK_COUNT, 0)
        set(value) = prefs.edit { putInt(PrefConstants.STREAK_COUNT, value) }

    var bestStreak: Int
        get() = prefs.getInt(PrefConstants.STREAK_BEST, 0)
        set(value) = prefs.edit { putInt(PrefConstants.STREAK_BEST, value) }

    private var streakLastDate: String
        get() = prefs.getString(PrefConstants.STREAK_LAST_DATE, "") ?: ""
        set(value) = prefs.edit { putString(PrefConstants.STREAK_LAST_DATE, value) }

    val currentStreak: Int
        get() = when (streakLastDate) {
            todayKey(), yesterdayKey() -> streakCount
            else -> 0
        }

    fun recordDailyVisit(): Int {
        val today = todayKey()
        if (streakLastDate == today) return streakCount

        val newCount = if (streakLastDate == yesterdayKey()) streakCount + 1 else 1
        streakCount = newCount
        streakLastDate = today
        if (newCount > bestStreak) bestStreak = newCount
        return newCount
    }

    fun resetStreaks() {
        streakCount = 0
        bestStreak = 0
        streakLastDate = ""
        lastDailyLoginDate = ""
    }

    private fun dateKey(daysAgo: Int): String {
        val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -daysAgo) }
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.time)
    }

    private fun todayKey(): String = dateKey(0)
    private fun yesterdayKey(): String = dateKey(1)

    private var dailyDialogLastShownDate: String
        get() = prefs.getString(PrefConstants.DAILY_DIALOG_LAST_SHOWN, "") ?: ""
        set(value) = prefs.edit { putString(PrefConstants.DAILY_DIALOG_LAST_SHOWN, value) }

    fun shouldShowDailyDialog(): Boolean = dailyDialogLastShownDate != todayKey()

    fun markDailyDialogShown() { dailyDialogLastShownDate = todayKey() }

    fun getETag(endpoint: KamusiApi.Endpoint): String? =
        prefs.getString(endpoint.prefKey, null)

    fun setETag(endpoint: KamusiApi.Endpoint, etag: String) =
        prefs.edit { putString(endpoint.prefKey, etag) }

    fun clearETags() = prefs.edit {
        KamusiApi.Endpoint.entries.forEach { remove(it.prefKey) }
    }
}
