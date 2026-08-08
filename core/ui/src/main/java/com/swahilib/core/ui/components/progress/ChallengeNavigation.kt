package com.swahilib.core.ui.components.progress

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.swahilib.core.common.utils.Routes
import com.swahilib.core.engagement.engine.ActivityRecommendation
import com.swahilib.core.engagement.model.ActivityType
import com.swahilib.core.engagement.model.Challenge
import com.swahilib.core.engagement.model.ChallengeActivity

/**
 * Maps a challenge activity to the route that plays it, or null when the activity has no
 * dedicated game screen (DAILY_READ, STREAK_VISIT, CUSTOM) and should just be marked complete
 * directly instead of navigating.
 *
 * Shared by every place that starts a challenge activity (Progress, Challenges, and the Home
 * engagement tab) specifically so a new [ActivityType] only needs a branch added *here* once -
 * this `when` has no `else`, so the compiler forces the update. Three separate copies of this
 * mapping is exactly how the SEASONAL/HANGMAN exhaustiveness gaps happened last sprint.
 */
fun routeForChallengeActivity(challenge: Challenge, activity: ChallengeActivity): String? =
    when (activity.type) {
        ActivityType.VOCABULARY_QUIZ ->
            Routes.quiz(challenge.id, activity.id, challenge.difficulty.name)
        ActivityType.PROVERB_CHALLENGE ->
            Routes.quiz(challenge.id, activity.id, challenge.difficulty.name, source = "PROVERBS")
        ActivityType.WORD_BUILDER ->
            Routes.wordBuilder(challenge.id, activity.id, challenge.difficulty.name)
        ActivityType.SENTENCE_BUILDER ->
            Routes.sentenceBuilder(challenge.id, activity.id, challenge.difficulty.name)
        ActivityType.SPELLING_CHALLENGE ->
            Routes.spelling(challenge.id, activity.id, challenge.difficulty.name)
        ActivityType.CROSSWORD ->
            Routes.crossword(challenge.id, activity.id, challenge.difficulty.name)
        ActivityType.WORD_SEARCH ->
            Routes.wordSearch(challenge.id, activity.id, challenge.difficulty.name)
        ActivityType.HANGMAN ->
            Routes.hangman(challenge.id, activity.id, challenge.difficulty.name)
        ActivityType.DAILY_READ, ActivityType.STREAK_VISIT, ActivityType.CUSTOM -> null
    }

/** Freeplay route + display title for a recommended activity type (raw string from the engine). */
fun routeAndTitleFor(type: String): Pair<String, String> = when (type) {
    "QUIZ" -> Routes.quizFreeplay() to "Jaribio la Msamiati"
    "WORD_BUILDER" -> Routes.wordBuilderFreeplay() to "Jenzi la Maneno"
    "SENTENCE_BUILDER" -> Routes.sentenceBuilderFreeplay() to "Panga Sentensi"
    "SPELLING" -> Routes.spellingFreeplay() to "Changamoto ya Tahajia"
    "CROSSWORD" -> Routes.crosswordFreeplay() to "Msalaba wa Maneno"
    "WORD_SEARCH" -> Routes.wordSearchFreeplay() to "Tafuta Maneno"
    "PROVERB" -> Routes.quizFreeplay(source = "PROVERBS") to "Changamoto ya Methali"
    "HANGMAN" -> Routes.hangmanFreeplay() to "Hangman"
    else -> Routes.quizFreeplay() to "Jaribio la Msamiati"
}

/** One row on the "Kwa Ajili Yako" recommendation list - reused by Challenges and the Home tab. */
@Composable
fun RecommendationRow(rec: ActivityRecommendation, onNavigate: (String) -> Unit) {
    val (route, title) = routeAndTitleFor(rec.type)
    Card(
        onClick = { onNavigate(route) },
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
            Text(
                rec.reason,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            rec.recentAccuracy?.let { acc ->
                Text(
                    "Usahihi wa hivi karibuni: ${(acc * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}
