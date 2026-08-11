package com.swahilib.feature.quiz.view.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.swahilib.core.games.model.QuizQuestion
import kotlin.collections.plus

@Composable
fun MatchWords(question: QuizQuestion, onMatches: (Map<String, String>) -> Unit) {
    var selectedLeft by remember(question.id) { mutableStateOf<String?>(null) }
    var pairs by remember(question.id) { mutableStateOf<Map<String, String>>(emptyMap()) }
    var submitted by remember(question.id) { mutableStateOf(false) }

    Text(
        "Gusa neno, kisha gusa maana yake.",
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Spacer(Modifier.height(12.dp))
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            question.matchLeft.forEach { left ->
                MatchChip(
                    text = left.text,
                    matched = pairs.containsKey(left.id),
                    selected = selectedLeft == left.id,
                    enabled = !submitted && !pairs.containsKey(left.id),
                    onClick = { selectedLeft = left.id },
                )
            }
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            question.matchRight.forEach { right ->
                val alreadyUsed = pairs.containsValue(right.id)
                MatchChip(
                    text = right.text,
                    matched = alreadyUsed,
                    selected = false,
                    enabled = !submitted && !alreadyUsed && selectedLeft != null,
                    onClick = {
                        selectedLeft?.let { left ->
                            pairs = pairs + (left to right.id)
                            selectedLeft = null
                            if (pairs.size == question.matchLeft.size) {
                                submitted = true
                                onMatches(pairs)
                            }
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun MatchChip(text: String, matched: Boolean, selected: Boolean, enabled: Boolean, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        enabled = enabled,
        colors = when {
            matched -> CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            selected -> CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            else -> CardDefaults.cardColors()
        },
    ) {
        Text(text, Modifier.padding(12.dp), style = MaterialTheme.typography.bodyMedium)
    }
}