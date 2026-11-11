package com.example.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.navigation.ScreenDestination

@Composable
fun AppBottomNavigation(
    currentScreen: ScreenDestination,
    onDestinationSelected: (ScreenDestination) -> Unit,
    onOpenMoreMenu: () -> Unit,
    expiryAlertCount: Int,
    modifier: Modifier = Modifier
) {
    val primaryScreens = listOf(
        ScreenDestination.DASHBOARD,
        ScreenDestination.MEDICINES,
        ScreenDestination.SALES_BILLING,
        ScreenDestination.EXPIRY_ALERTS
    )

    val isMoreActive = currentScreen !in primaryScreens

    NavigationBar(
        modifier = modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("app_bottom_nav"),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp
    ) {
        primaryScreens.forEach { dest ->
            val isSelected = currentScreen == dest
            NavigationBarItem(
                selected = isSelected,
                onClick = { onDestinationSelected(dest) },
                icon = {
                    if (dest == ScreenDestination.EXPIRY_ALERTS && expiryAlertCount > 0) {
                        BadgedBox(badge = {
                            Badge(containerColor = MaterialTheme.colorScheme.error) {
                                Text("$expiryAlertCount")
                            }
                        }) {
                            Icon(dest.icon, contentDescription = dest.title)
                        }
                    } else {
                        Icon(dest.icon, contentDescription = dest.title)
                    }
                },
                label = {
                    Text(
                        text = if (dest == ScreenDestination.SALES_BILLING) "Billing" else dest.title,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.onSurface,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.testTag("bottom_nav_${dest.name.lowercase()}")
            )
        }

        // "More" item
        NavigationBarItem(
            selected = isMoreActive,
            onClick = onOpenMoreMenu,
            icon = {
                Icon(Icons.Default.MoreHoriz, contentDescription = "More Sections")
            },
            label = {
                Text(
                    text = "More",
                    fontWeight = if (isMoreActive) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                selectedTextColor = MaterialTheme.colorScheme.onSurface,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.testTag("bottom_nav_more")
        )
    }
}
