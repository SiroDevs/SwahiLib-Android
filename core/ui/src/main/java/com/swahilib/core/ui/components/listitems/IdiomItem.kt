package com.swahilib.core.ui.components.listitems

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.swahilib.core.common.utils.cleanMeaning
import com.swahilib.core.database.model.IdiomEntity
import com.swahilib.core.ui.sample.SampleIdioms

@Composable
fun IdiomItem(
    modifier: Modifier = Modifier,
    idiom: IdiomEntity,
    showSidebar: Boolean = false,
    onTap: (() -> Unit)? = null,
) {
    val titleTxtStyle = MaterialTheme.typography.titleLarge.copy(
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 1.2.em
    )

    val bodyTxtStyle = MaterialTheme.typography.bodySmall.copy(
        fontSize = 18.sp
    )

    val meaning = remember(idiom.meaning) {
        val cleaned = cleanMeaning(idiom.meaning ?: "")
        val contents = cleaned.split("|")
        val extra = contents.getOrNull(0)?.split(":") ?: emptyList()
        var result = if (extra.isNotEmpty()) " ~ ${extra[0].trim()}." else ""

        if (contents.size > 1) {
            val extra2 = contents[1].split(":")
            result = "$result\n ~ ${extra2.getOrNull(0)?.trim()}."
        }
        result
    }

    Box(
        modifier = modifier.padding(start = if (showSidebar) 75.dp else 0.dp)
    ) {
        ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(5.dp)
            .clickable { onTap?.invoke() },
        elevation = CardDefaults.cardElevation(5.dp),
    ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = idiom.title ?: "",
                    style = titleTxtStyle,
                    modifier = Modifier.padding(bottom = 5.dp)
                )

                if (meaning.isNotEmpty()) {
                    Text(
                        text = meaning,
                        style = bodyTxtStyle,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewIdiomItem() {
    IdiomItem(
        idiom = SampleIdioms[1],
        onTap = {},
    )
}