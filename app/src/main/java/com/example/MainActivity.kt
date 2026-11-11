package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AppBottomNavigation
import com.example.ui.components.AppNavigationRail
import com.example.ui.components.NotificationsSheet
import com.example.ui.components.PharmacyCrossIcon
import com.example.ui.components.TopHeaderBar
import com.example.ui.navigation.ScreenDestination
import com.example.ui.screens.AddStockScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ExpiryAlertsScreen
import com.example.ui.screens.GeneralItemsScreen
import com.example.ui.screens.LowStockScreen
import com.example.ui.screens.MedicinesScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.SalesBillingScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.StockManagementScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppThemeMode
import com.example.ui.viewmodel.PharmacyViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: PharmacyViewModel = viewModel()
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
            val systemDark = isSystemInDarkTheme()

            val isDarkTheme = when (themeMode) {
                AppThemeMode.LIGHT -> false
                AppThemeMode.DARK -> true
                AppThemeMode.SYSTEM -> false
            }

            MyApplicationTheme(darkTheme = isDarkTheme) {
                PharmacyApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun PharmacyApp(viewModel: PharmacyViewModel) {
    val configuration = LocalConfiguration.current
    val isWideScreen = configuration.screenWidthDp >= 720

    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val unreadNotifications by viewModel.unreadNotificationsCount.collectAsStateWithLifecycle()
    val notifications by viewModel.allNotifications.collectAsStateWithLifecycle()
    val activeRole by viewModel.activeUserRole.collectAsStateWithLifecycle()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()
    val globalSearchQuery by viewModel.globalSearchQuery.collectAsStateWithLifecycle()

    val medicines by viewModel.allMedicines.collectAsStateWithLifecycle()
    val generalItems by viewModel.allGeneralItems.collectAsStateWithLifecycle()
    val showNotificationsSheet by viewModel.showNotificationsSheet.collectAsStateWithLifecycle()

    val lowStockCount = medicines.count { it.isLowStock } + generalItems.count { it.isLowStock }
    val expiryAlertCount = medicines.count { it.daysRemaining <= 30 }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Back handler
    BackHandler(enabled = drawerState.isOpen || currentScreen != ScreenDestination.DASHBOARD) {
        if (drawerState.isOpen) {
            scope.launch { drawerState.close() }
        } else {
            val handled = viewModel.navigateBack()
            if (!handled && currentScreen != ScreenDestination.DASHBOARD) {
                viewModel.navigateTo(ScreenDestination.DASHBOARD)
            }
        }
    }

    // Show toast / snackbar on message
    LaunchedEffect(userMessage) {
        userMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    // Sync global search with medicines/general screen if user types in header
    LaunchedEffect(globalSearchQuery) {
        if (globalSearchQuery.isNotBlank() && currentScreen != ScreenDestination.MEDICINES && currentScreen != ScreenDestination.GENERAL_ITEMS && currentScreen != ScreenDestination.SALES_BILLING) {
            viewModel.medicineSearchQuery.value = globalSearchQuery
        }
    }

    val contentBody = @Composable {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing),
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopHeaderBar(
                    currentScreen = currentScreen,
                    unreadNotifications = unreadNotifications,
                    activeRole = activeRole,
                    themeMode = themeMode,
                    isWideScreen = isWideScreen,
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onNotificationsClick = { viewModel.showNotificationsSheet.value = true },
                    onRoleChange = { viewModel.setActiveUserRole(it) },
                    onToggleTheme = {
                        viewModel.setThemeMode(
                            if (themeMode == AppThemeMode.DARK) AppThemeMode.LIGHT else AppThemeMode.DARK
                        )
                    },
                    globalSearchQuery = globalSearchQuery,
                    onGlobalSearchChange = {
                        viewModel.setGlobalSearchQuery(it)
                        viewModel.medicineSearchQuery.value = it
                    }
                )
            },
            bottomBar = {
                if (!isWideScreen) {
                    AppBottomNavigation(
                        currentScreen = currentScreen,
                        onDestinationSelected = { dest ->
                            viewModel.navigateTo(dest)
                        },
                        onOpenMoreMenu = {
                            scope.launch { drawerState.open() }
                        },
                        expiryAlertCount = expiryAlertCount
                    )
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                AnimatedContent(
                    targetState = currentScreen,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "screen_transition"
                ) { target ->
                    when (target) {
                        ScreenDestination.DASHBOARD -> DashboardScreen(
                            viewModel = viewModel,
                            onNavigate = { viewModel.navigateTo(it) }
                        )
                        ScreenDestination.MEDICINES -> MedicinesScreen(viewModel = viewModel)
                        ScreenDestination.GENERAL_ITEMS -> GeneralItemsScreen(viewModel = viewModel)
                        ScreenDestination.STOCK_MANAGEMENT -> StockManagementScreen(
                            viewModel = viewModel,
                            onNavigate = { viewModel.navigateTo(it) }
                        )
                        ScreenDestination.ADD_STOCK -> AddStockScreen(
                            viewModel = viewModel,
                            onNavigate = { viewModel.navigateTo(it) }
                        )
                        ScreenDestination.EXPIRY_ALERTS -> ExpiryAlertsScreen(viewModel = viewModel)
                        ScreenDestination.LOW_STOCK -> LowStockScreen(
                            viewModel = viewModel,
                            onNavigate = { viewModel.navigateTo(it) }
                        )
                        ScreenDestination.SALES_BILLING -> SalesBillingScreen(viewModel = viewModel)
                        ScreenDestination.REPORTS -> ReportsScreen(viewModel = viewModel)
                        ScreenDestination.SETTINGS -> SettingsScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }

    if (isWideScreen) {
        // Desktop / Tablet Layout: Sidebar Navigation Rail + Main Content
        Row(modifier = Modifier.fillMaxSize()) {
            AppNavigationRail(
                currentScreen = currentScreen,
                onDestinationSelected = { viewModel.navigateTo(it) },
                lowStockCount = lowStockCount,
                expiryAlertCount = expiryAlertCount
            )
            Divider(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(1.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            )
            Box(modifier = Modifier.weight(1f)) {
                contentBody()
            }
        }
    } else {
        // Phone Layout: Modal Navigation Drawer for "More" sections + Bottom Navigation
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(modifier = Modifier.width(300.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color.White,
                                shadowElevation = 2.dp,
                                modifier = Modifier.size(46.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.zamzam_logo),
                                    contentDescription = "Zamzam Logo",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                )
                            }
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = "ZamZam MEDICAL",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    PharmacyCrossIcon(size = 18.dp)
                                }
                                Text(
                                    text = "Pharmacy • Marg ERP 9",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(12.dp))

                        ScreenDestination.values().forEach { dest ->
                            NavigationDrawerItem(
                                icon = { Icon(dest.icon, contentDescription = dest.title) },
                                label = { Text(dest.title) },
                                selected = currentScreen == dest,
                                onClick = {
                                    viewModel.navigateTo(dest)
                                    scope.launch { drawerState.close() }
                                },
                                colors = NavigationDrawerItemDefaults.colors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        ) {
            contentBody()
        }
    }

    // Notifications Bottom Sheet
    if (showNotificationsSheet) {
        NotificationsSheet(
            notifications = notifications,
            onDismiss = { viewModel.showNotificationsSheet.value = false },
            onMarkAsRead = { viewModel.markNotificationAsRead(it) },
            onMarkAllAsRead = { viewModel.markAllNotificationsAsRead() },
            onClearAll = { viewModel.clearAllNotifications() }
        )
    }
}
