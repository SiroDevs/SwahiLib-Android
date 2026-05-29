package com.swahilib.feature.proverb.view

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import com.swahilib.core.database.model.ProverbEntity
import com.swahilib.core.ui.components.general.*
import com.swahilib.core.ui.components.listitems.SynonymItem
import com.swahilib.feature.proverb.ProverbViewModel

@Composable
fun ProverbDetails(
    modifier: Modifier = Modifier,
    viewModel: ProverbViewModel,
    title: String,
    meanings: List<String>,
    synonyms: List<ProverbEntity>,
    explanations: List<String>,
) {
    val scrollState = rememberLazyListState()

    val hasLiteralAndFigurativeMeanings =
        meanings.indices.contains(1) && meanings[1].isNotEmpty()

    val literalMeanings = meanings[0]
        .takeIf { it.isNotEmpty() }
        ?.split(";")
        ?.map { it.trim() }
        ?.filter { it.isNotEmpty() }
        ?: emptyList()
    val figurativeMeanings = meanings[1]
        .takeIf { it.isNotEmpty() }
        ?.split(";")
        ?.map { it.trim() }
        ?.filter { it.isNotEmpty() }
        ?: emptyList()
    val hasFirstExplanation = explanations.indices.contains(0) && explanations.isNotEmpty()
    val hasSecondExplanation = explanations.indices.contains(1) && explanations.isNotEmpty()
    val synonymsTitle =
        if (synonyms.size == 1) "Kisawe" else "Visawe ${synonyms.size}"

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(state = scrollState) {
            item { CollapsingHeader(title = title) }
            item {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (hasFirstExplanation) FirstExplanationView(explanation = explanations[0])

                    Spacer(Modifier.height(80.dp))

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
                                    onClick = { viewModel.loadProverb(synonym) }
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(80.dp))

                    if (meanings.isNotEmpty()) {
                        if (hasLiteralAndFigurativeMeanings) {
                            Text(
                                text = "MAANA HALISI ${literalMeanings.size}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                            )
                            MeaningsView(meanings = literalMeanings)
                            Text(
                                text = "MAANA YA KIFALSAFA/KIMAFUMBO ${figurativeMeanings.size}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                            )
                            MeaningsView(meanings = figurativeMeanings)
                        } else {
                            Text(
                                text = "MAANA YA METHALI ${meanings.size}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                            )
                            MeaningsView(meanings = meanings)
                        }
                    }

                    Spacer(Modifier.height(80.dp))

                    if (hasSecondExplanation) SecondExplanationView(explanation = explanations[1])

                }
            }
        }
    }
}
