package com.swahilib.presentation.viewer.word.view

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import com.swahilib.data.models.Word
import com.swahilib.presentation.components.general.*
import com.swahilib.presentation.components.listitems.SynonymItem
import com.swahilib.presentation.viewer.word.WordViewModel

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
            .background(MaterialTheme.colorScheme.onPrimary)
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
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(start = 10.dp)
                        )
                    }

                    if (synonyms.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = if (synonyms.size == 1) "KISAWE" else "VISAWE ${synonyms.size}",
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
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
    Column(modifier = Modifier.padding(horizontal = 10.dp)) {
        synonyms.forEach { synonym ->
            SynonymItem(
                title = synonym.title ?: "",
                onClick = { onSynonymClicked(synonym) }
            )
        }
    }
}
