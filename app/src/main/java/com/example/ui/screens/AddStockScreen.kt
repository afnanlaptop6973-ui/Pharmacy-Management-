package com.example.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.SectionHeader
import com.example.ui.navigation.ScreenDestination
import com.example.ui.theme.StatusWarning
import com.example.ui.viewmodel.PharmacyViewModel
import com.example.util.DateUtils

@Composable
fun AddStockScreen(
    viewModel: PharmacyViewModel,
    onNavigate: (ScreenDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    val medicines by viewModel.allMedicines.collectAsStateWithLifecycle()
    val generalItems by viewModel.allGeneralItems.collectAsStateWithLifecycle()
    val suppliers by viewModel.allSuppliers.collectAsStateWithLifecycle()

    var productTypeIndex by remember { mutableStateOf(0) } // 0 = Medicine, 1 = General Item
    val isMedicine = productTypeIndex == 0

    var selectedProductId by remember { mutableStateOf<Long?>(null) }
    var showProductDropdown by remember { mutableStateOf(false) }
    var showSupplierDropdown by remember { mutableStateOf(false) }

    var batchNumber by remember { mutableStateOf("") }
    var quantityText by remember { mutableStateOf("") }
    var purchasePriceText by remember { mutableStateOf("") }
    var supplierName by remember { mutableStateOf("") }
    var invoiceNumber by remember { mutableStateOf("") }
    var purchaseDate by remember { mutableStateOf(DateUtils.getCurrentDateString()) }
    var mfgDate by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }

    var validationError by remember { mutableStateOf<String?>(null) }

    // Selected product reference
    val selectedMedicine = if (isMedicine) medicines.find { it.id == selectedProductId } else null
    val selectedGeneralItem = if (!isMedicine) generalItems.find { it.id == selectedProductId } else null

    val currentStock = selectedMedicine?.currentStock ?: selectedGeneralItem?.currentStock ?: 0
    val productName = selectedMedicine?.name ?: selectedGeneralItem?.name ?: ""

    // Auto-populate batch / price defaults when product selected
    fun onSelectProduct(id: Long) {
        selectedProductId = id
        if (isMedicine) {
            val med = medicines.find { it.id == id }
            if (med != null) {
                batchNumber = med.batchNumber
                purchasePriceText = med.purchasePrice.toString()
                expiryDate = med.expiryDate
                mfgDate = med.manufacturingDate
            }
        } else {
            val gen = generalItems.find { it.id == id }
            if (gen != null) {
                batchNumber = gen.batchNumber
                purchasePriceText = gen.purchasePrice.toString()
                supplierName = gen.supplier
                expiryDate = gen.expiryDate
                mfgDate = gen.manufacturingDate
            }
        }
    }

    fun clearForm() {
        selectedProductId = null
        batchNumber = ""
        quantityText = ""
        purchasePriceText = ""
        supplierName = ""
        invoiceNumber = ""
        mfgDate = ""
        expiryDate = ""
        validationError = null
    }

    fun validateAndSubmit(addAnother: Boolean) {
        if (selectedProductId == null) {
            validationError = "Please select a product from inventory."
            return
        }
        val qty = quantityText.toIntOrNull() ?: 0
        if (qty <= 0) {
            validationError = "Quantity must be greater than zero."
            return
        }
        if (batchNumber.isBlank()) {
            validationError = "Batch / Lot number is required."
            return
        }
        if (isMedicine && expiryDate.isBlank()) {
            validationError = "Expiry date is required for medicines."
            return
        }

        val price = purchasePriceText.toDoubleOrNull() ?: 0.0

        viewModel.submitNewStock(
            productType = if (isMedicine) "MEDICINE" else "GENERAL",
            productId = selectedProductId!!,
            productName = productName,
            batchNumber = batchNumber,
            quantity = qty,
            purchasePrice = price,
            supplierName = supplierName,
            invoiceNumber = invoiceNumber,
            mfgDate = mfgDate,
            expiryDate = expiryDate
        )

        if (addAnother) {
            quantityText = ""
            invoiceNumber = ""
            validationError = null
        } else {
            clearForm()
            onNavigate(ScreenDestination.STOCK_MANAGEMENT)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("add_stock_screen")
    ) {
        SectionHeader(
            title = "Inward Stock Entry (Daily Purchase Log)",
            subtitle = "Add new stock batches from suppliers with automated ledger updates"
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Product Type Segmented Buttons
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            SegmentedButton(
                selected = productTypeIndex == 0,
                onClick = {
                    productTypeIndex = 0
                    clearForm()
                },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
            ) {
                Text("Medicine Stock")
            }
            SegmentedButton(
                selected = productTypeIndex == 1,
                onClick = {
                    productTypeIndex = 1
                    clearForm()
                },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
            ) {
                Text("General Item Stock")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (validationError != null) {
            Surface(
                color = MaterialTheme.colorScheme.errorContainer,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            ) {
                Text(
                    text = validationError!!,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Product Selection Dropdown Field
                Text(
                    text = if (isMedicine) "Select Medicine *" else "Select General Product *",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = if (productName.isNotBlank()) "$productName (Current Stock: $currentStock)" else "Tap to choose product...",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showProductDropdown = true }
                            .testTag("select_product_dropdown")
                    )

                    DropdownMenu(
                        expanded = showProductDropdown,
                        onDismissRequest = { showProductDropdown = false },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        if (isMedicine) {
                            medicines.forEach { med ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(med.name, fontWeight = FontWeight.SemiBold)
                                            Text("Batch: ${med.batchNumber} • Current Stock: ${med.currentStock} pcs", style = MaterialTheme.typography.bodySmall)
                                        }
                                    },
                                    onClick = {
                                        onSelectProduct(med.id)
                                        showProductDropdown = false
                                    }
                                )
                            }
                        } else {
                            generalItems.forEach { gen ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(gen.name, fontWeight = FontWeight.SemiBold)
                                            Text("${gen.category} • Current Stock: ${gen.currentStock} pcs", style = MaterialTheme.typography.bodySmall)
                                        }
                                    },
                                    onClick = {
                                        onSelectProduct(gen.id)
                                        showProductDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }

                if (selectedProductId != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Current Stock on Hand:", style = MaterialTheme.typography.bodySmall)
                            Text("$currentStock units", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Quantity and Batch
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = quantityText,
                        onValueChange = { quantityText = it.filter { c -> c.isDigit() } },
                        label = { Text("New Inward Quantity *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("inward_quantity_input")
                    )

                    OutlinedTextField(
                        value = batchNumber,
                        onValueChange = { batchNumber = it },
                        label = { Text("Batch Number *") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("inward_batch_input")
                    )
                }

                val enteredQty = quantityText.toIntOrNull() ?: 0
                if (enteredQty > 0 && selectedProductId != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "New stock after inward: ${currentStock + enteredQty} units",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Pricing and Supplier
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = purchasePriceText,
                        onValueChange = { purchasePriceText = it },
                        label = { Text("Purchase Rate / Unit (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("inward_price_input")
                    )

                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = supplierName,
                            onValueChange = { supplierName = it },
                            label = { Text("Supplier Name") },
                            trailingIcon = {
                                IconButton(onClick = { showSupplierDropdown = true }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Select supplier")
                                }
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        DropdownMenu(
                            expanded = showSupplierDropdown,
                            onDismissRequest = { showSupplierDropdown = false }
                        ) {
                            suppliers.forEach { sup ->
                                DropdownMenuItem(
                                    text = { Text(sup.name) },
                                    onClick = {
                                        supplierName = sup.name
                                        showSupplierDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Invoice Number and Purchase Date
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = invoiceNumber,
                        onValueChange = { invoiceNumber = it },
                        label = { Text("Supplier Invoice / Challan #") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = purchaseDate,
                        onValueChange = { purchaseDate = it },
                        label = { Text("Purchase Date") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Dates: Mfg and Expiry
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
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
                        label = { Text(if (isMedicine) "Expiry Date * (YYYY-MM-DD)" else "Expiry Date (Optional)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("inward_expiry_input")
                    )
                }

                // Expiry Warning check
                if (expiryDate.isNotBlank()) {
                    val days = DateUtils.calculateDaysRemaining(expiryDate)
                    if (days <= 30) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                                Text(
                                    text = if (days <= 0) "⚠️ Warning: This batch has already expired ($days days ago)!"
                                    else "⚠️ Warning: This batch expires in $days days (${expiryDate}).",
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { clearForm() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Clear")
                    }

                    OutlinedButton(
                        onClick = { validateAndSubmit(addAnother = true) },
                        modifier = Modifier.weight(1.3f).testTag("save_and_add_another_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Save & Add More", fontSize = 12.sp)
                    }

                    Button(
                        onClick = { validateAndSubmit(addAnother = false) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.weight(1.3f).testTag("save_stock_btn")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Save Stock")
                    }
                }
            }
        }
    }
}
