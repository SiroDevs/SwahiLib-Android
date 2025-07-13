package com.swahilib.presentation.screens.viewer.word

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import com.swahilib.data.models.Word
import com.swahilib.presentation.components.action.CollapsingHeader
import com.swahilib.presentation.components.general.MeaningsView
import com.swahilib.presentation.theme.ThemeColors
import com.swahilib.presentation.viewmodels.WordViewModel

@Composable
fun WordView(
    modifier: Modifier = Modifier,
    viewModel: WordViewModel,
    title: String,
    conjugation: String,
    meanings: List<String>,
    synonyms: List<Word>
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
                        WordSynonyms(
                            synonyms = synonyms,
                            onSynonymClicked = { synonym ->
                                viewModel.loadWord(synonym)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WordSynonyms(
    synonyms: List<Word>,
    onSynonymClicked: (Word) -> Unit
) {
    Column(modifier = Modifier.padding(10.dp)) {

        Divider(
            color = ThemeColors.primary1.copy(alpha = 0.3f),
            thickness = 1.dp
        )

        synonyms.forEachIndexed { index, synonym ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .clickable { onSynonymClicked(synonym) }
            ) {
                Icon(
                    Icons.Default.ArrowCircleRight,
                    contentDescription = null,
                    tint = ThemeColors.primary1
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = synonym.title.toString(),
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
