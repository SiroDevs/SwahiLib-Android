package com.swahilib.presentation.components.action

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.swahilib.presentation.theme.ThemeColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    actions: @Composable RowScope.() -> Unit = {},
    navigationIcon: @Composable () -> Unit = {},
) {
    Surface(
        shadowElevation = 4.dp
    ) {
        TopAppBar(
            title = {
                Text(
                    text = title,
                    color = Color.White
                )
            },
            actions = actions,
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = ThemeColors.primary,
                titleContentColor = Color.White,
                actionIconContentColor = Color.White,
                navigationIconContentColor = Color.White
            ),
            navigationIcon = navigationIcon
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClose: () -> Unit
) {
    Surface(shadowElevation = 3.dp) {
        TopAppBar(
            title = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp, vertical = 5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    TextField(
                        value = query,
                        onValueChange = onQueryChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(25.dp))
                            .background(MaterialTheme.colorScheme.surface),
                        placeholder = { Text("Search for songs") },
                        singleLine = true,
                        trailingIcon = {
                            IconButton(onClick = onClose) {
                                Icon(Icons.Filled.Close, contentDescription = "Close")
                            }
                        },
//                        colors = TextFieldDefaults.TextFieldColors(
//                            containerColor = Color.White,
//                            unfocusedIndicatorColor = Color.Transparent,
//                            focusedIndicatorColor = Color.Transparent,
//                            disabledIndicatorColor = Color.Transparent
//                        ),
                        shape = RoundedCornerShape(24.dp)
                    )
                }
            },
            navigationIcon = {},
            actions = {},
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = ThemeColors.primary,
                titleContentColor = Color.White
            )
        )
    }
}
