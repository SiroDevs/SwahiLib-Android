package com.swahilib.core.ui.components.listitems

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.swahilib.core.common.utils.cleanMeaning
import com.swahilib.core.database.model.ProverbEntity
import com.swahilib.core.ui.sample.SampleProverbs

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProverbItem(
    modifier: Modifier = Modifier,
    proverb: ProverbEntity,
    showSidebar: Boolean = false,
    onTap: (() -> Unit)? = null,
    onLike: (() -> Unit)? = null,
    timestamp: String? = null,
) {
    var showMenu by remember { mutableStateOf(false) }

    val meaning = remember(proverb.meaning) {
        val cleaned = cleanMeaning(proverb.meaning ?: "")
        val contents = cleaned.split("|")
        val extra = contents.getOrNull(0)?.split(":") ?: emptyList()
        if (extra.isNotEmpty()) extra[0].trim() else ""
    }

    Box(modifier = modifier.padding(start = if (showSidebar) 75.dp else 0.dp)) {
        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
            DropdownMenuItem(
                text = { Text(if (proverb.liked) "Ondoa kutoka kwa Vipendwa" else "Ongeza kwa Vipendwa") },
                leadingIcon = {
                    Icon(
                        if (proverb.liked) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = if (proverb.liked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
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
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
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
                        .height(42.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            if (proverb.liked) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.tertiary
                        )
                )
                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = proverb.title ?: "",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold, fontSize = 18.sp
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        if (proverb.liked) {
                            Icon(
                                Icons.Filled.Favorite, contentDescription = null,
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
fun PreviewProverbItem() {
    ProverbItem(proverb = SampleProverbs[1], onTap = {}, onLike = {})
}
