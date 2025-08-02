package com.swahilib.presentation.screens.viewer.proverb

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import com.swahilib.data.models.Proverb
import com.swahilib.presentation.components.general.CollapsingHeader
import com.swahilib.presentation.components.general.MeaningsView
import com.swahilib.presentation.components.listitems.SynonymItem
import com.swahilib.presentation.theme.ThemeColors
import com.swahilib.presentation.viewmodels.ProverbViewModel

@Composable
fun ProverbView(
    modifier: Modifier = Modifier,
    viewModel: ProverbViewModel,
    title: String,
    conjugation: String,
    meanings: List<String>,
    synonyms: List<Proverb>
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
                        ProverbSynonyms(
                            synonyms = synonyms,
                            onSynonymClicked = { synonym ->
                                viewModel.loadProverb(synonym)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProverbSynonyms(
    synonyms: List<Proverb>,
    onSynonymClicked: (Proverb) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 10.dp)) {
        synonyms.forEach { synonym ->
            SynonymItem(
                title = synonym.title ?: "",
                onClick = { onSynonymClicked(synonym) }
            )
        }
    }
}
