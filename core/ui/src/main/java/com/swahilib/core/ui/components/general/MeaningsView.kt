package com.swahilib.core.ui.components.general

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MeaningsView(meanings: List<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        meanings.forEachIndexed { index, meaning ->
            val parts = meaning.split(":")
            val maana = parts.first().trim()
            val example = parts.getOrNull(1)?.trim()

            if (maana.isEmpty()) return@forEachIndexed

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.Top) {
                        if (meanings.size > 1) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(end = 10.dp, top = 2.dp)
                            ) {
                                Text(
                                    text = "${index + 1}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                )
                            }
                        }
                        Text(
                            text = maana,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (!example.isNullOrEmpty()) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 10.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                        )
                        Text(
                            text = buildAnnotatedString {
                                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("Mfano: ") }
                                withStyle(SpanStyle(fontStyle = FontStyle.Italic)) { append(example) }
                            },
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 17.sp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                        )
                    }
                }
            }
        }
    }
}
