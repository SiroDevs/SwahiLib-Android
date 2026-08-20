package com.swahilib.core.common.utils

import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object AppConstants {
    const val APP_TITLE = "SwahiLib"
    const val APP_TITLE2 = "Kamusi ya Kiswahili"
    const val APP_TAGLINE = "Kiswahili Kitukuzwe"
    const val APP_CREDITS = "© Siro Devs"
    const val APP_LINK = "https://sirodevs.vercel.app/SwahiLib"
    const val SUPPORT_EMAIL = "futuristicken@gmail.com"
}

object ApiConstants {
    const val PAYSTACK_BASE_URL = "https://api.paystack.co/"
    const val PAYSTACK_INITIALIZE = "transaction/initialize"
    const val PAYSTACK_CALLBACK_URL = "https://songlive.vercel.app/donation/callback"
    const val CALLBACK_URL = "https://swahilib.vercel.app/donation/callback"
    const val DONOR_EMAIL  = "anonymous_donor@swahilib.app"
    const val KAMUSI_API = "https://swahilive.vercel.app/"
}

object PayConstants {
    const val MPESA_NUMBER = "+254115586529"
    const val PAYBILL_NUMBER = "880100"
    const val ACCOUNT_NUMBER = "9530000018"
    const val CRYPTO_PROFILE = "sirodevs.base.eth"
    const val ETH_ADDRESS = "0x1C9A68b346D956e9Eff027C34DB671817e365fbb"
    const val SOL_ADDRESS = "5qW65Qu8hMLdsjNPxvpFGuv6EAJosGA5YDo6ePSnva1A"
    const val PAYPAL_ADDRESS = "sirodevske@gmail.com"
}

object PrefConstants {
    const val PREFERENCE_FILE = "app_pref"
    const val THEME_MODE = "theme_mode"
    const val IS_DATA_LOADED = "is_data_loaded"
    const val INSTALL_DATE = "install_date"
    const val LAST_HOME_TAB = "last_home_tab"
    const val LAST_APP_OPEN_TIME = "lastAppOpenTime"

    const val DONATION_DONE_AT = "donation_done_at"
    const val DONATION_METHOD = "donation_method"
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

    // ── Engagement ──
    const val NOTIF_CHALLENGE_ENABLED = "notif_challenge_enabled"
    const val NOTIF_CHALLENGE_HOUR = "notif_challenge_hour"
    const val NOTIF_CHALLENGE_MINUTE = "notif_challenge_minute"

    const val NOTIF_WEEKLY_SUMMARY_ENABLED = "notif_weekly_summary_enabled"
    const val NOTIF_WEEKLY_SUMMARY_HOUR = "notif_weekly_summary_hour"
    const val NOTIF_WEEKLY_SUMMARY_MINUTE = "notif_weekly_summary_minute"

    const val DAILY_LOGIN_LAST_DATE = "daily_login_last_date"

    // ── Game audio ──
    const val GAME_MUSIC_ENABLED = "game_music_enabled"
    const val GAME_SFX_ENABLED = "game_sfx_enabled"

    // ── Notification permission nag ──
    const val NOTIF_NAG_LAST_SHOWN_AT = "notif_nag_last_shown_at"
    const val NOTIF_NAG_DISMISSED_PERMANENTLY = "notif_nag_dismissed_permanently"
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

    const val CHANNEL_CHALLENGE_ID = "swahilib_daily_challenge"
    const val CHANNEL_CHALLENGE_NAME = "Changamoto ya Siku"
    const val NOTIF_CHALLENGE_ID = 1003
    const val WORK_CHALLENGE = "work_daily_challenge"
    const val DEFAULT_CHALLENGE_HOUR = 18
    const val DEFAULT_CHALLENGE_MINUTE = 0

    const val CHANNEL_SUMMARY_ID = "swahilib_weekly_summary"
    const val CHANNEL_SUMMARY_NAME = "Muhtasari wa Wiki"
    const val NOTIF_SUMMARY_ID = 1004
    const val WORK_WEEKLY_SUMMARY = "work_weekly_summary"
    const val DEFAULT_SUMMARY_HOUR = 19
    const val DEFAULT_SUMMARY_MINUTE = 0
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
    const val SETTINGS_APPEARANCE = "settings_appearance"
    const val SETTINGS_NOTIFICATIONS = "settings_notifications"
    const val SETTINGS_DATA = "settings_data"
    const val SETTINGS_GAMES = "settings_games"
    const val LIKES = "likes"
    const val HISTORY = "history"
    const val AUTH_SIGN_IN = "auth_sign_in"
    const val SOCIAL = "social"
    const val ADVANCED_SEARCH = "advanced_search"
    const val HOW_IT_WORKS = "how_it_works"
    const val HELP = "help"
    const val DONATION = "donation"
    const val DAILY_WORD = "daily_word"
    const val DAILY_PROVERB = "daily_proverb"

    const val PROGRESS = "progress"
    const val STATISTICS = "statistics"
    const val ACHIEVEMENTS = "achievements"
    const val CHALLENGES = "challenges"

    const val QUIZ = "quiz?challengeId={challengeId}&activityId={activityId}&difficulty={difficulty}&source={source}"

    /** Quiz tied to a specific challenge activity - completing it marks that activity done. */
    fun quiz(challengeId: String, activityId: String, difficulty: String, source: String = "WORDS"): String =
        "quiz?challengeId=$challengeId&activityId=$activityId&difficulty=$difficulty&source=$source"

    /** Freeplay quiz with no challenge attached - just awards XP directly. */
    fun quizFreeplay(difficulty: String = "BEGINNER", source: String = "WORDS"): String =
        "quiz?difficulty=$difficulty&source=$source"

    const val WORD_BUILDER =
        "wordbuilder?challengeId={challengeId}&activityId={activityId}&difficulty={difficulty}&timed={timed}&endless={endless}"

    fun wordBuilder(challengeId: String, activityId: String, difficulty: String): String =
        "wordbuilder?challengeId=$challengeId&activityId=$activityId&difficulty=$difficulty"

    fun wordBuilderFreeplay(difficulty: String = "BEGINNER", timed: Boolean = false, endless: Boolean = false): String =
        "wordbuilder?difficulty=$difficulty&timed=$timed&endless=$endless"

    const val SENTENCE_BUILDER = "sentencebuilder?challengeId={challengeId}&activityId={activityId}&difficulty={difficulty}"

    fun sentenceBuilder(challengeId: String, activityId: String, difficulty: String): String =
        "sentencebuilder?challengeId=$challengeId&activityId=$activityId&difficulty=$difficulty"

    fun sentenceBuilderFreeplay(difficulty: String = "BEGINNER"): String = "sentencebuilder?difficulty=$difficulty"

    const val SPELLING = "spelling?challengeId={challengeId}&activityId={activityId}&difficulty={difficulty}"

    fun spelling(challengeId: String, activityId: String, difficulty: String): String =
        "spelling?challengeId=$challengeId&activityId=$activityId&difficulty=$difficulty"

    fun spellingFreeplay(difficulty: String = "BEGINNER"): String = "spelling?difficulty=$difficulty"

    const val CROSSWORD = "crossword?challengeId={challengeId}&activityId={activityId}&difficulty={difficulty}"

    fun crossword(challengeId: String, activityId: String, difficulty: String): String =
        "crossword?challengeId=$challengeId&activityId=$activityId&difficulty=$difficulty"

    fun crosswordFreeplay(difficulty: String = "BEGINNER"): String = "crossword?difficulty=$difficulty"

    const val SUDOKU = "wordsearch?challengeId={challengeId}&activityId={activityId}&difficulty={difficulty}&theme={theme}"

    fun wordSearch(challengeId: String, activityId: String, difficulty: String, theme: String = "RANDOM"): String =
        "wordsearch?challengeId=$challengeId&activityId=$activityId&difficulty=$difficulty&theme=$theme"

    fun wordSearchFreeplay(difficulty: String = "BEGINNER", theme: String = "RANDOM"): String =
        "wordsearch?difficulty=$difficulty&theme=$theme"

    const val HANGMAN = "hangman?challengeId={challengeId}&activityId={activityId}&difficulty={difficulty}"

    fun hangman(challengeId: String, activityId: String, difficulty: String): String =
        "hangman?challengeId=$challengeId&activityId=$activityId&difficulty=$difficulty"

    fun hangmanFreeplay(difficulty: String = "BEGINNER"): String = "hangman?difficulty=$difficulty"

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
