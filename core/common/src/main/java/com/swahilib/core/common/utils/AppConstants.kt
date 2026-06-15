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
    const val PAYSTACK_BASE_URL = "https://api.paystack.co/"
    const val PAYSTACK_INITIALIZE = "transaction/initialize"
    const val PAYSTACK_CALLBACK_URL = "https://songlive.vercel.app/donation/callback"
    const val CALLBACK_URL = "https://swahilib.vercel.app/donation/callback"
    const val DONOR_EMAIL  = "donor@swahilib.app"
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
    const val NOTIF_BANNER_DISMISSED = "notif_banner_dismissed"
}

object NotifConstants {
    const val CHANNEL_WORD_ID = "swahilib_word_of_day"
    const val CHANNEL_PROVERB_ID = "swahilib_proverb_of_day"
    const val CHANNEL_WORD_NAME = "Word of the Day"
    const val CHANNEL_PROVERB_NAME = "Proverb of the Day"
    const val NOTIF_WORD_ID = 1001
    const val NOTIF_PROVERB_ID = 1002
    const val WORK_WORD = "work_daily_word"
    const val WORK_PROVERB = "work_daily_proverb"
    const val DEFAULT_HOUR = 7
    const val DEFAULT_MINUTE = 0
}

object DeepLinkConstants {
    const val EXTRA_NAVIGATE_TO = "navigate_to"
}

object Routes {
    const val HOME = "home"
    const val IDIOM = "idiom"
    const val PROVERB = "proverb"
    const val SAYING = "saying"
    const val WORD = "word"
    const val SETTINGS = "settings"
    const val ADVSEARCH = "advsearch"
    const val HOW_IT_WORKS = "how_it_works"
    const val HELP = "help"
    const val DONATION = "donation"
    const val DAILY_WORD = "daily_word"
    const val DAILY_PROVERB = "daily_proverb"

    const val PAYMENT_WEBVIEW = "payment_webview/{redirectUrl}"

    fun paymentWebView(redirectUrl: String): String {
        val encoded = URLEncoder.encode(redirectUrl, StandardCharsets.UTF_8.toString())
        return "payment_webview/$encoded"
    }

    fun decodeRedirectUrl(encoded: String): String =
        URLDecoder.decode(encoded, StandardCharsets.UTF_8.toString())
}
