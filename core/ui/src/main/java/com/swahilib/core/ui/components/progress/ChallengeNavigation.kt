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

fun routeAndTitleFor(type: String): Pair<String, String> = when (type) {
    "QUIZ" -> Routes.quizFreeplay() to "Jaribio la Msamiati"
    "WORD_BUILDER" -> Routes.wordBuilderFreeplay() to "Jenga Maneno"
    "SENTENCE_BUILDER" -> Routes.sentenceBuilderFreeplay() to "Jenga Sentensi"
    "SPELLING" -> Routes.spellingFreeplay() to "Tahajia (Spellcheck)"
    "CROSSWORD" -> Routes.crosswordFreeplay() to "CrossWord"
    "WORD_SEARCH" -> Routes.wordSearchFreeplay() to "Tafuta Maneno"
    "PROVERB" -> Routes.quizFreeplay(source = "PROVERBS") to "Changamoto ya Methali"
    "HANGMAN" -> Routes.hangmanFreeplay() to "Hangman"
    else -> Routes.quizFreeplay() to "Jaribio la Msamiati"
}

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
