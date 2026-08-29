package com.swahilib.feature.home.view.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.swahilib.core.common.utils.AppConstants
import com.swahilib.core.common.utils.Routes
import com.swahilib.core.ui.R
import kotlinx.coroutines.launch

@Composable
fun HomeNavDrawer(
    drawerState: DrawerState,
    onNavigate: (String) -> Unit,
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()

    fun navigateAndClose(route: String) {
        scope.launch { drawerState.close() }
        onNavigate(route)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.width(300.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 20.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.app_icon),
                        contentDescription = "AppIcon",
                        modifier = Modifier.size(50.dp),
                    )
                    Column() {
                        Text(
                            text = AppConstants.APP_TITLE,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        Text(
                            text = "${AppConstants.APP_TAGLINE} · ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                HorizontalDivider()
                Spacer(Modifier.height(8.dp))

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.AutoStories, contentDescription = null) },
                    label = { Text("Neno la Siku") },
                    selected = false,
                    onClick = { navigateAndClose(Routes.DAILY_WORD) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.FormatQuote, contentDescription = null) },
                    label = { Text("Methali ya Siku") },
                    selected = false,
                    onClick = { navigateAndClose(Routes.DAILY_PROVERB) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                Spacer(Modifier.weight(1f))
                HorizontalDivider()
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Groups, contentDescription = null) },
                    label = { Text("Jamii ya SwahiLib") },
                    selected = false,
                    onClick = { navigateAndClose(Routes.AUTH_SIGN_IN) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.TrendingUp, contentDescription = null) },
                    label = { Text("Maendeleo Yangu") },
                    selected = false,
                    onClick = { navigateAndClose(Routes.PROGRESS) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                Spacer(Modifier.weight(1f))
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Info, contentDescription = null) },
                    label = { Text("Jinsi ya Kutumia") },
                    selected = false,
                    onClick = { navigateAndClose(Routes.HOW_IT_WORKS) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.HelpOutline, contentDescription = null) },
                    label = { Text("Usaidizi na Maoni") },
                    selected = false,
                    onClick = { navigateAndClose(Routes.HELP) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.VolunteerActivism, contentDescription = null) },
                    label = { Text("Donate to SwahiLib") },
                    selected = false,
                    onClick = { navigateAndClose(Routes.DONATION) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text("Mipangilio ya SwahiLib") },
                    selected = false,
                    onClick = { navigateAndClose(Routes.SETTINGS) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                NavigationDrawerItem(
                    label = { Text(
                        text = AppConstants.APP_CREDITS,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    ) },
                    selected = false,
                    onClick = {  },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        },
        content = content
    )
}