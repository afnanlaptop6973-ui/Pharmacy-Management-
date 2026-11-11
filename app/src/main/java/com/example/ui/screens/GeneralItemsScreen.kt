package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Visibility
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
import com.example.data.model.GeneralItem
import com.example.ui.components.ChipFilterRow
import com.example.ui.components.EmptyStateView
import com.example.ui.components.GeneralItemCategoryBadge
import com.example.ui.components.PharmacySearchBar
import com.example.ui.components.StockAdjustmentDialog
import com.example.ui.components.StockBadge
import com.example.ui.theme.MedicalCrossGreen
import com.example.ui.theme.getGeneralItemCategoryVisual
import com.example.ui.viewmodel.PharmacyViewModel
import com.example.util.DateUtils

@Composable
fun GeneralItemsScreen(
    viewModel: PharmacyViewModel,
    modifier: Modifier = Modifier
) {
    val items by viewModel.allGeneralItems.collectAsStateWithLifecycle()
    val searchQuery by viewModel.generalSearchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.generalCategoryFilter.collectAsStateWithLifecycle()
    val selectedStockFilter by viewModel.generalStockFilter.collectAsStateWithLifecycle()

    val showAddEditDialog by viewModel.showAddEditGeneralDialog.collectAsStateWithLifecycle()
    val editingItem by viewModel.editingGeneralItem.collectAsStateWithLifecycle()

    var viewingItem by remember { mutableStateOf<GeneralItem?>(null) }
    var itemToDelete by remember { mutableStateOf<GeneralItem?>(null) }
    var adjustingItem by remember { mutableStateOf<GeneralItem?>(null) }

    val presetCategories = listOf(
        "All",
        "Personal Care",
        "Baby Care",
        "Medical Equipment",
        "Health Accessories",
        "Cosmetics",
        "Other"
    )

    val filteredItems = remember(items, searchQuery, selectedCategory, selectedStockFilter) {
        items.filter { itm ->
            val matchesSearch = searchQuery.isBlank() ||
                    itm.name.contains(searchQuery, ignoreCase = true) ||
                    itm.brand.contains(searchQuery, ignoreCase = true) ||
                    itm.supplier.contains(searchQuery, ignoreCase = true) ||
                    itm.batchNumber.contains(searchQuery, ignoreCase = true)

            val matchesCategory = selectedCategory == "All" || itm.category == selectedCategory

            val matchesStock = when (selectedStockFilter) {
                "In Stock" -> itm.currentStock > itm.minimumStock
                "Low Stock" -> itm.isLowStock && itm.currentStock > 0
                "Out of Stock" -> itm.currentStock == 0
                else -> true
            }

            matchesSearch && matchesCategory && matchesStock
        }
    }

    Box(modifier = modifier.fillMaxSize().testTag("general_items_screen")) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header with Notice
            Surface(
                color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Category, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(20.dp))
                    Text(
                        text = "General FMCG & Healthcare Items (Non-Medicine Section)",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }

            // Top Bar: Search + Add Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PharmacySearchBar(
                    query = searchQuery,
                    onQueryChange = { viewModel.generalSearchQuery.value = it },
                    placeholderText = "Search item name, brand, or supplier...",
                    modifier = Modifier.weight(1f),
                    testTag = "general_search_input"
                )

                Button(
                    onClick = {
                        viewModel.editingGeneralItem.value = null
                        viewModel.showAddEditGeneralDialog.value = true
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                    modifier = Modifier.testTag("add_general_item_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Item")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Filters
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Category: ", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.width(90.dp))
                    ChipFilterRow(
                        options = presetCategories,
                        selectedOption = selectedCategory,
                        onOptionSelected = { viewModel.generalCategoryFilter.value = it }
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Stock Status: ", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.width(90.dp))
                    ChipFilterRow(
                        options = listOf("All", "In Stock", "Low Stock", "Out of Stock"),
                        selectedOption = selectedStockFilter,
                        onOptionSelected = { viewModel.generalStockFilter.value = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Showing ${filteredItems.size} of ${items.size} general items",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (searchQuery.isNotBlank() || selectedCategory != "All" || selectedStockFilter != "All") {
                    TextButton(onClick = {
                        viewModel.generalSearchQuery.value = ""
                        viewModel.generalCategoryFilter.value = "All"
                        viewModel.generalStockFilter.value = "All"
                    }) {
                        Text("Reset Filters", fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Items List
            if (filteredItems.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.Category,
                    title = "No General Items Found",
                    description = "Add personal care, baby care, or medical equipment products to your inventory.",
                    actionButton = {
                        Button(onClick = {
                            viewModel.editingGeneralItem.value = null
                            viewModel.showAddEditGeneralDialog.value = true
                        }) {
                            Text("Add General Item")
                        }
                    }
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredItems, key = { it.id }) { itm ->
                        GeneralCardItem(
                            item = itm,
                            onView = { viewingItem = itm },
                            onEdit = {
                                viewModel.editingGeneralItem.value = itm
                                viewModel.showAddEditGeneralDialog.value = true
                            },
                            onAddStock = { adjustingItem = itm },
                            onDelete = { itemToDelete = itm },
                            onAddToCart = { viewModel.addGeneralItemToCart(itm) }
                        )
                    }
                }
            }
        }
    }

    // Add / Edit General Item Dialog
    if (showAddEditDialog) {
        AddEditGeneralDialog(
            initialItem = editingItem,
            onDismiss = {
                viewModel.showAddEditGeneralDialog.value = false
                viewModel.editingGeneralItem.value = null
            },
            onSave = { savedItem ->
                viewModel.saveGeneralItem(savedItem)
            }
        )
    }

    // View Details Modal
    viewingItem?.let { itm ->
        GeneralDetailDialog(
            item = itm,
            onDismiss = { viewingItem = null },
            onEdit = {
                viewingItem = null
                viewModel.editingGeneralItem.value = itm
                viewModel.showAddEditGeneralDialog.value = true
            }
        )
    }

    // Adjust Stock Dialog
    adjustingItem?.let { itm ->
        StockAdjustmentDialog(
            productName = itm.name,
            currentStock = itm.currentStock,
            onConfirm = { delta, reason ->
                viewModel.adjustStock("GENERAL", itm.id, delta, reason)
                adjustingItem = null
            },
            onDismiss = { adjustingItem = null }
        )
    }

    // Delete Confirmation
    itemToDelete?.let { itm ->
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text("Delete General Item") },
            text = { Text("Are you sure you want to delete '${itm.name}'? This cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteGeneralItem(itm)
                        itemToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { itemToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun GeneralCardItem(
    item: GeneralItem,
    onView: () -> Unit,
    onEdit: () -> Unit,
    onAddStock: () -> Unit,
    onDelete: () -> Unit,
    onAddToCart: () -> Unit
) {
    val categoryVisual = getGeneralItemCategoryVisual(item.category)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onView() }
            .testTag("general_item_card_${item.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
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
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(categoryVisual.containerColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = categoryVisual.icon,
                            contentDescription = item.category,
                            tint = categoryVisual.iconColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Brand: ${item.brand}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        GeneralItemCategoryBadge(category = item.category)
                    }
                }

                StockBadge(status = item.stockStatus, currentStock = item.currentStock, minStock = item.minimumStock)
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Supplier: ${item.supplier}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Batch: ${item.batchNumber}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                    if (item.expiryDate.isNotBlank()) {
                        Text("Exp: ${DateUtils.formatDisplayDate(item.expiryDate)}", style = MaterialTheme.typography.labelSmall)
                    }
                }
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                    Text("Price: ${DateUtils.formatCurrency(item.sellingPrice)}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = MedicalCrossGreen)
                    Text("MRP: ${DateUtils.formatCurrency(item.mrp)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("GST: ${item.gstPercentage}%", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(8.dp))

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
                        Icon(Icons.Default.Edit, contentDescription = "Edit Item", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = onAddStock, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.AddShoppingCart, contentDescription = "Adjust Stock", tint = MaterialTheme.colorScheme.secondary)
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Item", tint = MaterialTheme.colorScheme.error)
                    }
                }

                Button(
                    onClick = onAddToCart,
                    enabled = item.currentStock > 0 && !item.isExpired,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MedicalCrossGreen,
                        contentColor = androidx.compose.ui.graphics.Color.White
                    ),
                    modifier = Modifier.testTag("add_general_to_bill_${item.id}")
                ) {
                    Text(
                        text = if (item.currentStock == 0) "Out of Stock" else "+ Bill",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun AddEditGeneralDialog(
    initialItem: GeneralItem?,
    onDismiss: () -> Unit,
    onSave: (GeneralItem) -> Unit
) {
    val isEdit = initialItem != null
    var name by remember { mutableStateOf(initialItem?.name ?: "") }
    var category by remember { mutableStateOf(initialItem?.category ?: "Personal Care") }
    var brand by remember { mutableStateOf(initialItem?.brand ?: "") }
    var supplier by remember { mutableStateOf(initialItem?.supplier ?: "") }
    var batchNumber by remember { mutableStateOf(initialItem?.batchNumber ?: "GEN-${System.currentTimeMillis() % 10000}") }
    var currentStockText by remember { mutableStateOf(initialItem?.currentStock?.toString() ?: "20") }
    var minStockText by remember { mutableStateOf(initialItem?.minimumStock?.toString() ?: "10") }
    var purchasePriceText by remember { mutableStateOf(initialItem?.purchasePrice?.toString() ?: "100.0") }
    var sellingPriceText by remember { mutableStateOf(initialItem?.sellingPrice?.toString() ?: "140.0") }
    var mrpText by remember { mutableStateOf(initialItem?.mrp?.toString() ?: "150.0") }
    var gstText by remember { mutableStateOf(initialItem?.gstPercentage?.toString() ?: "18.0") }
    var mfgDate by remember { mutableStateOf(initialItem?.manufacturingDate ?: "") }
    var expiryDate by remember { mutableStateOf(initialItem?.expiryDate ?: "") }

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
                    text = if (isEdit) "Edit General Product" else "Add General Product",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Personal care, baby care, or medical devices",
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

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Product / Item Name *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("general_input_name")
                )
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
                        value = brand,
                        onValueChange = { brand = it },
                        label = { Text("Brand") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = supplier,
                        onValueChange = { supplier = it },
                        label = { Text("Supplier / Distributor") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = batchNumber,
                        onValueChange = { batchNumber = it },
                        label = { Text("Batch / Lot Number") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = currentStockText,
                        onValueChange = { currentStockText = it.filter { c -> c.isDigit() } },
                        label = { Text("Current Stock *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("general_input_stock")
                    )
                    OutlinedTextField(
                        value = minStockText,
                        onValueChange = { minStockText = it.filter { c -> c.isDigit() } },
                        label = { Text("Min Stock Alert") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

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
                        modifier = Modifier.weight(1f)
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

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = mfgDate,
                        onValueChange = { mfgDate = it },
                        label = { Text("Mfg Date (Optional)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = expiryDate,
                        onValueChange = { expiryDate = it },
                        label = { Text("Expiry Date (Optional)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

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
                                errorMessage = "Item name is required."
                                return@Button
                            }
                            val stock = currentStockText.toIntOrNull() ?: 0
                            val minStock = minStockText.toIntOrNull() ?: 10
                            val purchase = purchasePriceText.toDoubleOrNull() ?: 0.0
                            val selling = sellingPriceText.toDoubleOrNull() ?: 0.0
                            val mrp = mrpText.toDoubleOrNull() ?: selling
                            val gst = gstText.toDoubleOrNull() ?: 18.0

                            val updated = (initialItem ?: GeneralItem(name = name)).copy(
                                name = name.trim(),
                                category = category.trim(),
                                brand = brand.trim(),
                                supplier = supplier.trim(),
                                batchNumber = batchNumber.trim(),
                                currentStock = stock,
                                minimumStock = minStock,
                                purchasePrice = purchase,
                                sellingPrice = selling,
                                mrp = mrp,
                                gstPercentage = gst,
                                manufacturingDate = mfgDate.trim(),
                                expiryDate = expiryDate.trim(),
                                updatedAt = System.currentTimeMillis()
                            )
                            onSave(updated)
                        },
                        modifier = Modifier.testTag("submit_general_item_button")
                    ) {
                        Text(if (isEdit) "Update Item" else "Save Item")
                    }
                }
            }
        }
    }
}

@Composable
fun GeneralDetailDialog(
    item: GeneralItem,
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
                Text(
                    text = "General Item Details",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Text(
                    text = "${item.category} • Brand: ${item.brand}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))
                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(10.dp))

                DetailRow("Supplier", item.supplier)
                DetailRow("Batch Number", item.batchNumber)
                DetailRow("Current Available Stock", "${item.currentStock} units")
                DetailRow("Minimum Stock Threshold", "${item.minimumStock} units")
                DetailRow("Suggested Reorder Qty", "${item.suggestedReorderQty} units")
                DetailRow("Purchase Price", DateUtils.formatCurrency(item.purchasePrice))
                DetailRow("Selling Price", DateUtils.formatCurrency(item.sellingPrice))
                DetailRow("MRP", DateUtils.formatCurrency(item.mrp))
                DetailRow("GST Tax", "${item.gstPercentage}%")
                if (item.expiryDate.isNotBlank()) {
                    DetailRow("Expiry Date", DateUtils.formatDisplayDate(item.expiryDate))
                }

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
