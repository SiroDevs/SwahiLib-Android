package com.swahilib.domain.repository

import android.content.Context
import androidx.core.content.edit
import com.swahilib.core.utils.PrefConstants
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.*

@Singleton
class PrefsRepository @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs =
        context.getSharedPreferences(PrefConstants.PREFERENCE_FILE, Context.MODE_PRIVATE)

    var initialBooks: String
        get() = prefs.getString(PrefConstants.INITIAL_BOOKS, "") ?: ""
        set(value) = prefs.edit { putString(PrefConstants.INITIAL_BOOKS, value) }

    var selectedBooks: String
        get() = prefs.getString(PrefConstants.SELECTED_BOOKS, "1,2") ?: ""
        set(value) = prefs.edit { putString(PrefConstants.SELECTED_BOOKS, value) }

    var isDataSelected: Boolean
        get() = prefs.getBoolean(PrefConstants.SELECT_AFRESH, false)
        set(value) = prefs.edit { putBoolean(PrefConstants.DATA_SELECTED, value) }

    var selectAfresh: Boolean
        get() = prefs.getBoolean(PrefConstants.SELECT_AFRESH, false)
        set(value) = prefs.edit { putBoolean(PrefConstants.SELECT_AFRESH, value) }

    var isDataLoaded: Boolean
        get() = prefs.getBoolean(PrefConstants.DATA_LOADED, false)
        set(value) = prefs.edit { putBoolean(PrefConstants.DATA_LOADED, value) }

    var appThemeMode: ThemeMode
        get() = ThemeMode.valueOf(
            prefs.getString(PrefConstants.THEME_MODE, ThemeMode.SYSTEM.name)
                ?: ThemeMode.SYSTEM.name
        )
        set(value) = prefs.edit { putString(PrefConstants.THEME_MODE, value.name) }

    var horizontalSlides: Boolean
        get() = prefs.getBoolean(PrefConstants.HORIZONTAL_SLIDES, false)
        set(value) = prefs.edit { putBoolean(PrefConstants.HORIZONTAL_SLIDES, value) }

}
