package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.ChipFilterRow
import com.example.ui.components.EmptyStateView
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatCard
import com.example.ui.theme.StatusCritical
import com.example.ui.theme.StatusHealthy
import com.example.ui.theme.StatusInfo
import com.example.ui.theme.StatusWarning
import com.example.ui.viewmodel.PharmacyViewModel
import com.example.util.DateUtils

@Composable
fun ReportsScreen(
    viewModel: PharmacyViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val sales by viewModel.allSales.collectAsStateWithLifecycle()
    val medicines by viewModel.allMedicines.collectAsStateWithLifecycle()
    val generalItems by viewModel.allGeneralItems.collectAsStateWithLifecycle()
    val purchases by viewModel.allPurchases.collectAsStateWithLifecycle()

    val reportType by viewModel.reportType.collectAsStateWithLifecycle()
    val dateRange by viewModel.reportDateRange.collectAsStateWithLifecycle()

    val reportTypes = listOf(
        "Daily Sales",
        "Monthly Sales",
        "Stock Valuation",
        "Low Stock Report",
        "Expiry Report",
        "Purchase Report"
    )

    val dateRanges = listOf("Today", "This Week", "This Month", "All Time")

    // Filter sales by date range
    val now = System.currentTimeMillis()
    val filteredSales = remember(sales, dateRange) {
        when (dateRange) {
            "Today" -> sales.filter { DateUtils.formatDate(it.createdAt) == DateUtils.formatDate(now) }
            "This Week" -> sales.filter { (now - it.createdAt) <= 7 * 86400000L }
            "This Month" -> sales.filter { (now - it.createdAt) <= 30 * 86400000L }
            else -> sales
        }
    }

    val totalSalesRevenue = filteredSales.sumOf { it.grandTotal }
    val totalDiscountsGiven = filteredSales.sumOf { it.discount }
    val totalTaxCollected = filteredSales.sumOf { it.tax }
    val invoiceCount = filteredSales.size

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("reports_screen")
    ) {
        // Header & Export
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Reports & Business Intelligence",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Marg ERP compliant statements, tax & inventory reports",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            OutlinedButton(
                onClick = {
                    val exportContent = buildString {
                        appendLine("=== PHARMACY $reportType.uppercase() ===")
                        appendLine("Period: $dateRange | Generated: ${DateUtils.formatDateTime(now)}")
                        appendLine("Total Revenue: ${DateUtils.formatCurrency(totalSalesRevenue)}")
                        appendLine("Total Invoices: $invoiceCount\n")
                        filteredSales.forEach { s ->
                            appendLine("Inv #${s.invoiceNumber} | ${DateUtils.formatDate(s.createdAt)} | Customer: ${s.customerName} | Paid: ${DateUtils.formatCurrency(s.grandTotal)} (${s.paymentMethod})")
                        }
                    }
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, "$reportType - $dateRange")
                        putExtra(Intent.EXTRA_TEXT, exportContent)
                    }
                    context.startActivity(Intent.createChooser(intent, "Share Report"))
                },
                modifier = Modifier.testTag("share_report_btn")
            ) {
                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Export / Share")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Report Type Tabs
        ScrollableTabRow(
            selectedTabIndex = reportTypes.indexOf(reportType).coerceAtLeast(0),
            edgePadding = 0.dp,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth()
        ) {
            reportTypes.forEach { rType ->
                Tab(
                    selected = reportType == rType,
                    onClick = { viewModel.reportType.value = rType },
                    text = {
                        Text(rType, fontWeight = if (reportType == rType) FontWeight.Bold else FontWeight.Normal)
                    },
                    modifier = Modifier.testTag("report_tab_${rType.lowercase().replace(" ", "_")}")
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Date Range Selector
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Date Range: ", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.width(90.dp))
            ChipFilterRow(
                options = dateRanges,
                selectedOption = dateRange,
                onOptionSelected = { viewModel.reportDateRange.value = it }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Top Summary Cards for this report
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                title = "Total Revenue",
                value = DateUtils.formatCurrency(totalSalesRevenue),
                subtitle = "$invoiceCount sales billed",
                icon = Icons.Default.CurrencyRupee,
                iconTint = StatusHealthy,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Discounts Given",
                value = DateUtils.formatCurrency(totalDiscountsGiven),
                subtitle = "Promotional concessions",
                icon = Icons.Default.Assessment,
                iconTint = StatusWarning,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Tax (GST)",
                value = DateUtils.formatCurrency(totalTaxCollected),
                subtitle = "Output tax collected",
                icon = Icons.Default.Receipt,
                iconTint = StatusInfo,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Detailed Report Content
        when (reportType) {
            "Daily Sales", "Monthly Sales" -> {
                if (filteredSales.isEmpty()) {
                    EmptyStateView(
                        icon = Icons.Default.Assessment,
                        title = "No Sales in this Period",
                        description = "There are no recorded invoices matching the selected date range."
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredSales, key = { it.id }) { sale ->
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
                                            Text("Invoice #${sale.invoiceNumber}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                            Text(
                                                "Customer: ${sale.customerName} • ${DateUtils.formatDateTime(sale.createdAt)}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                DateUtils.formatCurrency(sale.grandTotal),
                                                fontWeight = FontWeight.Bold,
                                                style = MaterialTheme.typography.titleMedium,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            Text(
                                                "Paid: ${sale.paymentMethod}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))
                                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        "Items: " + sale.items.joinToString(", ") { "${it.productName} (${it.quantity})" },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
            "Stock Valuation" -> {
                val allProducts = (medicines.map { it.name to it.totalStockValue } + generalItems.map { it.name to it.totalStockValue })
                    .sortedByDescending { it.second }
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(allProducts) { (name, value) ->
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
                                Text(name, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                                Text(DateUtils.formatCurrency(value), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
            "Low Stock Report" -> {
                val lowList = (medicines.filter { it.isLowStock } + generalItems.filter { it.isLowStock })
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(lowList) { item ->
                        val name = if (item is com.example.data.model.Medicine) item.name else (item as com.example.data.model.GeneralItem).name
                        val stock = if (item is com.example.data.model.Medicine) item.currentStock else (item as com.example.data.model.GeneralItem).currentStock
                        val min = if (item is com.example.data.model.Medicine) item.minimumStock else (item as com.example.data.model.GeneralItem).minimumStock

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
                                Column {
                                    Text(name, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                                    Text("Min Threshold: $min units", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Text(
                                    "$stock units",
                                    fontWeight = FontWeight.Bold,
                                    color = if (stock == 0) StatusCritical else StatusWarning
                                )
                            }
                        }
                    }
                }
            }
            "Expiry Report" -> {
                val expiring = medicines.filter { it.daysRemaining <= 90 }.sortedBy { it.daysRemaining }
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(expiring) { med ->
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
                                Column {
                                    Text(med.name, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                                    Text("Batch: ${med.batchNumber} • Expiry: ${DateUtils.formatDisplayDate(med.expiryDate)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Text(
                                    if (med.daysRemaining <= 0) "EXPIRED (${-med.daysRemaining}d)" else "${med.daysRemaining}d left",
                                    fontWeight = FontWeight.Bold,
                                    color = if (med.daysRemaining <= 0) StatusCritical else StatusWarning
                                )
                            }
                        }
                    }
                }
            }
            "Purchase Report" -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(purchases) { p ->
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
                                Column {
                                    Text(p.supplierName, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                                    Text("Invoice #${p.invoiceNumber} • ${DateUtils.formatDate(p.purchaseDate)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Text(DateUtils.formatCurrency(p.totalAmount), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }
    }
}
