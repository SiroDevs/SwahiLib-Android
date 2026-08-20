package com.swahilib.feature.donation.view.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.swahilib.core.common.utils.PayConstants
import com.swahilib.feature.donation.R

@Composable
fun DiyDonationSection(
    onItemCopied: (item: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val clipboardManager = LocalClipboardManager.current

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = "Support via Mpesa or Paypal",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(Modifier.height(10.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
        ) {
            DiyBlock(
                itemLabel = "Donate to M-Pesa Number",
                iconRes = R.drawable.mpesa,
                type = DiyBlockType.Digits("+${PayConstants.MPESA_NUMBER}"),
                onCopyClick = {
                    clipboardManager.setText(
                        AnnotatedString("+${PayConstants.MPESA_NUMBER}")
                    )
                    onItemCopied("M-Pesa Number")
                },
            )
        }

        Card(
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
        ) {
            Column() {
                Text(
                    text = "Donate to M-Pesa PayBill",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(10.dp)
                )

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant,
                    modifier = Modifier.padding(10.dp)
                )

                DiyBlock(
                    itemLabel = "Paybill Number",
                    iconRes = R.drawable.mpesa,
                    type = DiyBlockType.Digits(PayConstants.PAYBILL_NUMBER),
                    onCopyClick = {
                        clipboardManager.setText(
                            AnnotatedString(PayConstants.PAYBILL_NUMBER)
                        )
                        onItemCopied("Paybill Number")
                    },
                )

                DiyBlock(
                    itemLabel = "Account Number",
                    iconRes = R.drawable.mpesa,
                    type = DiyBlockType.Digits(PayConstants.ACCOUNT_NUMBER),
                    onCopyClick = {
                        clipboardManager.setText(
                            AnnotatedString(PayConstants.ACCOUNT_NUMBER)
                        )
                        onItemCopied("Account Number")
                    },
                )
            }
        }

        Card(
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
        ) {
            DiyBlock(
                itemLabel = "PayPal",
                iconRes = R.drawable.paypal,
                type = DiyBlockType.Link(PayConstants.PAYPAL_ADDRESS),
            )
        }
    }
}

sealed interface DiyBlockType {
    data class Digits(val value: String) : DiyBlockType
    data class Link(val value: String) : DiyBlockType
}

@Composable
private fun DiyBlock(
    itemLabel: String,
    iconRes: Int,
    type: DiyBlockType,
    onCopyClick: (() -> Unit)? = null,
) {
    val uriHandler = LocalUriHandler.current
    Row(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = "$itemLabel logo",
                modifier = Modifier.size(50.dp),
                contentScale = ContentScale.Fit,
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = itemLabel,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                ),
                color = MaterialTheme.colorScheme.onSurface,
            )

            when (type) {
                is DiyBlockType.Digits -> {
                    Text(
                        text = truncateDigits(type.value),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                is DiyBlockType.Link -> {
                    Text(
                        text = type.value,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                    )
                }
            }
        }

        IconButton(
            onClick = {
                when (type) {
                    is DiyBlockType.Digits -> onCopyClick?.invoke()
                    is DiyBlockType.Link -> uriHandler.openUri(type.value)
                }
            },
        ) {
            Icon(
                imageVector = when (type) {
                    is DiyBlockType.Digits -> Icons.Filled.ContentCopy
                    is DiyBlockType.Link -> Icons.Filled.Link
                },
                contentDescription = when (type) {
                    is DiyBlockType.Digits -> "Copy $itemLabel"
                    is DiyBlockType.Link -> "Open $itemLabel link"
                },
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private fun truncateDigits(value: String?): String {
    if (value.isNullOrEmpty()) return ""
    return if (value.length <= 8) value else "${value.take(5)}....${value.takeLast(3)}"
}