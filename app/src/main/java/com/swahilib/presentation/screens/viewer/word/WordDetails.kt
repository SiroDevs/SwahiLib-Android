package com.swahilib.presentation.screens.viewer.word

import android.graphics.fonts.FontStyle
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.swahilib.data.models.Word
import com.swahilib.presentation.theme.ThemeColors

@Composable
fun WordDetails(
    word: Word,
    meanings: List<String>,
    synonyms: List<String>
) {
    Column(modifier = Modifier.padding(10.dp)) {
        if (meanings.isNotEmpty()) {
            MeaningCard(meanings = meanings)
        }

        if (synonyms.isNotEmpty()) {
            Spacer(modifier = Modifier.height(15.dp))
            Text(
                text = "Visawe",
                fontSize = 25.sp,
//                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold,
                color = ThemeColors.primary1
            )
            synonyms.forEach { synonym ->
                SynonymCard(synonym = synonym)
            }
        }
    }
}
