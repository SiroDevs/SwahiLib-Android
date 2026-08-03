package com.swahilib.core.engagement.engine

import com.swahilib.core.engagement.model.ActivityType
import com.swahilib.core.engagement.model.ChallengeActivity
import com.swahilib.core.engagement.model.ChallengeScope
import com.swahilib.core.engagement.model.Difficulty
import java.util.UUID

/**
 * Pure functions that generate the *shape* of a challenge (title + activity
 * list). ChallengeEngine turns these into persisted rows, adds an XP reward,
 * and wires up expiry. Sprint 2 games plug in by extending [ActivityType] and
 * writing a template here; nothing else in Sprint 1 needs to change.
 */
data class ChallengeTemplate(
    val scope: ChallengeScope,
    val title: String,
    val description: String,
    val difficulty: Difficulty,
    val activities: List<ChallengeActivity>,
) {
    val activityXp: Int get() = activities.sumOf { it.xpReward }
}

object ChallengeTemplates {

    fun daily(periodKey: String, difficulty: Difficulty = Difficulty.BEGINNER): ChallengeTemplate {
        val types = listOf(
            ActivityType.DAILY_READ to "Soma Neno la Siku",
            ActivityType.VOCABULARY_QUIZ to "Jaribio la Msamiati",
            ActivityType.PROVERB_CHALLENGE to "Changamoto ya Methali",
        )
        return ChallengeTemplate(
            scope = ChallengeScope.DAILY,
            title = "Changamoto ya Leo",
            description = "Kamilisha shughuli tatu fupi ndani ya dakika tano.",
            difficulty = difficulty,
            activities = types.mapIndexed { index, (type, title) ->
                ChallengeActivity(
                    id = activityId(periodKey, ChallengeScope.DAILY, index),
                    type = type,
                    title = title,
                    estimatedSeconds = if (type == ActivityType.DAILY_READ) 60 else 120,
                    xpReward = RewardRules.activityXp(type, difficulty),
                )
            },
        )
    }

    fun weekly(periodKey: String): ChallengeTemplate {
        val activities = listOf(
            ActivityType.VOCABULARY_QUIZ to "Kamilisha jaribio 3 la msamiati",
            ActivityType.WORD_BUILDER to "Jenga maneno 15",
            ActivityType.SENTENCE_BUILDER to "Panga sentensi 10",
            ActivityType.CROSSWORD to "Kamilisha msalaba mmoja",
        )
        return ChallengeTemplate(
            scope = ChallengeScope.WEEKLY,
            title = "Changamoto ya Wiki",
            description = "Kamilisha shughuli nne ndani ya wiki.",
            difficulty = Difficulty.INTERMEDIATE,
            activities = activities.mapIndexed { index, (type, title) ->
                ChallengeActivity(
                    id = activityId(periodKey, ChallengeScope.WEEKLY, index),
                    type = type,
                    title = title,
                    estimatedSeconds = 240,
                    xpReward = RewardRules.activityXp(type, Difficulty.INTERMEDIATE),
                )
            },
        )
    }

    fun monthly(periodKey: String): ChallengeTemplate {
        val activities = listOf(
            ActivityType.VOCABULARY_QUIZ to "Kamilisha majaribio 12",
            ActivityType.WORD_SEARCH to "Kamilisha michezo 6 ya kutafuta maneno",
            ActivityType.CROSSWORD to "Kamilisha misalaba 4",
            ActivityType.SENTENCE_BUILDER to "Panga sentensi 40",
            ActivityType.PROVERB_CHALLENGE to "Kamilisha changamoto 10 za methali",
        )
        return ChallengeTemplate(
            scope = ChallengeScope.MONTHLY,
            title = "Changamoto ya Mwezi",
            description = "Ongeza umahiri wako wa Kiswahili kwa shughuli tano kubwa.",
            difficulty = Difficulty.ADVANCED,
            activities = activities.mapIndexed { index, (type, title) ->
                ChallengeActivity(
                    id = activityId(periodKey, ChallengeScope.MONTHLY, index),
                    type = type,
                    title = title,
                    estimatedSeconds = 600,
                    xpReward = RewardRules.activityXp(type, Difficulty.ADVANCED),
                )
            },
        )
    }

    /** Ad-hoc practice session; no expiry, not indexed to a period. */
    fun practice(difficulty: Difficulty = Difficulty.BEGINNER): ChallengeTemplate {
        val id = UUID.randomUUID().toString().take(8)
        val activities = listOf(
            ActivityType.VOCABULARY_QUIZ,
            ActivityType.WORD_BUILDER,
            ActivityType.SPELLING_CHALLENGE,
        )
        return ChallengeTemplate(
            scope = ChallengeScope.PRACTICE,
            title = "Kikao cha Mazoezi",
            description = "Kikao kifupi cha mazoezi ya bure.",
            difficulty = difficulty,
            activities = activities.mapIndexed { index, type ->
                ChallengeActivity(
                    id = "practice_${id}_$index",
                    type = type,
                    title = practiceTitle(type),
                    estimatedSeconds = 90,
                    xpReward = RewardRules.activityXp(type, difficulty),
                )
            },
        )
    }

    /** Holiday event - richer activity set, boosted rewards handled via ChallengeScope.SEASONAL in RewardRules. */
    fun holidayEvent(periodKey: String, event: com.swahilib.core.engagement.catalog.SeasonalEventDef, difficulty: Difficulty = Difficulty.INTERMEDIATE): ChallengeTemplate {
        val activities = listOf(
            ActivityType.VOCABULARY_QUIZ to "Jaribio maalum la sikukuu",
            ActivityType.PROVERB_CHALLENGE to "Methali za sikukuu",
            ActivityType.WORD_BUILDER to "Jenga maneno ya sherehe",
        )
        return ChallengeTemplate(
            scope = ChallengeScope.SEASONAL,
            title = "\uD83C\uDF89 ${event.title}",
            description = event.description,
            difficulty = difficulty,
            activities = activities.mapIndexed { index, (type, title) ->
                ChallengeActivity(
                    id = activityId(periodKey, ChallengeScope.SEASONAL, index),
                    type = type,
                    title = title,
                    estimatedSeconds = 150,
                    xpReward = RewardRules.activityXp(type, difficulty),
                )
            },
        )
    }

    /** Weekend Challenge - active Saturday/Sunday, keyed per ISO week so it's created once and stays put all weekend. */
    fun weekendChallenge(periodKey: String, difficulty: Difficulty = Difficulty.INTERMEDIATE): ChallengeTemplate {
        val activities = listOf(
            ActivityType.CROSSWORD to "Msalaba wa wikendi",
            ActivityType.WORD_SEARCH to "Tafuta maneno ya wikendi",
            ActivityType.SENTENCE_BUILDER to "Panga sentensi za wikendi",
        )
        return ChallengeTemplate(
            scope = ChallengeScope.SEASONAL,
            title = "\uD83C\uDF1E Changamoto ya Wikendi",
            description = "Changamoto maalum ya wikendi yenye zawadi za ziada!",
            difficulty = difficulty,
            activities = activities.mapIndexed { index, (type, title) ->
                ChallengeActivity(
                    id = activityId(periodKey, ChallengeScope.SEASONAL, index),
                    type = type,
                    title = title,
                    estimatedSeconds = 180,
                    xpReward = RewardRules.activityXp(type, difficulty),
                )
            },
        )
    }

    private fun practiceTitle(type: ActivityType): String = when (type) {
        ActivityType.VOCABULARY_QUIZ -> "Jaribio la maneno 5"
        ActivityType.WORD_BUILDER -> "Jenga maneno 3"
        ActivityType.SPELLING_CHALLENGE -> "Andika maneno 5"
        else -> type.name
    }

    private fun activityId(periodKey: String, scope: ChallengeScope, index: Int): String =
        "${scope.name.lowercase()}_${periodKey}_$index"
}
