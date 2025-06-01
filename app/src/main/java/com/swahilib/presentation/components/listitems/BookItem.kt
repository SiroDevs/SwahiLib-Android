package com.swahilib.presentation.components.listitems

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.swahilib.core.utils.refineTitle
import com.swahilib.data.models.Word
import com.swahilib.domain.entities.Selectable
import com.swahilib.presentation.theme.ThemeColors

@Composable
fun BookItem(
    item: Selectable<Word>,
    onClick: (Selectable<Word>) -> Unit
) {
    val backgroundColor = if (item.isSelected) ThemeColors.primary else Color.White
    val contentColor = if (item.isSelected) Color.White else Color.Black

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .clickable { onClick(item) },
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            contentColor = contentColor,
        ),
        elevation = CardDefaults.cardElevation(5.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (item.isSelected) Icons.Filled.RadioButtonChecked else Icons.Filled.RadioButtonUnchecked,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.padding(end = 12.dp)
            )
            Column {
                Text(
                    text = refineTitle(item.data.title),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
                Text(
                    text = "${item.data.songs} ${item.data.meaning}",
                    fontSize = 18.sp,
                    color = contentColor
                )
            }
        }
    }
}
//
//@Preview(showBackground = true)
//@Composable
//fun PreviewBookItem() {
//    val sampleBook = Word(
//        bookId = 1,
//        bookNo = 1,
//        created = "2023-01-01",
//        enabled = true,
//        position = 1,
//        songs = 12,
//        subTitle = "worship",
//        title = "Songs of Worship",
//        user = 42
//    )
//
//    val selectableBook = Selectable(data = sampleBook, isSelected = true)
//
//    BookItem(
//        item = selectableBook,
//        onClick = {}
//    )
//}
