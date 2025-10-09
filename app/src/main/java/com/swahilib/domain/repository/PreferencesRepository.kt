package com.swahilib.domain.repository

import android.content.Context
import android.content.SharedPreferences
import com.swahilib.core.utils.PrefConstants
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.*

@Singleton
class PreferencesRepository @Inject constructor(
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

    var isReviewDone: Boolean
        get() = prefs.getBoolean(PrefConstants.IS_REVIEW_DONE, false)
        set(value) = prefs.edit { putBoolean(PrefConstants.IS_REVIEW_DONE, value) }

    var installDate: Long
        get() = prefs.getLong(PrefConstants.INSTALL_DATE, 0L)
        set(value) = prefs.edit { putLong(PrefConstants.INSTALL_DATE, value) }

    var lastHomeTab: Int
        get() = prefs.getInt(PrefConstants.LAST_HOME_TAB, 0)
        set(value) = prefs.edit { putInt(PrefConstants.LAST_HOME_TAB, value) }

    var isProUser: Boolean
        get() = prefs.getBoolean(PrefConstants.IS_PRO_USER, false)
        set(value) = prefs.edit { putBoolean(PrefConstants.IS_PRO_USER, value) }

    var canShowPaywall: Boolean
        get() = prefs.getBoolean(PrefConstants.CAN_SHOW_PAYWALL, false)
        set(value) = prefs.edit { putBoolean(PrefConstants.CAN_SHOW_PAYWALL, value) }

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

    fun updateAppOpenTime() {
        lastAppOpenTime = System.currentTimeMillis()
    }

    fun getTimeSinceLastOpen(): Long {
        val lastTime = lastAppOpenTime
        if (lastTime == 0L) return 0L
        return System.currentTimeMillis() - lastTime
    }

    private inline fun SharedPreferences.edit(
        commit: Boolean = false,
        action: SharedPreferences.Editor.() -> Unit
    ) {
        val editor = edit()
        editor.action()
        if (commit) {
            editor.apply()
        }
    }
}