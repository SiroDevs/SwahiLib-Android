package com.swahilib.feature.word.view

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import com.swahilib.core.database.model.WordEntity
import com.swahilib.core.ui.components.general.*
import com.swahilib.core.ui.components.listitems.SynonymItem
import com.swahilib.feature.word.WordViewModel

@Composable
fun WordView(
    modifier: Modifier = Modifier,
    viewModel: WordViewModel,
    title: String,
    conjugation: String,
    meanings: List<String>,
    synonyms: List<WordEntity>
) {
    val scrollState = rememberLazyListState()
    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        LazyColumn(state = scrollState) {
            item { CollapsingHeader(title = title) }
            item {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (meanings.isNotEmpty()) MeaningsView(meanings = meanings)

                    if (conjugation.isNotEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "MNYAMBULIKO",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    letterSpacing = 1.5.sp
                                )
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = conjugation,
                                    style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic),
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }

                    if (synonyms.isNotEmpty()) {
                        Column {
                            Text(
                                text = if (synonyms.size == 1) "KISAWE" else "VISAWE (${synonyms.size})",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.height(8.dp))
                            synonyms.forEach { synonym ->
                                SynonymItem(
                                    title = synonym.title ?: "",
                                    onClick = { viewModel.loadWord(synonym) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
