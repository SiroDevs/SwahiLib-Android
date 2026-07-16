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

/**
 * All data needed to populate [ShareContentCard] and compose the text-share body.
 *
 * Both the text share and the image share render the same structure:
 *
 * ```
 * Neno la Kiswahili
 *
 * Chaa
 * Maana: Kikundi cha ...
 * English: farming Group
 *
 * Visawe (3): kikosi, kikundi
 *
 * Hisani: SwahiLib · Kamusi ya Kiswahili
 * ```
 *
 * [english] and [synonyms] are optional — idioms and sayings don't have either,
 * proverbs don't have [english] — so those lines are simply omitted when absent.
 */
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
            append(title).append("\n")
            append("Maana: ").append(meaning).append("\n")
            if (!english.isNullOrBlank()) {
                append("English: ").append(english).append("\n")
            }
            if (synonyms.isNotEmpty()) {
                append("\nVisawe (${synonyms.size}): ").append(synonyms.joinToString(", ")).append("\n")
            }
            append("\nHisani: SwahiLib · Kamusi ya Kiswahili")
        }
}

/**
 * Bottom sheet presenting a [ShareContentCard] preview with two share options:
 *  - "Maandishi" — plain text via system share sheet
 *  - "Picha" — the card itself, rendered as a PNG
 *
 * The image share captures ONLY the [ShareContentCard] (via a [androidx.compose.ui.graphics.layer.GraphicsLayer]
 * recorded off the card's own draw pass), not a screenshot of the whole screen —
 * so the shared image always matches the "Neno la Kiswahili / title / Maana: … /
 * English: … / Visawe (N): … / Hisani: …" format regardless of what's behind the sheet.
 */
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

            // Preview card — also the exact thing captured for the image share.
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
                        // Record this card's draw output into its own layer so it
                        // can be rasterised on demand, independent of the rest of
                        // the screen behind the bottom sheet.
                        cardGraphicsLayer.record { this@drawWithContent.drawContent() }
                        drawLayer(cardGraphicsLayer)
                    },
            )

            Spacer(Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // ── Text share ──
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

                // ── Image share ──
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
                                    fileName = "swahilib_${shareData.title.take(20).replace(" ", "_")}.png",
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
