package com.swahilib.core.common.utils

import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object AppConstants {
    const val APP_TITLE = "SwahiLib"
    const val APP_TITLE2 = "Kamusi ya Kiswahili"
    const val APP_TAGLINE = "Kiswahili Kitukuzwe"
    const val APP_CREDITS = "© Siro Devs"
    const val APP_LINK = "https://linktr.ee/SwahilibApp"
    const val SUPPORT_EMAIL = "futuristicken@gmail.com"
}

object ApiConstants {
    // The app must NEVER talk to Paystack directly with a secret key.
    // This points at the server-side donation proxy (sirodevs.vercel.app),
    // which holds the Paystack secret key server-side and forwards the
    // initialize call — the app never sees a credential.
    const val DONATION_API_BASE_URL = "https://sirodevs.vercel.app/"
    const val DONATION_INITIALIZE = "SwahiLib/donation"
    // Must match the callbackUrl registered for "swahilib" in the proxy's
    // infrastructure/donation/app-registry.ts — that's what's actually sent
    // to Paystack as callback_url, and this WebView only recognizes
    // completion by matching against this exact prefix.
    const val DONATION_CALLBACK_URL = "https://sirodevs.vercel.app/SwahiLib/donation/callback"
    const val DONOR_EMAIL = "anonymous_donor@swahilib.app"
    const val KAMUSI_API = "https://swahilive.vercel.app/"
}

object PrefConstants {
    const val PREFERENCE_FILE = "app_pref"
    const val THEME_MODE = "theme_mode"
    const val IS_DATA_LOADED = "is_data_loaded"
    const val INSTALL_DATE = "install_date"
    const val LAST_HOME_TAB = "last_home_tab"
    const val LAST_APP_OPEN_TIME = "lastAppOpenTime"

    const val DONATION_DONE_AT = "donation_done_at"
    const val DONATION_REMIND_NEXT_OPEN = "donation_remind_next"
    const val LAST_SYNCED_AT = "last_synced_at"

    const val NOTIF_WORD_ENABLED = "notif_word_enabled"
    const val NOTIF_WORD_HOUR = "notif_word_hour"
    const val NOTIF_WORD_MINUTE = "notif_word_minute"
    const val NOTIF_PROVERB_ENABLED = "notif_proverb_enabled"
    const val NOTIF_PROVERB_HOUR = "notif_proverb_hour"
    const val NOTIF_PROVERB_MINUTE = "notif_proverb_minute"
    const val STREAK_COUNT = "streak_count"
    const val STREAK_BEST = "streak_best"
    const val STREAK_LAST_DATE = "streak_last_date"

    const val DAILY_DIALOG_LAST_SHOWN = "daily_dialog_last_shown"
}

object NotifConstants {
    const val CHANNEL_WORD_ID = "swahilib_word_of_day"
    const val CHANNEL_PROVERB_ID = "swahilib_proverb_of_day"
    const val CHANNEL_WORD_NAME = "Neno la Siku"
    const val CHANNEL_PROVERB_NAME = "Methali ya Siku"
    const val NOTIF_WORD_ID = 1001
    const val NOTIF_PROVERB_ID = 1002
    const val WORK_WORD = "work_daily_word"
    const val WORK_PROVERB = "work_daily_proverb"
    const val DEFAULT_HOUR = 7
    const val DEFAULT_MINUTE = 0
}

object DeepLinkConstants {
    const val EXTRA_NAVIGATE_TO = "swahilib_daily_content_navigation"
}

object Routes {
    const val HOME = "home"
    const val IDIOM = "idiom"
    const val PROVERB = "proverb"
    const val SAYING = "saying"
    const val WORD = "word"
    const val SETTINGS = "settings"
    const val ADVANCED_SEARCH = "advanced_search"
    const val HOW_IT_WORKS = "how_it_works"
    const val HELP = "help"
    const val DONATION = "donation"
    const val DAILY_WORD = "daily_word"
    const val DAILY_PROVERB = "daily_proverb"

    const val DAILY_CONTENT_TYPE_WORD = "word"
    const val DAILY_CONTENT_TYPE_PROVERB = "proverb"
    const val DAILY_CONTENT_HISTORY = "daily_content_history/{type}"

    fun dailyContentHistory(type: String): String = "daily_content_history/$type"

    const val PAYMENT_WEBVIEW = "payment_webview/{redirectUrl}"

    fun paymentWebView(redirectUrl: String): String {
        val encoded = URLEncoder.encode(redirectUrl, StandardCharsets.UTF_8.toString())
        return "payment_webview/$encoded"
    }

    fun decodeRedirectUrl(encoded: String): String =
        URLDecoder.decode(encoded, StandardCharsets.UTF_8.toString())
}
