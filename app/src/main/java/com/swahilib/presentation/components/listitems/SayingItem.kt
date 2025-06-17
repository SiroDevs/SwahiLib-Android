package com.swahilib.presentation.components.listitems

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.swahilib.core.utils.cleanMeaning
import com.swahilib.data.models.Saying
import com.swahilib.data.sample.SampleSayings

@Composable
fun SayingItem(
    saying: Saying,
    onTap: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val titleTxtStyle = MaterialTheme.typography.titleLarge.copy(
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 1.2.em
    )

    val bodyTxtStyle = MaterialTheme.typography.bodySmall.copy(
        fontSize = 18.sp
    )

    val meaning = remember(saying.meaning) {
        val cleaned = cleanMeaning(saying.meaning ?: "")
        val contents = cleaned.split("|")
        val extra = contents.getOrNull(0)?.split(":") ?: emptyList()
        var result = if (extra.isNotEmpty()) " ~ ${extra[0].trim()}." else ""

        if (contents.size > 1) {
            val extra2 = contents[1].split(":")
            result = "$result\n ~ ${extra2.getOrNull(0)?.trim()}."
        }
        result
    }

    val synonyms = remember(saying.synonyms) {
        saying.synonyms?.split(',')
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            ?: emptyList()
    }

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
                text = saying.title ?: "",
                style = titleTxtStyle,
                modifier = Modifier.padding(bottom = 5.dp)
            )

            if (meaning.isNotEmpty()) {
                Text(
                    text = meaning,
                    style = bodyTxtStyle,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(bottom = if (synonyms.isNotEmpty()) 8.dp else 0.dp)
                )
            }

            if (synonyms.isNotEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (synonyms.size == 1) "KISAWE:" else "VISAWE ${synonyms.size}:",
                        style = bodyTxtStyle.copy(fontWeight = FontWeight.Bold)
                    )

                    Spacer(Modifier.width(5.dp))

                    LazyRow(
                        modifier = Modifier
                            .weight(1f)
                            .height(35.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        items(synonyms) { synonym ->
                            TagView(tagText = synonym.toString())
                            Spacer(Modifier.width(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSayingItem() {
    SayingItem(
        saying = SampleSayings[3],
        onTap = {},
    )
}