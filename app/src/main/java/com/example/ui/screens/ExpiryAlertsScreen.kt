package com.example.ui.screens

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ExpiryStatus
import com.example.data.model.Medicine
import com.example.ui.components.EmptyStateView
import com.example.ui.components.ExpiryBadge
import com.example.ui.components.PharmacySearchBar
import com.example.ui.components.SectionHeader
import com.example.ui.components.StockAdjustmentDialog
import com.example.ui.theme.StatusCritical
import com.example.ui.theme.StatusCriticalBg
import com.example.ui.theme.StatusHealthy
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.StatusWarningBg
import com.example.ui.viewmodel.PharmacyViewModel
import com.example.util.DateUtils

@Composable
fun ExpiryAlertsScreen(
    viewModel: PharmacyViewModel,
    modifier: Modifier = Modifier
) {
    val medicines by viewModel.allMedicines.collectAsStateWithLifecycle()
    val selectedTab by viewModel.expirySelectedTab.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }

    var viewingMedicine by remember { mutableStateOf<Medicine?>(null) }
    var disposingMedicine by remember { mutableStateOf<Medicine?>(null) }
    var adjustingMedicine by remember { mutableStateOf<Medicine?>(null) }

    // Alert Group Tabs
    val tabs = listOf("All Alerts", "Expired (<=0d)", "7 Days", "30 Days", "60 Days", "90 Days")

    // Filter by tab
    val filteredList = remember(medicines, selectedTab, searchQuery) {
        val tabFiltered = when (selectedTab) {
            "Expired (<=0d)" -> medicines.filter { it.daysRemaining <= 0 }
            "7 Days" -> medicines.filter { it.daysRemaining in 1..7 }
            "30 Days" -> medicines.filter { it.daysRemaining in 8..30 }
            "60 Days" -> medicines.filter { it.daysRemaining in 31..60 }
            "90 Days" -> medicines.filter { it.daysRemaining in 61..90 }
            else -> medicines.filter { it.daysRemaining <= 90 } // All monitored
        }

        if (searchQuery.isBlank()) tabFiltered
        else tabFiltered.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.batchNumber.contains(searchQuery, ignoreCase = true) ||
                    it.genericName.contains(searchQuery, ignoreCase = true)
        }
    }

    val expiredCount = medicines.count { it.isExpired }
    val near30Count = medicines.count { !it.isExpired && it.daysRemaining <= 30 }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("expiry_alerts_screen")
    ) {
        SectionHeader(
            title = "Expiry & Disposal Monitoring System",
            subtitle = "Prevent expired drug dispensing with proactive batch monitoring"
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Prominent Alert Banner
        if (expiredCount > 0 || near30Count > 0) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (expiredCount > 0) StatusCriticalBg else StatusWarningBg,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = if (expiredCount > 0) Icons.Default.Error else Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (expiredCount > 0) StatusCritical else StatusWarning,
                        modifier = Modifier.size(28.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (expiredCount > 0)
                                "⚠️ ATTENTION: $expiredCount medicines have EXPIRED and must be quarantined!"
                            else "⚠️ Notice: $near30Count medicines are expiring within the next 30 days.",
                            fontWeight = FontWeight.Bold,
                            color = if (expiredCount > 0) StatusCritical else StatusWarning,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Expired products cannot be billed and must be disposed or returned to suppliers.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Search Bar
        PharmacySearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            placeholderText = "Search expiring medicines by name or batch...",
            modifier = Modifier.fillMaxWidth(),
            testTag = "expiry_search_input"
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Tabs Row
        ScrollableTabRow(
            selectedTabIndex = tabs.indexOf(selectedTab).coerceAtLeast(0),
            edgePadding = 0.dp,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabs.forEach { tabTitle ->
                val isSelected = selectedTab == tabTitle
                val count = when (tabTitle) {
                    "Expired (<=0d)" -> medicines.count { it.daysRemaining <= 0 }
                    "7 Days" -> medicines.count { it.daysRemaining in 1..7 }
                    "30 Days" -> medicines.count { it.daysRemaining in 8..30 }
                    "60 Days" -> medicines.count { it.daysRemaining in 31..60 }
                    "90 Days" -> medicines.count { it.daysRemaining in 61..90 }
                    else -> medicines.count { it.daysRemaining <= 90 }
                }

                Tab(
                    selected = isSelected,
                    onClick = { viewModel.expirySelectedTab.value = tabTitle },
                    text = {
                        Text(
                            text = "$tabTitle ($count)",
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    modifier = Modifier.testTag("expiry_tab_${tabTitle.lowercase().replace(" ", "_")}")
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (filteredList.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.CheckCircle,
                title = "No Medicines in this Expiry Range",
                description = "All products in this category have healthy shelf lives."
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredList, key = { it.id }) { med ->
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
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = med.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "Batch: ${med.batchNumber} • ${med.manufacturer}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                ExpiryBadge(status = med.expiryStatus, days = med.daysRemaining)
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
                                        text = "Expiry Date: ${DateUtils.formatDisplayDate(med.expiryDate)}",
                                        fontWeight = FontWeight.SemiBold,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Text(
                                        text = "Available In-Stock: ${med.currentStock} units (${DateUtils.formatCurrency(med.totalStockValue)})",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    OutlinedButton(
                                        onClick = { viewingMedicine = med },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("View", fontSize = 12.sp)
                                    }

                                    OutlinedButton(
                                        onClick = { adjustingMedicine = med },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Adjust", fontSize = 12.sp)
                                    }

                                    if (med.isExpired && med.currentStock > 0) {
                                        Button(
                                            onClick = { disposingMedicine = med },
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                            modifier = Modifier.testTag("dispose_expired_btn_${med.id}")
                                        ) {
                                            Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Dispose Stock", fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // View Details Modal
    viewingMedicine?.let { med ->
        MedicineDetailDialog(
            medicine = med,
            onDismiss = { viewingMedicine = null },
            onEdit = {
                viewingMedicine = null
                viewModel.editingMedicine.value = med
                viewModel.showAddEditMedicineDialog.value = true
            }
        )
    }

    // Adjust Stock Modal
    adjustingMedicine?.let { med ->
        StockAdjustmentDialog(
            productName = med.name,
            currentStock = med.currentStock,
            onConfirm = { delta, reason ->
                viewModel.adjustStock("MEDICINE", med.id, delta, reason)
                adjustingMedicine = null
            },
            onDismiss = { adjustingMedicine = null }
        )
    }

    // Dispose Confirmation Modal
    disposingMedicine?.let { med ->
        AlertDialog(
            onDismissRequest = { disposingMedicine = null },
            title = { Text("Dispose Expired Medicine") },
            text = {
                Text("Confirm disposal of ${med.currentStock} units of '${med.name}' (Batch: ${med.batchNumber})? This will write off the remaining balance as disposed stock.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.markDisposed(med)
                        disposingMedicine = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Confirm Disposal")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { disposingMedicine = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
