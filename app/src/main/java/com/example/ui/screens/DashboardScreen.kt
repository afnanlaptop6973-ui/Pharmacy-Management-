package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ExpiryStatus
import com.example.data.model.GeneralItem
import com.example.data.model.Medicine
import com.example.ui.components.EmptyStateView
import com.example.ui.components.ExpiryBadge
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatCard
import com.example.ui.components.StockBadge
import com.example.ui.navigation.ScreenDestination
import com.example.ui.theme.PharmacyPrimary
import com.example.ui.theme.PharmacyTertiary
import com.example.ui.theme.StatusCritical
import com.example.ui.theme.StatusHealthy
import com.example.ui.theme.StatusInfo
import com.example.ui.theme.StatusOrange
import com.example.ui.theme.StatusPurple
import com.example.ui.theme.StatusTeal
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.MedicalCrossGreen
import com.example.ui.theme.MedicalGradientPharmacyGreen
import androidx.compose.material.icons.filled.LocalHospital
import com.example.ui.viewmodel.PharmacyViewModel
import com.example.util.DateUtils

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DashboardScreen(
    viewModel: PharmacyViewModel,
    onNavigate: (ScreenDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    val medicines by viewModel.allMedicines.collectAsStateWithLifecycle()
    val generalItems by viewModel.allGeneralItems.collectAsStateWithLifecycle()
    val sales by viewModel.allSales.collectAsStateWithLifecycle()
    val transactions by viewModel.allStockTransactions.collectAsStateWithLifecycle()

    // Calculated stats
    val totalMedicinesCount = medicines.size
    val totalGeneralItemsCount = generalItems.size
    val totalProductsCount = totalMedicinesCount + totalGeneralItemsCount

    val totalMedicineValue = medicines.sumOf { it.totalStockValue }
    val totalGeneralValue = generalItems.sumOf { it.totalStockValue }
    val totalStockValue = totalMedicineValue + totalGeneralValue

    val lowStockMedicines = medicines.filter { it.isLowStock }
    val lowStockGeneral = generalItems.filter { it.isLowStock }
    val totalLowStockCount = lowStockMedicines.size + lowStockGeneral.size

    val expiredMedicines = medicines.filter { it.isExpired }
    val expiringSoonMedicines = medicines.filter { !it.isExpired && it.daysRemaining <= 30 }

    // Today's Sales
    val todaySales = sales.filter { DateUtils.formatDate(it.createdAt) == DateUtils.formatDate(System.currentTimeMillis()) }
    val todaySalesTotal = todaySales.sumOf { it.grandTotal }
    val monthlySalesTotal = sales.sumOf { it.grandTotal }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("dashboard_screen")
    ) {
        // Medical Shop Live Counter Hero Banner
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MedicalGradientPharmacyGreen)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White.copy(alpha = 0.22f),
                            modifier = Modifier.size(46.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.LocalHospital,
                                    contentDescription = "Medical Shop",
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "ZamZam MEDICAL",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color.White.copy(alpha = 0.28f)
                                ) {
                                    Text(
                                        text = "DISPENSARY ACTIVE",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Wholesale & Retail Chemists • Schedule H & OTC Dispensary",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.92f)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Quick Action Bar (Colorful Medical Shop Lanes)
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(MedicalCrossGreen)
                    )
                    Text(
                        text = "Quick Actions (ERP Fast Lane)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onNavigate(ScreenDestination.SALES_BILLING) },
                        colors = ButtonDefaults.buttonColors(containerColor = MedicalCrossGreen),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("quick_create_bill")
                    ) {
                        Icon(Icons.Default.PointOfSale, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Create Bill (POS)", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            viewModel.editingMedicine.value = null
                            viewModel.showAddEditMedicineDialog.value = true
                            onNavigate(ScreenDestination.MEDICINES)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("quick_add_medicine")
                    ) {
                        Icon(Icons.Default.Medication, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add Medicine", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            viewModel.editingGeneralItem.value = null
                            viewModel.showAddEditGeneralDialog.value = true
                            onNavigate(ScreenDestination.GENERAL_ITEMS)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("quick_add_general")
                    ) {
                        Icon(Icons.Default.Category, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add General Item", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { onNavigate(ScreenDestination.ADD_STOCK) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("quick_add_stock")
                    ) {
                        Icon(Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Inward New Stock", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Top Summary Cards Grid (Vibrant Medical Palette)
        SectionHeader(title = "Pharmacy Executive Overview")
        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Total Medicines",
                value = "$totalMedicinesCount",
                subtitle = "Active batches tracked",
                icon = Icons.Default.Medication,
                iconTint = Color(0xFF0284C7), // Clinical Cyan
                modifier = Modifier.width(180.dp),
                onClick = { onNavigate(ScreenDestination.MEDICINES) }
            )

            StatCard(
                title = "General Items",
                value = "$totalGeneralItemsCount",
                subtitle = "Personal & Baby care",
                icon = Icons.Default.Category,
                iconTint = Color(0xFF7C3AED), // Pediatric & Care Purple
                modifier = Modifier.width(180.dp),
                onClick = { onNavigate(ScreenDestination.GENERAL_ITEMS) }
            )

            StatCard(
                title = "Total Products",
                value = "$totalProductsCount",
                subtitle = "Combined inventory items",
                icon = Icons.Default.Inventory,
                iconTint = Color(0xFF0D9488), // Medical Teal
                modifier = Modifier.width(180.dp),
                onClick = { onNavigate(ScreenDestination.STOCK_MANAGEMENT) }
            )

            StatCard(
                title = "Stock Value",
                value = DateUtils.formatCurrency(totalStockValue),
                subtitle = "Purchase valuation",
                icon = Icons.Default.CurrencyRupee,
                iconTint = MedicalCrossGreen, // Emerald Mint
                modifier = Modifier.width(180.dp)
            )

            StatCard(
                title = "Low Stock Alert",
                value = "$totalLowStockCount",
                subtitle = "${lowStockMedicines.count { it.currentStock == 0 }} out of stock",
                icon = Icons.Default.WarningAmber,
                iconTint = Color(0xFFD97706), // Warning Amber
                modifier = Modifier.width(180.dp),
                onClick = { onNavigate(ScreenDestination.LOW_STOCK) }
            )

            StatCard(
                title = "Expiring Soon",
                value = "${expiringSoonMedicines.size}",
                subtitle = "Within next 30 days",
                icon = Icons.Default.Warning,
                iconTint = Color(0xFFEA580C), // Vitamin Orange / Alert
                modifier = Modifier.width(180.dp),
                onClick = { onNavigate(ScreenDestination.EXPIRY_ALERTS) }
            )

            StatCard(
                title = "Expired Products",
                value = "${expiredMedicines.size}",
                subtitle = "Quarantine & dispose",
                icon = Icons.Default.Warning,
                iconTint = Color(0xFFDC2626), // Emergency Red
                modifier = Modifier.width(180.dp),
                onClick = { onNavigate(ScreenDestination.EXPIRY_ALERTS) }
            )

            StatCard(
                title = "Today's Sales",
                value = DateUtils.formatCurrency(todaySalesTotal),
                subtitle = "${todaySales.size} invoices issued",
                icon = Icons.Default.ReceiptLong,
                iconTint = Color(0xFF2563EB), // Sapphire POS Blue
                modifier = Modifier.width(180.dp),
                onClick = { onNavigate(ScreenDestination.REPORTS) }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Main Dashboard Sections: Stock & Sales Overview
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section A: Stock Distribution & Health
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "A. Stock Overview & Health",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val inStockCount = medicines.count { !it.isLowStock }
                    val lowCount = medicines.count { it.isLowStock && it.currentStock > 0 }
                    val outCount = medicines.count { it.currentStock == 0 }
                    val total = (inStockCount + lowCount + outCount).coerceAtLeast(1)

                    HealthRow(label = "Optimal Stock", count = inStockCount, total = total, color = StatusHealthy)
                    Spacer(modifier = Modifier.height(8.dp))
                    HealthRow(label = "Low Stock Level", count = lowCount, total = total, color = StatusWarning)
                    Spacer(modifier = Modifier.height(8.dp))
                    HealthRow(label = "Critical / Out of Stock", count = outCount, total = total, color = StatusCritical)

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Medicines Stock:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(DateUtils.formatCurrency(totalMedicineValue), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("General Items Stock:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(DateUtils.formatCurrency(totalGeneralValue), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Section B: Sales Performance
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "B. Sales Overview",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Today's Gross Sales", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(DateUtils.formatCurrency(todaySalesTotal), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Monthly Sales", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(DateUtils.formatCurrency(monthlySalesTotal), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Payment Methods Breakdown:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(6.dp))
                    val cashSales = sales.filter { it.paymentMethod == "Cash" }.sumOf { it.grandTotal }
                    val upiSales = sales.filter { it.paymentMethod == "UPI" }.sumOf { it.grandTotal }
                    val cardSales = sales.filter { it.paymentMethod == "Card" }.sumOf { it.grandTotal }

                    PaymentRow("UPI / QR Code", upiSales, Color(0xFF2563EB))
                    PaymentRow("Cash Counter", cashSales, MedicalCrossGreen)
                    PaymentRow("Debit / Credit Card", cardSales, Color(0xFF7C3AED))
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Section C: Expiry Alerts Spotlight
        SectionHeader(
            title = "C. Expiry & Disposal Alerts",
            subtitle = "Active batches requiring immediate action",
            action = {
                Text(
                    text = "View All (${medicines.count { it.daysRemaining <= 90 }}) →",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigate(ScreenDestination.EXPIRY_ALERTS) }
                )
            }
        )

        val nearExpiryList = medicines.filter { it.daysRemaining <= 30 }.take(4)
        if (nearExpiryList.isEmpty()) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusHealthy)
                    Text("No immediate expiry risks in the next 30 days! All stock is healthy.", style = MaterialTheme.typography.bodyMedium)
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                nearExpiryList.forEach { med ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(med.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                Text("Batch: ${med.batchNumber} • Stock: ${med.currentStock} pcs", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            ExpiryBadge(status = med.expiryStatus, days = med.daysRemaining)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Section D: Low Stock Alerts
        SectionHeader(
            title = "D. Low Stock & Reorder Alerts",
            subtitle = "Items at or below minimum threshold",
            action = {
                Text(
                    text = "Reorder List →",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigate(ScreenDestination.LOW_STOCK) }
                )
            }
        )

        val lowStockCombined = (medicines.filter { it.isLowStock } + generalItems.filter { it.isLowStock }).take(4)
        if (lowStockCombined.isEmpty()) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusHealthy)
                    Text("All product stock levels are above minimum threshold.", style = MaterialTheme.typography.bodyMedium)
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                lowStockCombined.forEach { item ->
                    val name = if (item is Medicine) item.name else (item as GeneralItem).name
                    val stock = if (item is Medicine) item.currentStock else (item as GeneralItem).currentStock
                    val minStock = if (item is Medicine) item.minimumStock else (item as GeneralItem).minimumStock
                    val status = if (item is Medicine) item.stockStatus else (item as GeneralItem).stockStatus

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                Text("Available: $stock pcs (Min threshold: $minStock)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            StockBadge(status = status, currentStock = stock, minStock = minStock)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Section E: Recent Activity
        SectionHeader(
            title = "E. Recent Stock Activity & Sales Audit",
            subtitle = "Real-time Marg ERP audit trail",
            action = {
                Text(
                    text = "Full History →",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigate(ScreenDestination.STOCK_MANAGEMENT) }
                )
            }
        )

        val recentTx = transactions.take(5)
        if (recentTx.isEmpty()) {
            Text("No recent transactions recorded.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                recentTx.forEach { tx ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when (tx.transactionType) {
                                                "PURCHASE" -> StatusHealthy.copy(alpha = 0.15f)
                                                "SALE" -> StatusInfo.copy(alpha = 0.15f)
                                                "DISPOSAL" -> StatusCritical.copy(alpha = 0.15f)
                                                else -> StatusWarning.copy(alpha = 0.15f)
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when (tx.transactionType) {
                                            "PURCHASE" -> Icons.Default.Add
                                            "SALE" -> Icons.Default.PointOfSale
                                            "DISPOSAL" -> Icons.Default.Warning
                                            else -> Icons.Default.History
                                        },
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = when (tx.transactionType) {
                                            "PURCHASE" -> StatusHealthy
                                            "SALE" -> StatusInfo
                                            "DISPOSAL" -> StatusCritical
                                            else -> StatusWarning
                                        }
                                    )
                                }
                                Column {
                                    Text(tx.productName, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                                    Text(
                                        "${tx.transactionType} • ${tx.reason} • By ${tx.userId}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = if (tx.quantity > 0) "+${tx.quantity}" else "${tx.quantity}",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (tx.quantity > 0) StatusHealthy else StatusCritical
                                )
                                Text(
                                    text = DateUtils.formatDate(tx.date),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun HealthRow(label: String, count: Int, total: Int, color: Color) {
    val progress = (count.toFloat() / total.toFloat()).coerceIn(0f, 1f)
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("$count items (${(progress * 100).toInt()}%)", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.15f)
        )
    }
}

@Composable
private fun PaymentRow(label: String, amount: Double, color: Color = MaterialTheme.colorScheme.primary) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(DateUtils.formatCurrency(amount), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
    }
}
