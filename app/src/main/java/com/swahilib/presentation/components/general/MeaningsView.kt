package com.swahilib.presentation.components.general

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.text.*
import com.swahilib.presentation.theme.ThemeColors

@Composable
fun MeaningsView(meanings: List<String>) {
    Column {
        meanings.forEachIndexed { index, meaning ->
            val parts = meaning.split(":")
            val maana = parts.first().trim()
            val mfano = parts.getOrNull(1)?.trim()

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = buildString {
                            if (meanings.size > 1) {
                                append("${index + 1}. ")
                            }
                            append(maana)
                        },
                        fontSize = 22.sp,
                        color = ThemeColors.primary1,
                        modifier = Modifier.padding(start = 5.dp, bottom = if (mfano != null) 8.dp else 0.dp)
                    )

                    if (!mfano.isNullOrEmpty()) {
                        Divider(
                            color = ThemeColors.primary1.copy(alpha = 0.3f),
                            thickness = 1.dp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Text(
                            text = buildAnnotatedString {
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append("Mfano: ")
                                }
                                withStyle(style = SpanStyle(fontStyle = FontStyle.Italic)) {
                                    append(mfano)
                                }
                            },
                            fontSize = 18.sp,
                            color = ThemeColors.primary1,
                            modifier = Modifier.padding(start = 15.dp)
                        )
                    }
                }
            }
        }
    }
}
