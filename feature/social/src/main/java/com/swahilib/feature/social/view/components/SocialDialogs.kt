package com.swahilib.feature.social.view.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.swahilib.core.engagement.model.ActivityType
import com.swahilib.core.engagement.model.Difficulty
import com.swahilib.core.social.models.Friend
import com.swahilib.feature.social.utils.challengeableActivityTypes
import com.swahilib.feature.social.utils.difficultyTitle
import com.swahilib.feature.social.utils.titleFor

@Composable
fun AddFriendDialog(
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit,
    isSubmitting: Boolean,
    error: String?,
) {
    var code by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ongeza Rafiki") },
        text = {
            Column {
                Text(
                    "Ingiza msimbo wa rafiki yako (herufi 6).",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it.uppercase().take(6) },
                    singleLine = true,
                    label = { Text("Msimbo wa Rafiki") },
                    modifier = Modifier.fillMaxWidth(),
                )
                if (error != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onSubmit(code) }, enabled = code.length == 6 && !isSubmitting) {
                Text(if (isSubmitting) "Inatuma..." else "Tuma Ombi")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Ghairi") } },
    )
}

@Composable
fun CreateChallengeDialog(
    friends: List<Friend>,
    onDismiss: () -> Unit,
    onSubmit: (opponentId: String, activityType: String, difficulty: String) -> Unit,
    isSubmitting: Boolean,
    error: String?,
) {
    var selectedFriendId by remember { mutableStateOf<String?>(null) }
    var selectedActivity by remember { mutableStateOf(ActivityType.VOCABULARY_QUIZ) }
    var selectedDifficulty by remember { mutableStateOf(Difficulty.BEGINNER) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Unda Changamoto") },
        text = {
            Column {
                if (friends.isEmpty()) {
                    Text(
                        "Ongeza rafiki kwanza ili uweze kumpatia changamoto.",
                        style = MaterialTheme.typography.bodySmall,
                    )
                } else {
                    Text("Chagua Rafiki", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold))
                    Spacer(Modifier.height(8.dp))
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(friends, key = { it.friendshipId }) { friend ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                FilterChip(
                                    selected = selectedFriendId == friend.profile.userId,
                                    onClick = { selectedFriendId = friend.profile.userId },
                                    label = { Text(friend.profile.displayName) },
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text("Chagua Mchezo", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold))
                    Spacer(Modifier.height(8.dp))
                    ChipFlow(challengeableActivityTypes.map { it to titleFor(it) }, selectedActivity) {
                        selectedActivity = it
                    }
                    Spacer(Modifier.height(8.dp))
                    Text("Chagua Ugumu", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold))
                    Spacer(Modifier.height(8.dp))
                    ChipFlow(Difficulty.entries.map { it to difficultyTitle(it.name) }, selectedDifficulty) {
                        selectedDifficulty = it
                    }
                    if (error != null) {
                        Spacer(Modifier.height(8.dp))
                        Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    selectedFriendId?.let { onSubmit(it, selectedActivity.name, selectedDifficulty.name) }
                },
                enabled = selectedFriendId != null && !isSubmitting,
            ) { Text(if (isSubmitting) "Inaunda..." else "Anzisha") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Ghairi") } },
    )
}

@Composable
fun EnterScoreDialog(
    onDismiss: () -> Unit,
    onSubmit: (Int) -> Unit,
) {
    var text by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Weka Alama Yako") },
        text = {
            Column {
                Text(
                    "Baada ya kucheza, ingiza sign uliyopata.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = text,
                    onValueChange = { v -> if (v.all { it.isDigit() }) text = v },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    label = { Text("Alama") },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { text.toIntOrNull()?.let(onSubmit) },
                enabled = text.toIntOrNull() != null,
            ) { Text("Weka") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Ghairi") } },
    )
}

@Composable
private fun <T> ChipFlow(options: List<Pair<T, String>>, selected: T, onSelect: (T) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        options.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { (value, label) ->
                    FilterChip(
                        selected = selected == value,
                        onClick = { onSelect(value) },
                        label = { Text(label) },
                    )
                }
            }
        }
    }
}
