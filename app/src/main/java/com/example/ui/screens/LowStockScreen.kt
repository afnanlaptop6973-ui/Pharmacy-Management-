package com.example.ui.screens

import android.content.Intent
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.GeneralItem
import com.example.data.model.Medicine
import com.example.data.model.StockStatus
import com.example.ui.components.ChipFilterRow
import com.example.ui.components.EmptyStateView
import com.example.ui.components.PharmacySearchBar
import com.example.ui.components.SectionHeader
import com.example.ui.components.StockAdjustmentDialog
import com.example.ui.components.StockBadge
import com.example.ui.navigation.ScreenDestination
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.StatusWarningBg
import com.example.ui.viewmodel.PharmacyViewModel

@Composable
fun LowStockScreen(
    viewModel: PharmacyViewModel,
    onNavigate: (ScreenDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val medicines by viewModel.allMedicines.collectAsStateWithLifecycle()
    val generalItems by viewModel.allGeneralItems.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.lowStockFilter.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    var adjustingProduct by remember { mutableStateOf<Triple<String, Long, String>?>(null) }

    data class LowStockItemRow(
        val id: Long,
        val type: String, // "Medicine" or "General"
        val name: String,
        val batchNumber: String,
        val currentStock: Int,
        val minStock: Int,
        val suggestedReorder: Int,
        val status: StockStatus,
        val isCritical: Boolean
    )

    val filterOptions = listOf("All", "Medicines", "General Items", "Critical Items Only")

    val lowStockList = remember(medicines, generalItems, selectedFilter, searchQuery) {
        val medRows = medicines.filter { it.isLowStock }.map {
            LowStockItemRow(
                id = it.id,
                type = "Medicine",
                name = it.name,
                batchNumber = it.batchNumber,
                currentStock = it.currentStock,
                minStock = it.minimumStock,
                suggestedReorder = it.suggestedReorderQty,
                status = it.stockStatus,
                isCritical = it.currentStock <= (it.minimumStock / 2)
            )
        }
        val genRows = generalItems.filter { it.isLowStock }.map {
            LowStockItemRow(
                id = it.id,
                type = "General",
                name = it.name,
                batchNumber = it.batchNumber,
                currentStock = it.currentStock,
                minStock = it.minimumStock,
                suggestedReorder = it.suggestedReorderQty,
                status = it.stockStatus,
                isCritical = it.currentStock <= (it.minimumStock / 2)
            )
        }

        val combined = when (selectedFilter) {
            "Medicines" -> medRows
            "General Items" -> genRows
            "Critical Items Only" -> (medRows + genRows).filter { it.isCritical }
            else -> medRows + genRows
        }

        if (searchQuery.isBlank()) combined
        else combined.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.batchNumber.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("low_stock_screen")
    ) {
        // Top Header & Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Low Stock & Reorder Management",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Automated minimum inventory threshold alerts",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = {
                        val reorderSheet = buildString {
                            appendLine("=== PHARMACY PURCHASE REORDER LIST ===")
                            appendLine("Date: ${com.example.util.DateUtils.getCurrentDateString()}")
                            appendLine("Total Items Requiring Restock: ${lowStockList.size}\n")
                            lowStockList.forEachIndexed { i, itm ->
                                appendLine("${i + 1}. [${itm.type}] ${itm.name} (Batch: ${itm.batchNumber})")
                                appendLine("   Current: ${itm.currentStock} | Min Threshold: ${itm.minStock} | Suggested Order: ${itm.suggestedReorder} units\n")
                            }
                        }
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "Pharmacy Supplier Reorder Sheet")
                            putExtra(Intent.EXTRA_TEXT, reorderSheet)
                        }
                        context.startActivity(Intent.createChooser(intent, "Export Reorder List"))
                    },
                    modifier = Modifier.testTag("export_reorder_list_btn")
                ) {
                    Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Export Reorder List")
                }

                Button(
                    onClick = { onNavigate(ScreenDestination.ADD_STOCK) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.testTag("low_stock_inward_btn")
                ) {
                    Icon(Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Inward Stock")
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Search Bar
        PharmacySearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            placeholderText = "Search low stock items...",
            modifier = Modifier.fillMaxWidth(),
            testTag = "low_stock_search_input"
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Filters Row
        ChipFilterRow(
            options = filterOptions,
            selectedOption = selectedFilter,
            onOptionSelected = { viewModel.lowStockFilter.value = it }
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (lowStockList.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.CheckCircle,
                title = "All Stocks Healthy",
                description = "No products have reached or breached their minimum stock thresholds."
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(lowStockList, key = { "${it.type}_${it.id}" }) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1.5f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
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
                                        text = "Batch: ${item.batchNumber}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                StockBadge(status = item.status, currentStock = item.currentStock, minStock = item.minStock)
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Current Stock: ${item.currentStock} units | Min Level: ${item.minStock}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Text(
                                        text = "Suggested Reorder Qty: ${item.suggestedReorder} units",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = {
                                            adjustingProduct = Triple(if (item.type == "Medicine") "MEDICINE" else "GENERAL", item.id, item.name)
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                        modifier = Modifier.testTag("add_stock_btn_${item.id}")
                                    ) {
                                        Icon(Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Add Stock", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

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
