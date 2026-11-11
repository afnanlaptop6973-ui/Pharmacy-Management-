package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.ui.graphics.vector.ImageVector

enum class ScreenDestination(
    val title: String,
    val icon: ImageVector,
    val isPrimaryInMobile: Boolean = false
) {
    DASHBOARD("Dashboard", Icons.Default.Dashboard, isPrimaryInMobile = true),
    MEDICINES("Medicines", Icons.Default.Medication, isPrimaryInMobile = true),
    GENERAL_ITEMS("General Items", Icons.Default.Category, isPrimaryInMobile = false),
    STOCK_MANAGEMENT("Stock Management", Icons.Default.Inventory, isPrimaryInMobile = false),
    ADD_STOCK("Add New Stock", Icons.Default.AddBox, isPrimaryInMobile = false),
    EXPIRY_ALERTS("Expiry & Alerts", Icons.Default.Warning, isPrimaryInMobile = true),
    LOW_STOCK("Low Stock", Icons.Default.WarningAmber, isPrimaryInMobile = false),
    SALES_BILLING("Sales & Billing", Icons.Default.PointOfSale, isPrimaryInMobile = true),
    REPORTS("Reports", Icons.Default.Assessment, isPrimaryInMobile = false),
    SETTINGS("Settings", Icons.Default.Settings, isPrimaryInMobile = false);
}
