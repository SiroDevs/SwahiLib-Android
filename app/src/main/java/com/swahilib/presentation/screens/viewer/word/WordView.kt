package com.swahilib.presentation.screens.viewer.word

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.swahilib.presentation.components.action.CollapsingHeader
import com.swahilib.presentation.components.general.MeaningsView
import com.swahilib.presentation.theme.ThemeColors

@Composable
fun WordView(
    modifier: Modifier = Modifier,
    title: String,
    conjugation: String,
    meanings: List<String>,
    synonyms: List<String>
) {
    val scrollState = rememberLazyListState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ThemeColors.accent0)
    ) {
        LazyColumn(state = scrollState) {
            item {
                CollapsingHeader(title = title)
            }
            item {
                Column(modifier = Modifier.padding(10.dp)) {
                    if (meanings.isNotEmpty()) {
                        MeaningsView(meanings = meanings)
                    }

                    if (conjugation.isNotEmpty()) {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append("Mnyambuliko: ")
                                }
                                withStyle(style = SpanStyle(fontStyle = FontStyle.Italic)) {
                                    append(conjugation)
                                }
                            },
                            fontSize = 20.sp,
                            color = ThemeColors.primary1,
                            modifier = Modifier.padding(start = 10.dp)
                        )
                    }

                    if (synonyms.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = if (synonyms.size == 1) "KISAWE" else "VISAWE ${synonyms.size}",
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold,
                            color = ThemeColors.primary1,
                            modifier = Modifier.padding(start = 10.dp)
                        )
                        WordSynonyms(synonyms = synonyms)
                    }
                }
            }
        }
    }
}

@Composable
fun WordSynonyms(synonyms: List<String>) {
    val sortedSynonyms = remember(synonyms) { synonyms.sortedBy { it.lowercase() } }

    Column(modifier = Modifier.padding(10.dp)) {

        Divider(
            color = ThemeColors.primary1.copy(alpha = 0.3f),
            thickness = 1.dp
        )

        sortedSynonyms.forEachIndexed { index, synonym ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Icon(
                    Icons.Default.ArrowCircleRight,
                    contentDescription = null,
                    tint = ThemeColors.primary1
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = synonym,
                    fontSize = 20.sp,
                    color = ThemeColors.primary1
                )
            }

            if (index < synonyms.lastIndex) {
                Divider(
                    color = ThemeColors.primary1.copy(alpha = 0.3f),
                    thickness = 1.dp
                )
            }
        }

        Divider(
            color = ThemeColors.primary1.copy(alpha = 0.3f),
            thickness = 1.dp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WordViewPreview() {
    WordView(
        title = "fuata",
        conjugation = "fuatana,  fuatia, fuatika, fuatisha, fuatiwa, fuatwa",
        meanings = listOf("andama mtu kwa nyuma, -wa pamoja na: Mtoto anamfuata baba yake kila aendapo.", "kwenda kule anakokwenda au alikokwenda mtu: John alimfuata mkewe Uingereza.", "iga matendo au tabia za mtu: Bwana huyu anafuata tabia za baba yake.","kuwa mwumini wa imani fulani"),
        synonyms = listOf("ambatana", "andama chanjari")
    )
}
