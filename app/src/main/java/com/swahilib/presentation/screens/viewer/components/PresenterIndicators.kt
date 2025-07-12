package com.swahilib.presentation.screens.viewer.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.pager.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Color
import com.swahilib.presentation.theme.ThemeColors
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun PresenterIndicators(
    pagerState: PagerState,
    indicators: List<String>,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 65.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(indicators.size) { index ->
            val label = indicators[index]
            val isSelected = pagerState.currentPage == index

            Button(
                onClick = {
                    scope.launch { pagerState.animateScrollToPage(index) }
                },
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) ThemeColors.primary3 else ThemeColors.primary1,
                    contentColor = Color.White,
                ),
                elevation =  ButtonDefaults.buttonElevation(5.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 5.dp),
                modifier = Modifier
                    .height(40.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = label,
                    style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

/*@Preview(showBackground = true)
@Composable
fun PresenterIndicatorsPreview() {
    val pagerState = rememberPagerState { SampleVerses.size }
    LaunchedEffect(Unit) {
        pagerState.scrollToPage(0)
    }

    PresenterIndicators(
        pagerState = pagerState,
        indicators = SampleIndicators,
        modifier = Modifier.padding(16.dp)
    )
}*/
