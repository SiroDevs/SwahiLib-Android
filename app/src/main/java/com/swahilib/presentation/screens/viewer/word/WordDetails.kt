package com.swahilib.presentation.screens.viewer.word

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleRight
import androidx.compose.material3.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import com.swahilib.presentation.components.general.MeaningsView
import com.swahilib.presentation.theme.ThemeColors

@Composable
fun WordDetails(
    meanings: List<String>,
    synonyms: List<String>
) {
    Column(modifier = Modifier.padding(10.dp)) {
        if (meanings.isNotEmpty()) {
            MeaningsView(meanings = meanings)
        }

        if (synonyms.isNotEmpty()) {
            Spacer(modifier = Modifier.height(15.dp))
            Text(
                text = "Visawe",
                fontSize = 25.sp,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold,
                color = ThemeColors.primary1
            )
            synonyms.forEach { synonym ->
                WordSynonym(synonym = synonym)
            }
        }
    }
}

@Composable
fun WordSynonym(synonym: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.ArrowCircleRight, contentDescription = null, tint = ThemeColors.primary1)
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = synonym,
                fontSize = 20.sp,
                color = ThemeColors.primary1
            )
        }
    }
}
