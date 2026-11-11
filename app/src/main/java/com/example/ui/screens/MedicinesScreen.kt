package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ExpiryStatus
import com.example.data.model.Medicine
import com.example.data.model.StockStatus
import com.example.ui.components.ChipFilterRow
import com.example.ui.components.EmptyStateView
import com.example.ui.components.ExpiryBadge
import com.example.ui.components.MedicineCategoryBadge
import com.example.ui.components.PharmacySearchBar
import com.example.ui.components.StockAdjustmentDialog
import com.example.ui.components.StockBadge
import com.example.ui.theme.MedicalCrossGreen
import com.example.ui.theme.getMedicineCategoryVisual
import com.example.ui.viewmodel.PharmacyViewModel
import com.example.util.DateUtils

@Composable
fun MedicinesScreen(
    viewModel: PharmacyViewModel,
    modifier: Modifier = Modifier
) {
    val medicines by viewModel.allMedicines.collectAsStateWithLifecycle()
    val searchQuery by viewModel.medicineSearchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.medicineCategoryFilter.collectAsStateWithLifecycle()
    val selectedManufacturer by viewModel.medicineManufacturerFilter.collectAsStateWithLifecycle()
    val selectedStockFilter by viewModel.medicineStockFilter.collectAsStateWithLifecycle()
    val selectedExpiryFilter by viewModel.medicineExpiryFilter.collectAsStateWithLifecycle()

    val showAddEditDialog by viewModel.showAddEditMedicineDialog.collectAsStateWithLifecycle()
    val editingMedicine by viewModel.editingMedicine.collectAsStateWithLifecycle()

    var viewingMedicine by remember { mutableStateOf<Medicine?>(null) }
    var medicineToDelete by remember { mutableStateOf<Medicine?>(null) }
    var adjustingMedicine by remember { mutableStateOf<Medicine?>(null) }

    // Categories and Manufacturers for dynamic filter chips
    val categories = remember(medicines) {
        listOf("All") + medicines.map { it.category }.distinct().filter { it.isNotBlank() }
    }
    val manufacturers = remember(medicines) {
        listOf("All") + medicines.map { it.manufacturer }.distinct().filter { it.isNotBlank() }
    }

    // Filtered list
    val filteredMedicines = remember(medicines, searchQuery, selectedCategory, selectedManufacturer, selectedStockFilter, selectedExpiryFilter) {
        medicines.filter { med ->
            val matchesSearch = searchQuery.isBlank() ||
                    med.name.contains(searchQuery, ignoreCase = true) ||
                    med.genericName.contains(searchQuery, ignoreCase = true) ||
                    med.brandName.contains(searchQuery, ignoreCase = true) ||
                    med.batchNumber.contains(searchQuery, ignoreCase = true)

            val matchesCategory = selectedCategory == "All" || med.category == selectedCategory
            val matchesManufacturer = selectedManufacturer == "All" || med.manufacturer == selectedManufacturer

            val matchesStock = when (selectedStockFilter) {
                "In Stock" -> med.currentStock > med.minimumStock
                "Low Stock" -> med.isLowStock && med.currentStock > 0
                "Out of Stock" -> med.currentStock == 0
                else -> true
            }

            val matchesExpiry = when (selectedExpiryFilter) {
                "Expired" -> med.isExpired
                "Expiring Soon" -> !med.isExpired && med.daysRemaining <= 30
                "Good" -> med.daysRemaining > 30
                else -> true
            }

            matchesSearch && matchesCategory && matchesManufacturer && matchesStock && matchesExpiry
        }
    }

    Box(modifier = modifier.fillMaxSize().testTag("medicines_screen")) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Top Controls: Search + Add Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PharmacySearchBar(
                    query = searchQuery,
                    onQueryChange = { viewModel.medicineSearchQuery.value = it },
                    placeholderText = "Search medicine, generic name, brand or batch...",
                    modifier = Modifier.weight(1f),
                    testTag = "medicine_search_input"
                )

                Button(
                    onClick = {
                        viewModel.editingMedicine.value = null
                        viewModel.showAddEditMedicineDialog.value = true
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.testTag("add_medicine_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Medicine")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Multi-level Filters
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                // Category Filter Row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Category: ", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.width(90.dp))
                    ChipFilterRow(
                        options = categories,
                        selectedOption = selectedCategory,
                        onOptionSelected = { viewModel.medicineCategoryFilter.value = it }
                    )
                }

                // Stock Status Filter Row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Stock Status: ", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.width(90.dp))
                    ChipFilterRow(
                        options = listOf("All", "In Stock", "Low Stock", "Out of Stock"),
                        selectedOption = selectedStockFilter,
                        onOptionSelected = { viewModel.medicineStockFilter.value = it }
                    )
                }

                // Expiry Status Filter Row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Expiry: ", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.width(90.dp))
                    ChipFilterRow(
                        options = listOf("All", "Expired", "Expiring Soon", "Good"),
                        selectedOption = selectedExpiryFilter,
                        onOptionSelected = { viewModel.medicineExpiryFilter.value = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Summary bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Showing ${filteredMedicines.size} of ${medicines.size} medicines",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (searchQuery.isNotBlank() || selectedCategory != "All" || selectedStockFilter != "All" || selectedExpiryFilter != "All") {
                    TextButton(onClick = {
                        viewModel.medicineSearchQuery.value = ""
                        viewModel.medicineCategoryFilter.value = "All"
                        viewModel.medicineManufacturerFilter.value = "All"
                        viewModel.medicineStockFilter.value = "All"
                        viewModel.medicineExpiryFilter.value = "All"
                    }) {
                        Text("Reset Filters", fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Medicines List
            if (filteredMedicines.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.Medication,
                    title = "No Medicines Found",
                    description = "Try adjusting your search query or filters, or add a new medicine to inventory.",
                    actionButton = {
                        Button(onClick = {
                            viewModel.editingMedicine.value = null
                            viewModel.showAddEditMedicineDialog.value = true
                        }) {
                            Text("Add Medicine")
                        }
                    }
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredMedicines, key = { it.id }) { med ->
                        MedicineCardItem(
                            medicine = med,
                            onView = { viewingMedicine = med },
                            onEdit = {
                                viewModel.editingMedicine.value = med
                                viewModel.showAddEditMedicineDialog.value = true
                            },
                            onAddStock = { adjustingMedicine = med },
                            onDelete = { medicineToDelete = med },
                            onAddToCart = { viewModel.addMedicineToCart(med) }
                        )
                    }
                }
            }
        }
    }

    // Add / Edit Medicine Dialog Form
    if (showAddEditDialog) {
        AddEditMedicineDialog(
            initialMedicine = editingMedicine,
            onDismiss = {
                viewModel.showAddEditMedicineDialog.value = false
                viewModel.editingMedicine.value = null
            },
            onSave = { savedMed ->
                viewModel.saveMedicine(savedMed)
            }
        )
    }

    // View Medicine Details Modal
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

    // Stock Adjustment Dialog
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

    // Delete Confirmation Dialog
    medicineToDelete?.let { med ->
        AlertDialog(
            onDismissRequest = { medicineToDelete = null },
            title = { Text("Delete Medicine") },
            text = { Text("Are you sure you want to delete '${med.name}' (Batch: ${med.batchNumber})? This will permanently remove its inventory records.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteMedicine(med)
                        medicineToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { medicineToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun MedicineCardItem(
    medicine: Medicine,
    onView: () -> Unit,
    onEdit: () -> Unit,
    onAddStock: () -> Unit,
    onDelete: () -> Unit,
    onAddToCart: () -> Unit
) {
    val categoryVisual = getMedicineCategoryVisual(medicine.category)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onView() }
            .testTag("medicine_card_${medicine.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Avatar Icon + Name + Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(categoryVisual.containerColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = categoryVisual.icon,
                            contentDescription = medicine.category,
                            tint = categoryVisual.iconColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = medicine.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (medicine.genericName.isNotBlank() || medicine.brandName.isNotBlank()) {
                            Text(
                                text = "${medicine.genericName} • Brand: ${medicine.brandName}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        MedicineCategoryBadge(category = medicine.category)
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    ExpiryBadge(status = medicine.expiryStatus, days = medicine.daysRemaining)
                    StockBadge(status = medicine.stockStatus, currentStock = medicine.currentStock, minStock = medicine.minimumStock)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(8.dp))

            // ERP Data Grid: Batch, Category, Manufacturer, Rack, Prices
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Batch #: ${medicine.batchNumber}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                    Text("Mfg: ${medicine.manufacturer}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(2.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(
                            text = "Rack: ${medicine.rackLocation}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                    Text("Selling Price: ${DateUtils.formatCurrency(medicine.sellingPrice)}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = MedicalCrossGreen)
                    Text("MRP: ${DateUtils.formatCurrency(medicine.mrp)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Purchase: ${DateUtils.formatCurrency(medicine.purchasePrice)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Exp Date: ${DateUtils.formatDisplayDate(medicine.expiryDate)}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onView, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Visibility, contentDescription = "View Details", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Medicine", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = onAddStock, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.AddShoppingCart, contentDescription = "Adjust Stock", tint = MaterialTheme.colorScheme.secondary)
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Medicine", tint = MaterialTheme.colorScheme.error)
                    }
                }

                Button(
                    onClick = onAddToCart,
                    enabled = !medicine.isExpired && medicine.currentStock > 0,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MedicalCrossGreen,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.testTag("add_to_bill_${medicine.id}")
                ) {
                    Text(
                        text = if (medicine.isExpired) "Expired" else if (medicine.currentStock == 0) "Out of Stock" else "+ Bill",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun AddEditMedicineDialog(
    initialMedicine: Medicine?,
    onDismiss: () -> Unit,
    onSave: (Medicine) -> Unit
) {
    val isEdit = initialMedicine != null
    var name by remember { mutableStateOf(initialMedicine?.name ?: "") }
    var genericName by remember { mutableStateOf(initialMedicine?.genericName ?: "") }
    var brandName by remember { mutableStateOf(initialMedicine?.brandName ?: "") }
    var category by remember { mutableStateOf(initialMedicine?.category ?: "Antibiotics") }
    var manufacturer by remember { mutableStateOf(initialMedicine?.manufacturer ?: "Cipla Ltd.") }
    var batchNumber by remember { mutableStateOf(initialMedicine?.batchNumber ?: "BT-${System.currentTimeMillis() % 10000}") }
    var currentStockText by remember { mutableStateOf(initialMedicine?.currentStock?.toString() ?: "50") }
    var minStockText by remember { mutableStateOf(initialMedicine?.minimumStock?.toString() ?: "15") }
    var purchasePriceText by remember { mutableStateOf(initialMedicine?.purchasePrice?.toString() ?: "45.0") }
    var sellingPriceText by remember { mutableStateOf(initialMedicine?.sellingPrice?.toString() ?: "75.0") }
    var mrpText by remember { mutableStateOf(initialMedicine?.mrp?.toString() ?: "85.0") }
    var gstText by remember { mutableStateOf(initialMedicine?.gstPercentage?.toString() ?: "12.0") }
    var mfgDate by remember { mutableStateOf(initialMedicine?.manufacturingDate ?: "2024-06-01") }
    var expiryDate by remember { mutableStateOf(initialMedicine?.expiryDate ?: "2027-12-31") }
    var rackLocation by remember { mutableStateOf(initialMedicine?.rackLocation ?: "Rack A-01") }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth(0.98f)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = if (isEdit) "Edit Medicine Details" else "Add New Medicine",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Marg ERP 9 compliant master record",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                if (errorMessage != null) {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    ) {
                        Text(
                            text = errorMessage!!,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                // Section 1: Basic Information
                Text("1. Basic Information", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Medicine Name *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("med_input_name")
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = genericName,
                        onValueChange = { genericName = it },
                        label = { Text("Generic Name") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = brandName,
                        onValueChange = { brandName = it },
                        label = { Text("Brand Name") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Category") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = manufacturer,
                        onValueChange = { manufacturer = it },
                        label = { Text("Manufacturer / Company") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 2: Batch and Stock Information
                Text("2. Batch & Stock Information", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = batchNumber,
                        onValueChange = { batchNumber = it },
                        label = { Text("Batch Number *") },
                        singleLine = true,
                        modifier = Modifier.weight(1.2f).testTag("med_input_batch")
                    )
                    OutlinedTextField(
                        value = rackLocation,
                        onValueChange = { rackLocation = it },
                        label = { Text("Rack / Shelf") },
                        singleLine = true,
                        modifier = Modifier.weight(0.8f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = currentStockText,
                        onValueChange = { currentStockText = it.filter { c -> c.isDigit() } },
                        label = { Text("Current Stock *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("med_input_stock")
                    )
                    OutlinedTextField(
                        value = minStockText,
                        onValueChange = { minStockText = it.filter { c -> c.isDigit() } },
                        label = { Text("Min Stock Alert Level") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = mfgDate,
                        onValueChange = { mfgDate = it },
                        label = { Text("Mfg Date (YYYY-MM-DD)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = expiryDate,
                        onValueChange = { expiryDate = it },
                        label = { Text("Expiry Date * (YYYY-MM-DD)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("med_input_expiry")
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 3: Pricing and Tax
                Text("3. Price Information & GST", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = purchasePriceText,
                        onValueChange = { purchasePriceText = it },
                        label = { Text("Purchase Price (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = sellingPriceText,
                        onValueChange = { sellingPriceText = it },
                        label = { Text("Selling Price (₹) *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("med_input_selling_price")
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = mrpText,
                        onValueChange = { mrpText = it },
                        label = { Text("MRP (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = gstText,
                        onValueChange = { gstText = it },
                        label = { Text("GST %") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Form Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (name.isBlank()) {
                                errorMessage = "Medicine Name is required."
                                return@Button
                            }
                            if (batchNumber.isBlank()) {
                                errorMessage = "Batch Number is required."
                                return@Button
                            }
                            if (expiryDate.isBlank()) {
                                errorMessage = "Expiry Date is required (format YYYY-MM-DD)."
                                return@Button
                            }
                            val stock = currentStockText.toIntOrNull() ?: 0
                            val minStock = minStockText.toIntOrNull() ?: 10
                            val purchase = purchasePriceText.toDoubleOrNull() ?: 0.0
                            val selling = sellingPriceText.toDoubleOrNull() ?: 0.0
                            val mrp = mrpText.toDoubleOrNull() ?: selling
                            val gst = gstText.toDoubleOrNull() ?: 12.0

                            val updated = (initialMedicine ?: Medicine(
                                name = name,
                                batchNumber = batchNumber,
                                expiryDate = expiryDate
                            )).copy(
                                name = name.trim(),
                                genericName = genericName.trim(),
                                brandName = brandName.trim(),
                                category = category.trim(),
                                manufacturer = manufacturer.trim(),
                                batchNumber = batchNumber.trim(),
                                currentStock = stock,
                                minimumStock = minStock,
                                purchasePrice = purchase,
                                sellingPrice = selling,
                                mrp = mrp,
                                gstPercentage = gst,
                                manufacturingDate = mfgDate.trim(),
                                expiryDate = expiryDate.trim(),
                                rackLocation = rackLocation.trim(),
                                updatedAt = System.currentTimeMillis()
                            )
                            onSave(updated)
                        },
                        modifier = Modifier.testTag("submit_medicine_button")
                    ) {
                        Text(if (isEdit) "Update Medicine" else "Save Medicine")
                    }
                }
            }
        }
    }
}

@Composable
fun MedicineDetailDialog(
    medicine: Medicine,
    onDismiss: () -> Unit,
    onEdit: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth(0.95f)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Medicine Specifications",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        ExpiryBadge(status = medicine.expiryStatus, days = medicine.daysRemaining)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = medicine.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Generic: ${medicine.genericName} | Brand: ${medicine.brandName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))
                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(10.dp))

                DetailRow("Category", medicine.category)
                DetailRow("Manufacturer", medicine.manufacturer)
                DetailRow("Batch Number", medicine.batchNumber)
                DetailRow("Rack Location", medicine.rackLocation)
                DetailRow("Current Available Stock", "${medicine.currentStock} units")
                DetailRow("Minimum Stock Threshold", "${medicine.minimumStock} units")
                DetailRow("Suggested Reorder Qty", "${medicine.suggestedReorderQty} units")
                DetailRow("Purchase Price", DateUtils.formatCurrency(medicine.purchasePrice))
                DetailRow("Selling Price", DateUtils.formatCurrency(medicine.sellingPrice))
                DetailRow("MRP", DateUtils.formatCurrency(medicine.mrp))
                DetailRow("Applicable GST", "${medicine.gstPercentage}%")
                DetailRow("Manufacturing Date", DateUtils.formatDisplayDate(medicine.manufacturingDate))
                DetailRow("Expiry Date", DateUtils.formatDisplayDate(medicine.expiryDate))
                DetailRow("Days Remaining", "${medicine.daysRemaining} days")

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onDismiss) {
                        Text("Close")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Edit Details")
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
    }
}
