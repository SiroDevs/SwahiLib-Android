package com.swahilib.feature.word.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.swahilib.core.database.model.WordEntity
import com.swahilib.core.ui.components.donation.DonationBanner
import com.swahilib.core.ui.components.general.CollapsingHeader
import com.swahilib.core.ui.components.general.MeaningsView
import com.swahilib.core.ui.components.listitems.SynonymItem
import com.swahilib.feature.word.WordViewModel

@Composable
fun WordView(
    modifier: Modifier = Modifier,
    viewModel: WordViewModel,
    title: String,
    conjugation: String,
    meanings: List<String>,
    synonyms: List<WordEntity>,
    english: String? = null,
    showDonation: Boolean = false,
    onShowDonation: () -> Unit = {},
) {
    val scrollState = rememberLazyListState()
    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        LazyColumn(state = scrollState) {
            item { CollapsingHeader(title = title) }
            item {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (meanings.isNotEmpty()) MeaningsView(meanings = meanings)

                    if (!english.isNullOrBlank()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer
                            )
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "ENGLISH",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                                    letterSpacing = 1.5.sp
                                )
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = english,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            }
                        }
                    }

                    if (conjugation.isNotEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
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
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontStyle = FontStyle.Italic
                                    ),
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }

                    if (synonyms.isNotEmpty()) {
                        Column {
                            Text(
                                text = if (synonyms.size == 1) "KISAWE" else "VISAWE (${synonyms.size})",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
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
