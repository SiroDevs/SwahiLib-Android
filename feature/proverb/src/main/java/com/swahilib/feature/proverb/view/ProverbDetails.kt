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

    val literalMeanings = meanings.getOrNull(0)
        ?.takeIf { it.isNotEmpty() }
        ?.split(";")
        ?.map { it.trim() }
        ?.filter { it.isNotEmpty() }
        ?: emptyList()

    val figurativeMeanings = meanings.getOrNull(1)
        ?.takeIf { it.isNotEmpty() }
        ?.split(";")
        ?.map { it.trim() }
        ?.filter { it.isNotEmpty() }
        ?: emptyList()

    val hasLiteralAndFigurativeMeanings = figurativeMeanings.isNotEmpty()

    val hasFirstExplanation  = explanations.getOrNull(0)?.isNotEmpty() == true
    val hasSecondExplanation = explanations.getOrNull(1)?.isNotEmpty() == true

    val synonymsTitle = if (synonyms.size == 1) "KISAWE" else "VISAWE (${synonyms.size})"

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

                    if (synonyms.isNotEmpty()) {
                        Column {
                            Text(
                                text = synonymsTitle,
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

                    if (meanings.isNotEmpty()) {
                        if (hasLiteralAndFigurativeMeanings) {
                            Text(
                                text = "MAANA HALISI",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                            )
                            MeaningsView(meanings = literalMeanings)
                            Text(
                                text = "MAANA YA KIFALSAFA/KIMAFUMBO",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                            )
                            MeaningsView(meanings = figurativeMeanings)
                        } else {
                            Text(
                                text = "MAANA YA METHALI",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                            )
                            MeaningsView(meanings = literalMeanings)
                        }
                    }

                    if (hasSecondExplanation) SecondExplanationView(explanation = explanations[1])

                    Spacer(Modifier.height(10.dp))
                }
            }
        }
    }
}
