package com.swahilib.feature.how_it_works.view

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.ManageSearch
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.swahilib.core.ui.components.action.AppTopBar

data class HowItWorksSection(
    val icon: ImageVector,
    val title: String,
    val description: String
)

val sections = listOf(
    HowItWorksSection(
        icon = Icons.Default.Search,
        title = "Jinsi ya Kutafuta",
        description = "Anza kutafuta kwa kuandika neno, nahau, methali, au msemo katika sehemu ya utafutaji. " +
                "Vile vile waweza pia kutumia herufi za upande wa kulia kuchagua herufi maalum, au kutumia " +
                "kipengele cha kutafuta kwa sauti kwa kubonyeza ikoni ya maikrofoni."
    ),
    HowItWorksSection(
        icon = Icons.Default.ManageSearch,
        title = "Tafuta kwa Kina",
        description = "Bonyeza kitufe kinachoelea chenye ikoni ya utafutaji wa kina. " +
                "Hapa unaweza kuchagua namna ya utafutaji unaotaka, kutafuta kwa maneno yanayofanana, " +
                "na kupanga matokeo kwa njia tofauti."
    ),
    HowItWorksSection(
        icon = Icons.Default.Favorite,
        title = "Vipendwa Vyako",
        description = "Bonyeza kwa muda mrefu kwenye kipande chochote kwenye orodha na uchague 'Penda' kutoka kwa menyu. " +
                "Unaweza pia kupenda kipande ukiwa ukikitazama kwa kubonyeza ikoni ya moyo. " +
                "Vipande vyako vipendwa vitaonekana kwenye kichupo cha 'Vipendwa'."
    ),
    HowItWorksSection(
        icon = Icons.Default.History,
        title = "Historia Yako",
        description = "Kichupo cha 'Historia' kinaonyesha historia ya vyote ulivyotembelea, vikipangwa kulingana na wakati. " +
                "Hii inakusaidia kupata haraka vipande ulivyotembelea hivi karibuni bila kutafuta tena."
    ),
    HowItWorksSection(
        icon = Icons.Default.Book,
        title = "Kutazama Neno, Methali, Nahau au Methali",
        description = "Bonyeza kipande chochote kukifungua na kuona maelezo yake yote, visawe, na mifano. " +
                "Kwa maneno, utaona pia mnyambuliko. Kwa methali, utaona maelezo ya kina zaidi."
    ),
)

@Composable
fun HowItWorksScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Jinsi ya Kutumia",
                showGoBack = true,
                onNavIconClick = { navController.popBackStack() }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Jifunze jinsi ya kupata manufaa zaidi kutoka kwa SwahiLib",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            sections.forEach { HowItWorksCard(section = it) }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun HowItWorksCard(section: HowItWorksSection) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = section.icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                Text(
                    text = section.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (expanded) {
                Spacer(Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(Modifier.height(12.dp))
                Text(
                    text = section.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                )
            }
        }
    }
}
