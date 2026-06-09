package com.swahilib.core.ui.components.action

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextOverflow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    tagline: String? = null,
    centered: Boolean = false,
    showGoBack: Boolean = false,
    showNavDrawer: Boolean = false,
    onNavIconClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    require(!(showGoBack && showNavDrawer)) {
        "showGoBack and showNavDrawer cannot both be true"
    }

    val titleContent: @Composable () -> Unit = {
        if (tagline != null) {
            Column(horizontalAlignment = if (centered) Alignment.CenterHorizontally else Alignment.Start) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = tagline,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        } else {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }

    val navIcon: @Composable () -> Unit = {
        when {
            showGoBack -> IconButton(onClick = { onNavIconClick?.invoke() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Rudi Nyuma",
                )
            }
            showNavDrawer -> IconButton(onClick = { onNavIconClick?.invoke() }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                )
            }
        }
    }

    val colors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
        actionIconContentColor = MaterialTheme.colorScheme.onSurface,
    )

    if (centered) {
        CenterAlignedTopAppBar(
            title = titleContent,
            navigationIcon = navIcon,
            actions = actions,
            windowInsets = WindowInsets(0, 0, 0, 0),
            colors = colors,
        )
    } else {
        TopAppBar(
            title = titleContent,
            navigationIcon = navIcon,
            actions = actions,
            windowInsets = WindowInsets(0, 0, 0, 0),
            colors = colors,
        )
    }
}
