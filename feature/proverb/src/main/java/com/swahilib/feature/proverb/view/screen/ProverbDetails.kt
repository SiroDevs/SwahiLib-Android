package com.swahilib.feature.proverb.view.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.swahilib.core.database.model.ProverbEntity
import com.swahilib.core.ui.components.donation.DonationBanner
import com.swahilib.core.ui.components.general.CollapsingHeader
import com.swahilib.core.ui.components.general.MeaningsView
import com.swahilib.core.ui.components.listitems.SynonymItem
import com.swahilib.feature.proverb.viewmodel.ProverbViewModel
import com.swahilib.feature.proverb.view.components.FirstExplanationView
import com.swahilib.feature.proverb.view.components.SecondExplanationView
import kotlin.collections.forEach

@Composable
fun ProverbDetails(
    modifier: Modifier = Modifier,
    viewModel: ProverbViewModel,
    title: String,
    meanings: List<String>,
    synonyms: List<ProverbEntity>,
    explanations: List<String>,
    showDonation: Boolean = false,
    onShowDonation: () -> Unit = {},
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
            item {
                DonationBanner(
                    show = showDonation,
                    onTap = onShowDonation,
                    modifier = Modifier.padding(bottom = 24.dp),
                )
            }
        }

    }
}
