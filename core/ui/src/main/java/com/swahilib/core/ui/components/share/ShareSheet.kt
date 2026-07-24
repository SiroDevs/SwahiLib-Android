package com.swahilib.core.ui.components.share

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

data class ShareData(
    val emoji: String,
    val headerLabel: String,
    val title: String,
    val meaning: String,
    val english: String? = null,
    val synonyms: List<String> = emptyList(),
) {
    val textToShare: String
        get() = buildString {
            append(headerLabel).append("\n\n")
            append(title)
            if (!english.isNullOrBlank()) {
                append(" ($english)")
            }
            append("\n")
            append("Maana: ").append(meaning).append("\n")
            if (!english.isNullOrBlank()) {
                append("English: ").append(english).append("\n")
            }
            if (synonyms.isNotEmpty()) {
                append("\nVisawe (${synonyms.size}): ").append(synonyms.joinToString(", "))
                    .append("\n")
            }
            append("\nHisani: SwahiLib · Kamusi ya Kiswahili")
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareSheet(
    shareData: ShareData,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var capturing by remember { mutableStateOf(false) }
    val cardGraphicsLayer = rememberGraphicsLayer()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 36.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Shiriki",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )

            ShareContentCard(
                emoji = shareData.emoji,
                headerLabel = shareData.headerLabel,
                title = shareData.title,
                meaning = shareData.meaning,
                english = shareData.english,
                synonyms = shareData.synonyms,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(20.dp),
                    )
                    .drawWithContent {
                        cardGraphicsLayer.record { this@drawWithContent.drawContent() }
                        drawLayer(cardGraphicsLayer)
                    },
            )

            Spacer(Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedButton(
                    onClick = {
                        scope.launch { sheetState.hide(); onDismiss() }
                        ShareHelper.shareText(context, shareData.textToShare)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Icon(Icons.Default.TextFields, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Maandishi")
                }

                Button(
                    onClick = {
                        if (capturing) return@Button
                        capturing = true
                        scope.launch {
                            try {
                                val bitmap = cardGraphicsLayer.toImageBitmap().asAndroidBitmap()
                                sheetState.hide()
                                onDismiss()
                                ShareHelper.shareBitmap(
                                    context = context,
                                    bitmap = bitmap,
                                    fileName = "swahilib_${
                                        shareData.title.take(20).replace(" ", "_")
                                    }.png",
                                )
                            } finally {
                                capturing = false
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                    ),
                    enabled = !capturing,
                ) {
                    Icon(Icons.Default.Image, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(if (capturing) "..." else "Picha")
                }
            }
        }
    }
}
