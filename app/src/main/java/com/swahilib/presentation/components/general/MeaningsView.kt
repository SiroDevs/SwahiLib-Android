package com.swahilib.presentation.components.general

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.*
import com.swahilib.presentation.theme.ThemeColors

@Composable
fun MeaningsView(meanings: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            meanings.forEachIndexed { index, meaning ->
                val parts = meaning.split(":")
                Text(
                    text = buildAnnotatedString {
                        append("${index + 1}. ${parts.first()}")
                        if (parts.size > 1) {
                            append("\n")
                            withStyle(SpanStyle(fontStyle = FontStyle.Italic, fontWeight = FontWeight.Bold)) {
                                append("Mfano: ")
                            }
                            withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                                append(parts.last())
                            }
                        }
                    },
                    fontSize = 18.sp,
                    color = ThemeColors.primary1,
                    modifier = Modifier.padding(vertical = 5.dp)
                )
            }
        }
    }
}
