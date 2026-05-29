package com.swahilib.core.ui.components.listitems

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.swahilib.core.common.utils.cleanMeaning
import com.swahilib.core.database.model.WordEntity
import com.swahilib.core.ui.sample.SampleWords

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WordItem(
    modifier: Modifier = Modifier,
    word: WordEntity,
    showSidebar: Boolean = false,
    onTap: (() -> Unit)? = null,
    onLike: (() -> Unit)? = null,
    timestamp: String? = null,
) {
    var showMenu by remember { mutableStateOf(false) }

    val meaning = remember(word.meaning) {
        val cleaned = cleanMeaning(word.meaning ?: "")
        val contents = cleaned.split("|")
        val extra = contents.getOrNull(0)?.split(":") ?: emptyList()
        var result = if (extra.isNotEmpty()) extra[0].trim() else ""
        if (contents.size > 1) {
            val extra2 = contents[1].split(":")
            result = "$result\n${extra2.getOrNull(0)?.trim() ?: ""}"
        }
        result
    }

    val synonyms = remember(word.synonyms) {
        word.synonyms?.split(',')?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList()
    }

    Box(modifier = modifier.padding(start = if (showSidebar) 75.dp else 0.dp)) {
        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
            DropdownMenuItem(
                text = { Text(if (word.liked) "Ondoa Upendo" else "Penda") },
                leadingIcon = {
                    Icon(
                        if (word.liked) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = if (word.liked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                },
                onClick = { onLike?.invoke(); showMenu = false }
            )
        }

        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 3.dp)
                .combinedClickable(
                    onClick = { onTap?.invoke() },
                    onLongClick = { showMenu = true }
                ),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(48.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            if (word.liked) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.primaryContainer
                        )
                )
                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = word.title ?: "",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        if (word.liked) {
                            Icon(
                                Icons.Filled.Favorite,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    if (meaning.isNotEmpty()) {
                        Text(
                            text = meaning,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    if (synonyms.isNotEmpty()) {
                        LazyRow(
                            modifier = Modifier.padding(top = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(synonyms.take(3)) { synonym ->
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = MaterialTheme.colorScheme.secondaryContainer
                                ) {
                                    Text(
                                        text = synonym,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                    }

                    timestamp?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewWordItem() {
    WordItem(word = SampleWords[0], onTap = {}, onLike = {})
}
