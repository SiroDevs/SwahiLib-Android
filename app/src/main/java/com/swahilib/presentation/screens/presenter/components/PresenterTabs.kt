package com.swahilib.presentation.screens.presenter.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.pager.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.swahilib.data.sample.*

@Composable
fun PresenterTabs(
    pagerState: PagerState,
    verses: List<String>,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
            modifier = modifier
                .fillMaxWidth()
                .padding(15.dp),
            elevation = CardDefaults.cardElevation(5.dp),
        ) {
        VerticalPager(
            state = pagerState,
            modifier = modifier.fillMaxSize()
        ) { page ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = verses[page],
                    style = TextStyle(
                        fontSize = 30.sp,
                        //color = Color.White,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PresenterTabsPreview() {
    val pagerState = rememberPagerState { SampleVerses.size }
    LaunchedEffect(Unit) {
        pagerState.scrollToPage(0)
    }
    PresenterTabs(
        pagerState = pagerState,
        verses = SampleVerses,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    )
}
