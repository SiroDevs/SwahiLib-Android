/*
 * Copyright 2026 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.swahilib.core.ui.components.game

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun GameActionFab(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isContinue: Boolean = false,
) {
    ExtendedFloatingActionButton(
        onClick = { if (enabled) onClick() },
        modifier = modifier.padding(bottom = 4.dp),
        containerColor = if (enabled) {
            if (isContinue) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },
        contentColor = if (enabled) {
            if (isContinue) MaterialTheme.colorScheme.onTertiary else MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        icon = {
            Icon(
                imageVector = if (isContinue) Icons.Default.ArrowForward else Icons.Default.Check,
                contentDescription = null,
            )
        },
        text = { Text(text) },
        elevation = FloatingActionButtonDefaults.elevation(if (enabled) 4.dp else 0.dp),
    )
}
