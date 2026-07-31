package com.swahilib.core.engagement.catalog

import com.swahilib.core.engagement.model.Achievement

/**
 * Static catalog of every achievement the app can award. Unlock predicates
 * live in AchievementEngine so the catalog stays pure data - useful for
 * building a badges grid without pulling in DB dependencies.
 */
object AchievementCatalog {
    const val FIRST_STEPS = "first_steps"
    const val WEEK_WARRIOR = "week_warrior"
    const val MONTH_MASTER = "month_master"
    const val CENTURION = "centurion"

    const val CHALLENGE_ROOKIE = "challenge_rookie"
    const val CHALLENGE_REGULAR = "challenge_regular"
    const val CHALLENGE_LEGEND = "challenge_legend"

    const val WORD_COLLECTOR_10 = "word_collector_10"
    const val WORD_COLLECTOR_50 = "word_collector_50"
    const val WORD_COLLECTOR_200 = "word_collector_200"

    const val QUIZ_SHARPSHOOTER = "quiz_sharpshooter"
    const val LEVEL_5 = "level_5"
    const val LEVEL_10 = "level_10"
    const val LEVEL_25 = "level_25"

    val ALL: List<Achievement> = listOf(
        Achievement(FIRST_STEPS, "Hatua za Kwanza", "Kamilisha changamoto yako ya kwanza",
            iconKey = "footprints", xpReward = 25, coinReward = 5),
        Achievement(WEEK_WARRIOR, "Shujaa wa Wiki", "Fikia mfuatano wa siku 7",
            iconKey = "flame", xpReward = 100, coinReward = 20),
        Achievement(MONTH_MASTER, "Bingwa wa Mwezi", "Fikia mfuatano wa siku 30",
            iconKey = "trophy", xpReward = 500, coinReward = 100),
        Achievement(CENTURION, "Karne Moja", "Fikia mfuatano wa siku 100",
            iconKey = "crown", xpReward = 2000, coinReward = 500),
        Achievement(CHALLENGE_ROOKIE, "Mwanachama Mpya", "Kamilisha changamoto 5",
            iconKey = "star", xpReward = 50, coinReward = 10),
        Achievement(CHALLENGE_REGULAR, "Mshiriki wa Kawaida", "Kamilisha changamoto 25",
            iconKey = "star", xpReward = 200, coinReward = 50),
        Achievement(CHALLENGE_LEGEND, "Hadithi ya Changamoto", "Kamilisha changamoto 100",
            iconKey = "star", xpReward = 1000, coinReward = 250),
        Achievement(WORD_COLLECTOR_10, "Mkusanya Maneno I", "Jifunze maneno 10 mapya",
            iconKey = "book", xpReward = 25, coinReward = 5),
        Achievement(WORD_COLLECTOR_50, "Mkusanya Maneno II", "Jifunze maneno 50 mapya",
            iconKey = "book", xpReward = 100, coinReward = 25),
        Achievement(WORD_COLLECTOR_200, "Mkusanya Maneno III", "Jifunze maneno 200 mapya",
            iconKey = "book", xpReward = 500, coinReward = 100),
        Achievement(QUIZ_SHARPSHOOTER, "Mpiga Shabaha", "Pata jibu sahihi 100%",
            iconKey = "target", xpReward = 50, coinReward = 10),
        Achievement(LEVEL_5, "Ngazi ya 5", "Fikia ngazi ya 5",
            iconKey = "medal", xpReward = 50, coinReward = 10),
        Achievement(LEVEL_10, "Ngazi ya 10", "Fikia ngazi ya 10",
            iconKey = "medal", xpReward = 150, coinReward = 30),
        Achievement(LEVEL_25, "Ngazi ya 25", "Fikia ngazi ya 25",
            iconKey = "medal", xpReward = 500, coinReward = 100),
    )

    fun byId(id: String): Achievement? = ALL.firstOrNull { it.id == id }
}
