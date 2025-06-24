package com.swahilib.presentation.components.action

//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.TextField
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.TextFieldDefaults
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.unit.sp
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
                containerColor = ThemeColors.primary1,
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
    TopAppBar(
        title = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                TextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                        .clip(RoundedCornerShape(30.dp))
                        .border(1.dp, Color.White, RoundedCornerShape(30.dp))
                        .background(Color.Transparent),
                    textStyle = LocalTextStyle.current.copy(
                        color = Color.White,
                        fontSize = 18.sp,
                    ),
                    placeholder = {
                        Text(
                            text = "Tafuta kwenye kamusi",
                            color = Color.White,
                            fontSize = 18.sp
                        )
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                        textColor = Color.White,
                        cursorColor = Color.White,
                        placeholderColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    trailingIcon = {
                        IconButton(onClick = onClose) {
                            Icon(
                                Icons.Filled.Close,
                                contentDescription = "Close",
                                tint = Color.White
                            )
                        }
                    }
                )
            }
        },
        navigationIcon = {},
        actions = {},
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = ThemeColors.primary1,
            titleContentColor = Color.White
        )
    )
}
