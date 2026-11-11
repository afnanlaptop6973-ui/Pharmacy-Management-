package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.navigation.ScreenDestination
import com.example.ui.theme.MedicalCrossGreen
import com.example.ui.theme.MedicalGradientPharmacyGreen
import com.example.ui.theme.PharmacyPrimary
import com.example.ui.viewmodel.AppThemeMode

@Composable
fun TopHeaderBar(
    currentScreen: ScreenDestination,
    unreadNotifications: Int,
    activeRole: String,
    themeMode: AppThemeMode,
    isWideScreen: Boolean,
    onMenuClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onRoleChange: (String) -> Unit,
    onToggleTheme: () -> Unit,
    globalSearchQuery: String,
    onGlobalSearchChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showRoleMenu by remember { mutableStateOf(false) }
    var showSearchBoxInMobile by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Subtle colorful medical shop accent line at the very top
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(MedicalGradientPharmacyGreen)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Drawer menu button (mobile) or Screen title with Medical Shop Emblem
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (!isWideScreen) {
                        IconButton(
                            onClick = onMenuClick,
                            modifier = Modifier.testTag("nav_drawer_toggle")
                        ) {
                            Icon(imageVector = Icons.Default.Menu, contentDescription = "Open navigation menu")
                        }
                        Surface(
                            shape = CircleShape,
                            color = Color.White,
                            shadowElevation = 1.dp,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.zamzam_logo),
                                contentDescription = "Zamzam Logo",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                            )
                        }
                    }

                    // Green Pharmacy Cross & Title
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Medical Cross pill badge
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MedicalCrossGreen,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.LocalHospital,
                                    contentDescription = "Medical Shop",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = if (isWideScreen) currentScreen.title else "ZamZam MEDICAL",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                // Rx pill tag
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    modifier = Modifier.padding(start = 2.dp)
                                ) {
                                    Text(
                                        text = "Rx",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(MedicalCrossGreen)
                                )
                                Text(
                                    text = if (isWideScreen) "Pharmacy Dispensary • Marg ERP 9" else currentScreen.title,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                // Center: Wide screen search input
                if (isWideScreen) {
                    PharmacySearchBar(
                        query = globalSearchQuery,
                        onQueryChange = onGlobalSearchChange,
                        placeholderText = "Search Medicines, Generics, Salts, Batch, OTC...",
                        modifier = Modifier
                            .width(380.dp)
                            .height(48.dp),
                        testTag = "global_search_input"
                    )
                }

                // Right: Actions (Search on mobile, Notifications, Role, Dark/Light mode)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (!isWideScreen) {
                        IconButton(
                            onClick = { showSearchBoxInMobile = !showSearchBoxInMobile },
                            modifier = Modifier.testTag("mobile_search_toggle")
                        ) {
                            Icon(imageVector = Icons.Default.Search, contentDescription = "Toggle search")
                        }
                    }

                    // Theme Mode Toggle
                    IconButton(
                        onClick = onToggleTheme,
                        modifier = Modifier.testTag("theme_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (themeMode == AppThemeMode.DARK) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Dark/Light Mode",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Notifications Badge
                    IconButton(
                        onClick = onNotificationsClick,
                        modifier = Modifier.testTag("notifications_icon_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (unreadNotifications > 0) {
                                    Badge(containerColor = MaterialTheme.colorScheme.error) {
                                        Text("$unreadNotifications")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // User Profile / Role Dropdown with role color dot
                    val roleColor = when (activeRole) {
                        "Pharmacist" -> MedicalCrossGreen
                        "Admin" -> MaterialTheme.colorScheme.secondary
                        else -> Color(0xFFD97706) // Cashier Amber
                    }

                    Box {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier
                                .testTag("role_switcher_pill")
                                .clickable { showRoleMenu = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(roleColor),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Text(
                                    text = activeRole,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Select role",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = showRoleMenu,
                            onDismissRequest = { showRoleMenu = false }
                        ) {
                            listOf("Pharmacist", "Admin", "Cashier").forEach { role ->
                                val dotColor = when (role) {
                                    "Pharmacist" -> MedicalCrossGreen
                                    "Admin" -> MaterialTheme.colorScheme.secondary
                                    else -> Color(0xFFD97706)
                                }
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(10.dp)
                                                    .clip(CircleShape)
                                                    .background(dotColor)
                                            )
                                            Column {
                                                Text(
                                                    text = role,
                                                    fontWeight = if (role == activeRole) FontWeight.Bold else FontWeight.Normal
                                                )
                                                Text(
                                                    text = when (role) {
                                                        "Pharmacist" -> "Lead Dispenser & Inventory"
                                                        "Admin" -> "Full Access (All Modules)"
                                                        else -> "Billing & Sales Counter"
                                                    },
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    },
                                    onClick = {
                                        onRoleChange(role)
                                        showRoleMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Mobile expandable search bar
            AnimatedVisibility(visible = !isWideScreen && showSearchBoxInMobile) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    PharmacySearchBar(
                        query = globalSearchQuery,
                        onQueryChange = onGlobalSearchChange,
                        placeholderText = "Search Medicines, Salts, Brands...",
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "mobile_global_search_input"
                    )
                }
            }
        }
    }
}
