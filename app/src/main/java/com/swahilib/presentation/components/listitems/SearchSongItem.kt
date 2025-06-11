package com.swahilib.presentation.components.listitems

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.swahilib.core.utils.*
import com.swahilib.data.models.Word
import com.swahilib.presentation.theme.ThemeColors

@Composable
fun SearchWordItem(
    word: Word,
    isSelected: Boolean,
    isSearching: Boolean,
    onClick: (Word) -> Unit,
) {
    val verses = remember(word.meaning) { word.meaning.split("##") }
    val hasChorus = "CHORUS" in word.meaning
    val chorusText = if (hasChorus) "Chorus" else ""
    val verseCount = verses.size - if (hasChorus) 1 else 0
    val versesText = if (verses.size == 1) "$verseCount V" else "${verseCount} Vs"

    Box(
        modifier = Modifier.padding(horizontal = 10.dp)
    )
    {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (isSelected) ThemeColors.primary else Color.Transparent)
                .clickable(onClick = { onClick(word) })
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = word.title,
                    maxLines = 1,
                    modifier = Modifier.weight(1f),
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                TagItem(tagText = versesText)
                if (hasChorus) {
                    Spacer(modifier = Modifier.width(3.dp))
                    TagItem(tagText = chorusText)
                }
                Spacer(modifier = Modifier.width(3.dp))
//                Icon(
//                    imageVector = if (word.liked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
//                    contentDescription = null,
//                    //tint = ThemeColors.foreColorBW()
//                )
            }

            Spacer(modifier = Modifier.height(3.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = refineContent(verses.firstOrNull().orEmpty()),
                    style = TextStyle(fontSize = 16.sp),
                    maxLines = 2,
                    modifier = Modifier.weight(1f)
                )
            }
//        if (isSearching) {
//            Spacer(modifier = Modifier.height(4.dp))
//            TagItem(tagText = refineTitle(word.book), height = height)
//        }
            Spacer(modifier = Modifier.height(3.dp))
        }
    }
}

/*@Preview(showBackground = true)
@Composable
fun PreviewSearchWordItem() {
    SearchWordItem(
        word = SampleWords[3],
        onClick = {},
        isSelected = false,
        isSearching = false,
    )
}
*/