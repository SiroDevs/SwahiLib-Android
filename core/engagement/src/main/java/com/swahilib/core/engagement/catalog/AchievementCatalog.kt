package com.swahilib.core.engagement.catalog

import com.swahilib.core.engagement.model.Achievement

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

    const val VOCAB_APPRENTICE = "vocab_apprentice"
    const val VOCAB_MASTER = "vocab_master"
    const val WORD_EXPERT = "word_expert"
    const val SENTENCE_MASTER = "sentence_master"
    const val CROSSWORD_CHAMPION = "crossword_champion"
    const val SUDOKU_WIZARD = "sudoku_wizard"
    const val SPELLING_CHAMPION = "spelling_champion"
    const val PROVERB_SAGE = "proverb_sage"
    const val PERFECT_STREAK_5 = "perfect_streak_5"
    const val GRAND_SLAM = "grand_slam"

    val ALL: List<Achievement> = listOf(
        Achievement(FIRST_STEPS, "Hatua za Kwanza", "Kamilisha changamoto yako ya kwanza",
            iconKey = "footprints", xpReward = 25, coinReward = 5),
        Achievement(WEEK_WARRIOR, "Shujaa wa Wiki", "Fikia mfuatano wa siku 7",
            iconKey = "flame", xpReward = 100, coinReward = 20),
        Achievement(MONTH_MASTER, "Shujaa wa Mwezi", "Fikia mfuatano wa siku 30",
            iconKey = "trophy", xpReward = 500, coinReward = 100),
        Achievement(CENTURION, "Karne Moja", "Fikia mfuatano wa siku 100",
            iconKey = "crown", xpReward = 2000, coinReward = 500),
        Achievement(CHALLENGE_ROOKIE, "Bingwa Mpya", "Kamilisha changamoto 5",
            iconKey = "star", xpReward = 50, coinReward = 10),
        Achievement(CHALLENGE_REGULAR, "Bingwa wa Kawaida", "Kamilisha changamoto 25",
            iconKey = "star", xpReward = 200, coinReward = 50),
        Achievement(CHALLENGE_LEGEND, "Legend wa Changamoto", "Kamilisha changamoto 100",
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
        Achievement(VOCAB_APPRENTICE, "Mkufunzi wa Msamiati", "Kamilisha majaribio 10 ya msamiati",
            iconKey = "book", xpReward = 75, coinReward = 15),
        Achievement(VOCAB_MASTER, "Bingwa wa Msamiati", "Kamilisha majaribio 50 ya msamiati",
            iconKey = "book", xpReward = 300, coinReward = 75),
        Achievement(WORD_EXPERT, "Mtaalamu wa Kujenga Maneno", "Kamilisha raundi 25 za Jenga Maneno",
            iconKey = "puzzle", xpReward = 200, coinReward = 50),
        Achievement(SENTENCE_MASTER, "Bingwa wa Sentensi", "Kamilisha raundi 25 za KuJenga Sentensi",
            iconKey = "puzzle", xpReward = 200, coinReward = 50),
        Achievement(CROSSWORD_CHAMPION, "Bingwa wa CrossWord", "Kamilisha misalaba 10 ya maneno",
            iconKey = "grid", xpReward = 250, coinReward = 60),
        Achievement(SUDOKU_WIZARD, "Wizard wa Kutafuta Maneno", "Kamilisha michezo 15 ya Kutafuta Maneno",
            iconKey = "grid", xpReward = 200, coinReward = 50),
        Achievement(SPELLING_CHAMPION, "Bingwa wa Tahajia", "Kamilisha raundi 25 za tahajia",
            iconKey = "pencil", xpReward = 200, coinReward = 50),
        Achievement(PROVERB_SAGE, "Mjuzi wa Methali", "Kamilisha changamoto 20 za methali",
            iconKey = "scroll", xpReward = 250, coinReward = 60),
        Achievement(PERFECT_STREAK_5, "Mkamilifu", "Pata alama kamili mara 5 katika michezo yoyote",
            iconKey = "target", xpReward = 150, coinReward = 40),
        Achievement(GRAND_SLAM, "Bingwa wa Michezo Yote", "Pata alama kamili angalau mara moja katika kila mchezo",
            iconKey = "crown", xpReward = 750, coinReward = 150),
    )

    fun byId(id: String): Achievement? = ALL.firstOrNull { it.id == id }
}
