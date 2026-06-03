package com.swahilib.core.common.utils

object AppConstants {
    const val APP_TITLE = "SwahiLib"
    const val APP_TITLE2 = "Kamusi ya Kiswahili"
    const val APP_TAGLINE = "Kiswahili Kitukuzwe"
    const val APP_CREDITS = "© Siro Devs"
    const val APP_LINK = "https://linktr.ee/SwahilibApp"
    const val SUPPORT_EMAIL = "futuristicken@gmail.com"
}

object ApiConstants {
    const val PESAPAL_BASE_URL = "https://pay.pesapal.com/v3/api/"
    // For sandbox swap to: "https://cybqa.pesapal.com/pesapalv3/api/"

    const val PESAPAL_AUTH  = "Auth/RequestToken"
    const val PESAPAL_ORDER = "Transactions/SubmitOrderRequest"

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
}

object Routes {
    const val SPLASH = "splash"
    const val INIT = "init"
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
}
