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
    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        LazyColumn(state = scrollState) {
            item { CollapsingHeader(title = title, subtitle = "Methali") }
            item {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (meanings.isNotEmpty()) MeaningsView(meanings = meanings)

                    if (explanations.isNotEmpty() && explanations.any { it.isNotBlank() }) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "MAELEZO YA KINA",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                                    letterSpacing = 1.5.sp
                                )
                                Spacer(Modifier.height(8.dp))
                                explanations.filter { it.isNotBlank() }.forEachIndexed { i, ex ->
                                    if (i > 0) Spacer(Modifier.height(6.dp))
                                    Text(
                                        text = ex,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer
                                    )
                                }
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
                                    onClick = { viewModel.loadProverb(synonym) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
