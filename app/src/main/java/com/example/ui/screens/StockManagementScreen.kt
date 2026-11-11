package com.example.ui.screens

import android.content.Intent
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.GeneralItem
import com.example.data.model.Medicine
import com.example.data.model.Purchase
import com.example.data.model.StockStatus
import com.example.data.model.StockTransaction
import com.example.ui.components.EmptyStateView
import com.example.ui.components.PharmacySearchBar
import com.example.ui.components.StatCard
import com.example.ui.components.StockAdjustmentDialog
import com.example.ui.components.StockBadge
import com.example.ui.navigation.ScreenDestination
import com.example.ui.theme.PharmacyPrimary
import com.example.ui.theme.StatusCritical
import com.example.ui.theme.StatusHealthy
import com.example.ui.theme.StatusInfo
import com.example.ui.theme.StatusWarning
import com.example.ui.viewmodel.PharmacyViewModel
import com.example.util.DateUtils

@Composable
fun StockManagementScreen(
    viewModel: PharmacyViewModel,
    onNavigate: (ScreenDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val medicines by viewModel.allMedicines.collectAsStateWithLifecycle()
    val generalItems by viewModel.allGeneralItems.collectAsStateWithLifecycle()
    val transactions by viewModel.allStockTransactions.collectAsStateWithLifecycle()
    val purchases by viewModel.allPurchases.collectAsStateWithLifecycle()

    val selectedTab by viewModel.stockManagementTab.collectAsStateWithLifecycle()
    var stockSearchQuery by remember { mutableStateOf("") }
    var adjustingProduct by remember { mutableStateOf<Triple<String, Long, String>?>(null) } // type, id, name

    // Top summaries
    val totalStockValue = medicines.sumOf { it.totalStockValue } + generalItems.sumOf { it.totalStockValue }
    val lowStockCount = medicines.count { it.isLowStock } + generalItems.count { it.isLowStock }
    val outOfStockCount = medicines.count { it.currentStock == 0 } + generalItems.count { it.currentStock == 0 }
    val recentPurchasesCount = purchases.size

    val tabs = listOf("Current Stock", "Stock History (Audit)", "Purchase History")

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("stock_management_screen")
    ) {
        // Top Action Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Inventory & Stock Central",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Real-time stock valuation & batch ledger",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = {
                        val reportText = buildString {
                            appendLine("=== PHARMACY STOCK REPORT ===")
                            appendLine("Generated: ${DateUtils.formatDateTime(System.currentTimeMillis())}")
                            appendLine("Total Stock Valuation: ${DateUtils.formatCurrency(totalStockValue)}")
                            appendLine("Low Stock Items: $lowStockCount | Out of Stock: $outOfStockCount")
                            appendLine("\n--- MEDICINES ---")
                            medicines.forEach {
                                appendLine("${it.name} | Batch: ${it.batchNumber} | Stock: ${it.currentStock} | Exp: ${it.expiryDate}")
                            }
                            appendLine("\n--- GENERAL ITEMS ---")
                            generalItems.forEach {
                                appendLine("${it.name} | Batch: ${it.batchNumber} | Stock: ${it.currentStock}")
                            }
                        }
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "Pharmacy Inventory Stock Report")
                            putExtra(Intent.EXTRA_TEXT, reportText)
                        }
                        context.startActivity(Intent.createChooser(intent, "Export Stock Report"))
                    },
                    modifier = Modifier.testTag("export_stock_report_btn")
                ) {
                    Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Export Report")
                }

                Button(
                    onClick = { onNavigate(ScreenDestination.ADD_STOCK) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.testTag("stock_add_inward_btn")
                ) {
                    Icon(Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Inward Stock")
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Top Summary Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                title = "Total Valuation",
                value = DateUtils.formatCurrency(totalStockValue),
                subtitle = "${medicines.size + generalItems.size} products",
                icon = Icons.Default.CurrencyRupee,
                iconTint = StatusHealthy,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Low Stock",
                value = "$lowStockCount",
                subtitle = "Needs reorder",
                icon = Icons.Default.WarningAmber,
                iconTint = StatusWarning,
                modifier = Modifier.weight(1f),
                onClick = { onNavigate(ScreenDestination.LOW_STOCK) }
            )
            StatCard(
                title = "Out of Stock",
                value = "$outOfStockCount",
                subtitle = "Zero balance",
                icon = Icons.Default.Warning,
                iconTint = StatusCritical,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Purchases",
                value = "$recentPurchasesCount",
                subtitle = "Orders logged",
                icon = Icons.Default.Receipt,
                iconTint = StatusInfo,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            edgePadding = 0.dp,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabs.forEachIndexed { index, tabTitle ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { viewModel.stockManagementTab.value = index },
                    text = {
                        Text(
                            text = tabTitle,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    modifier = Modifier.testTag("stock_tab_$index")
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        when (selectedTab) {
            0 -> CurrentStockTab(
                medicines = medicines,
                generalItems = generalItems,
                searchQuery = stockSearchQuery,
                onSearchChange = { stockSearchQuery = it },
                onAdjust = { type, id, name -> adjustingProduct = Triple(type, id, name) }
            )
            1 -> StockHistoryTab(
                transactions = transactions,
                searchQuery = stockSearchQuery,
                onSearchChange = { stockSearchQuery = it }
            )
            2 -> PurchaseHistoryTab(
                purchases = purchases,
                searchQuery = stockSearchQuery,
                onSearchChange = { stockSearchQuery = it }
            )
        }
    }

    // Adjustment Dialog
    adjustingProduct?.let { (type, id, name) ->
        val currentStock = if (type == "MEDICINE") {
            medicines.find { it.id == id }?.currentStock ?: 0
        } else {
            generalItems.find { it.id == id }?.currentStock ?: 0
        }

        StockAdjustmentDialog(
            productName = name,
            currentStock = currentStock,
            onConfirm = { delta, reason ->
                viewModel.adjustStock(type, id, delta, reason)
                adjustingProduct = null
            },
            onDismiss = { adjustingProduct = null }
        )
    }
}

@Composable
private fun CurrentStockTab(
    medicines: List<Medicine>,
    generalItems: List<GeneralItem>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onAdjust: (type: String, id: Long, name: String) -> Unit
) {
    // Combined list of stock entries
    data class StockRowItem(
        val id: Long,
        val type: String, // "Medicine" or "General"
        val name: String,
        val batchNumber: String,
        val currentStock: Int,
        val minStock: Int,
        val unitPrice: Double,
        val stockValue: Double,
        val stockStatus: StockStatus,
        val lastUpdated: Long
    )

    val rows = remember(medicines, generalItems) {
        val medRows = medicines.map {
            StockRowItem(
                id = it.id,
                type = "Medicine",
                name = it.name,
                batchNumber = it.batchNumber,
                currentStock = it.currentStock,
                minStock = it.minimumStock,
                unitPrice = it.purchasePrice,
                stockValue = it.totalStockValue,
                stockStatus = it.stockStatus,
                lastUpdated = it.updatedAt
            )
        }
        val genRows = generalItems.map {
            StockRowItem(
                id = it.id,
                type = "General",
                name = it.name,
                batchNumber = it.batchNumber,
                currentStock = it.currentStock,
                minStock = it.minimumStock,
                unitPrice = it.purchasePrice,
                stockValue = it.totalStockValue,
                stockStatus = it.stockStatus,
                lastUpdated = it.updatedAt
            )
        }
        (medRows + genRows).sortedBy { it.name }
    }

    val filteredRows = remember(rows, searchQuery) {
        if (searchQuery.isBlank()) rows
        else rows.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.batchNumber.contains(searchQuery, ignoreCase = true) ||
                    it.type.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        PharmacySearchBar(
            query = searchQuery,
            onQueryChange = onSearchChange,
            placeholderText = "Filter by product name, batch, or type...",
            modifier = Modifier.fillMaxWidth(),
            testTag = "stock_tab_search"
        )
        Spacer(modifier = Modifier.height(10.dp))

        if (filteredRows.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.Inventory,
                title = "No Inventory Items Found",
                description = "No items match your filter criteria."
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredRows, key = { "${it.type}_${it.id}" }) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1.5f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Surface(
                                        color = if (item.type == "Medicine")
                                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                        else MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = item.type,
                                            style = MaterialTheme.typography.labelSmall,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Text(
                                        text = item.name,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyMedium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Batch: ${item.batchNumber} • Value: ${DateUtils.formatCurrency(item.stockValue)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                StockBadge(status = item.stockStatus, currentStock = item.currentStock, minStock = item.minStock)

                                OutlinedButton(
                                    onClick = { onAdjust(if (item.type == "Medicine") "MEDICINE" else "GENERAL", item.id, item.name) },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.size(36.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                                ) {
                                    Icon(Icons.Default.Tune, contentDescription = "Adjust", modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StockHistoryTab(
    transactions: List<StockTransaction>,
    searchQuery: String,
    onSearchChange: (String) -> Unit
) {
    val filtered = remember(transactions, searchQuery) {
        if (searchQuery.isBlank()) transactions
        else transactions.filter {
            it.productName.contains(searchQuery, ignoreCase = true) ||
                    it.batchNumber.contains(searchQuery, ignoreCase = true) ||
                    it.reason.contains(searchQuery, ignoreCase = true) ||
                    it.transactionType.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        PharmacySearchBar(
            query = searchQuery,
            onQueryChange = onSearchChange,
            placeholderText = "Search transactions by item, reason, or batch...",
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))

        if (filtered.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.History,
                title = "No Transaction History",
                description = "Stock additions, sales, and manual adjustments will appear here."
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filtered, key = { it.id }) { tx ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1.5f),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
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
                                            else -> Icons.Default.Tune
                                        },
                                        contentDescription = null,
                                        tint = when (tx.transactionType) {
                                            "PURCHASE" -> StatusHealthy
                                            "SALE" -> StatusInfo
                                            "DISPOSAL" -> StatusCritical
                                            else -> StatusWarning
                                        },
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                Column {
                                    Text(tx.productName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                    Text(
                                        "${tx.transactionType} • Batch ${tx.batchNumber} • By ${tx.userId}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    if (tx.reason.isNotBlank()) {
                                        Text(
                                            tx.reason,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.primary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = if (tx.quantity > 0) "+${tx.quantity}" else "${tx.quantity}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (tx.quantity > 0) StatusHealthy else StatusCritical
                                )
                                Text(
                                    text = "Stock: ${tx.previousStock} → ${tx.newStock}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = DateUtils.formatDateTime(tx.date),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PurchaseHistoryTab(
    purchases: List<Purchase>,
    searchQuery: String,
    onSearchChange: (String) -> Unit
) {
    val filtered = remember(purchases, searchQuery) {
        if (searchQuery.isBlank()) purchases
        else purchases.filter {
            it.supplierName.contains(searchQuery, ignoreCase = true) ||
                    it.invoiceNumber.contains(searchQuery, ignoreCase = true) ||
                    it.itemsSummary.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        PharmacySearchBar(
            query = searchQuery,
            onQueryChange = onSearchChange,
            placeholderText = "Search purchase invoices or supplier...",
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))

        if (filtered.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.Receipt,
                title = "No Purchase Entries",
                description = "Inward purchase invoices from pharmaceutical suppliers will appear here."
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filtered, key = { it.id }) { purchase ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(purchase.supplierName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                    Text("Invoice #: ${purchase.invoiceNumber}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                }
                                Text(
                                    DateUtils.formatCurrency(purchase.totalAmount),
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "Items: ${purchase.itemsSummary}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Date: ${DateUtils.formatDateTime(purchase.purchaseDate)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
