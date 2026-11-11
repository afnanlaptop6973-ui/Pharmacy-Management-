package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CartItem
import com.example.data.model.GeneralItem
import com.example.data.model.Medicine
import com.example.ui.components.EmptyStateView
import com.example.ui.components.ExpiryBadge
import com.example.ui.components.InvoiceReceiptDialog
import com.example.ui.components.PharmacySearchBar
import com.example.ui.components.StockBadge
import com.example.ui.viewmodel.PharmacyViewModel
import com.example.util.DateUtils

@Composable
fun SalesBillingScreen(
    viewModel: PharmacyViewModel,
    modifier: Modifier = Modifier
) {
    val medicines by viewModel.allMedicines.collectAsStateWithLifecycle()
    val generalItems by viewModel.allGeneralItems.collectAsStateWithLifecycle()
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val pharmacySettings by viewModel.pharmacySettings.collectAsStateWithLifecycle()

    val customerName by viewModel.customerName.collectAsStateWithLifecycle()
    val customerPhone by viewModel.customerPhone.collectAsStateWithLifecycle()
    val customerAddress by viewModel.customerAddress.collectAsStateWithLifecycle()
    val paymentMethod by viewModel.paymentMethod.collectAsStateWithLifecycle()
    val billDiscount by viewModel.billDiscountPercent.collectAsStateWithLifecycle()

    val showInvoiceDialog by viewModel.showInvoiceDialog.collectAsStateWithLifecycle()
    val lastCompletedSale by viewModel.lastCompletedSale.collectAsStateWithLifecycle()

    var billingSearchQuery by remember { mutableStateOf("") }
    var itemTypeTab by remember { mutableStateOf(0) } // 0 = Medicines, 1 = General Items

    // Totals
    val subtotal = cartItems.sumOf { it.subtotal }
    val itemDiscounts = cartItems.sumOf { it.discountAmount }
    val billDiscountAmount = (subtotal - itemDiscounts) * (billDiscount / 100.0)
    val totalDiscount = itemDiscounts + billDiscountAmount
    val totalTax = cartItems.sumOf { it.taxAmount }
    val grandTotal = (subtotal - totalDiscount + totalTax).coerceAtLeast(0.0)

    val paymentMethods = listOf("Cash", "UPI", "Card", "Other")

    // Filter available products
    val availableMedicines = remember(medicines, billingSearchQuery) {
        medicines.filter {
            it.name.contains(billingSearchQuery, ignoreCase = true) ||
                    it.genericName.contains(billingSearchQuery, ignoreCase = true) ||
                    it.batchNumber.contains(billingSearchQuery, ignoreCase = true)
        }
    }

    val availableGeneralItems = remember(generalItems, billingSearchQuery) {
        generalItems.filter {
            it.name.contains(billingSearchQuery, ignoreCase = true) ||
                    it.brand.contains(billingSearchQuery, ignoreCase = true) ||
                    it.batchNumber.contains(billingSearchQuery, ignoreCase = true)
        }
    }

    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp)
            .testTag("sales_billing_screen"),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // LEFT COLUMN: Product Catalog Search & Add to Cart
        Card(
            modifier = Modifier
                .weight(1.1f)
                .fillMaxHeight(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Product Catalog (POS)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    SegmentedButton(
                        selected = itemTypeTab == 0,
                        onClick = { itemTypeTab = 0 },
                        shape = SegmentedButtonDefaults.itemShape(0, 2)
                    ) {
                        Text("Medicines", fontSize = 12.sp)
                    }
                    SegmentedButton(
                        selected = itemTypeTab == 1,
                        onClick = { itemTypeTab = 1 },
                        shape = SegmentedButtonDefaults.itemShape(1, 2)
                    ) {
                        Text("General Items", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                PharmacySearchBar(
                    query = billingSearchQuery,
                    onQueryChange = { billingSearchQuery = it },
                    placeholderText = if (itemTypeTab == 0) "Search medicine, generic, batch..." else "Search general items...",
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "billing_search_input"
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Products List
                if (itemTypeTab == 0) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(availableMedicines, key = { it.id }) { med ->
                            BillingProductItem(
                                name = med.name,
                                subtitle = "Batch: ${med.batchNumber} • Stock: ${med.currentStock}",
                                price = med.sellingPrice,
                                isExpired = med.isExpired,
                                stock = med.currentStock,
                                onAdd = { viewModel.addMedicineToCart(med) }
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(availableGeneralItems, key = { it.id }) { gen ->
                            BillingProductItem(
                                name = gen.name,
                                subtitle = "Brand: ${gen.brand} • Stock: ${gen.currentStock}",
                                price = gen.sellingPrice,
                                isExpired = gen.isExpired,
                                stock = gen.currentStock,
                                onAdd = { viewModel.addGeneralItemToCart(gen) }
                            )
                        }
                    }
                }
            }
        }

        // CENTER COLUMN: Shopping Cart / Line Items
        Card(
            modifier = Modifier
                .weight(1.3f)
                .fillMaxHeight(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text(
                            text = "Billing Cart (${cartItems.sumOf { it.quantity }} items)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (cartItems.isNotEmpty()) {
                        OutlinedButton(
                            onClick = { viewModel.clearCart() },
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("clear_cart_btn")
                        ) {
                            Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Clear", fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(8.dp))

                if (cartItems.isEmpty()) {
                    EmptyStateView(
                        icon = Icons.Default.ShoppingCart,
                        title = "Cart is Empty",
                        description = "Select products from the catalog on the left to add items to this invoice."
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(cartItems, key = { "${it.productType}_${it.productId}" }) { item ->
                            CartLineItemRow(
                                item = item,
                                onQtyChange = { delta ->
                                    viewModel.updateCartItemQuantity(item.productId, item.productType, delta)
                                },
                                onDiscountChange = { disc ->
                                    viewModel.setCartItemDiscount(item.productId, item.productType, disc)
                                },
                                onRemove = {
                                    viewModel.removeCartItem(item.productId, item.productType)
                                }
                            )
                        }
                    }
                }
            }
        }

        // RIGHT COLUMN: Customer Details, Payment & Summary
        Card(
            modifier = Modifier
                .weight(1.1f)
                .fillMaxHeight(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(12.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Customer & Payment",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Customer Inputs
                OutlinedTextField(
                    value = customerName,
                    onValueChange = { viewModel.setCustomerDetails(it, customerPhone, customerAddress) },
                    label = { Text("Customer Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("billing_customer_name")
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = customerPhone,
                    onValueChange = { viewModel.setCustomerDetails(customerName, it, customerAddress) },
                    label = { Text("Phone Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("billing_customer_phone")
                )

                Spacer(modifier = Modifier.height(10.dp))
                Text("Payment Method", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))

                // Payment Method Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    paymentMethods.forEach { method ->
                        val isSelected = paymentMethod == method
                        val mColor = when(method) {
                            "Cash" -> Color(0xFF059669)
                            "UPI" -> Color(0xFF2563EB)
                            "Card" -> Color(0xFF7C3AED)
                            else -> Color(0xFFD97706)
                        }
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.setPaymentMethod(method) }
                                .testTag("payment_method_$method"),
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(mColor)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = method,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    textAlign = TextAlign.Center,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(10.dp))

                // Invoice Summary Breakdown
                Text("Calculation Summary", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))

                BillSummaryRow("Subtotal", DateUtils.formatCurrency(subtotal))
                if (itemDiscounts > 0) {
                    BillSummaryRow("Item Discounts", "- " + DateUtils.formatCurrency(itemDiscounts), color = MaterialTheme.colorScheme.error)
                }

                // Bill Discount Input
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Overall Bill Discount %", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    OutlinedTextField(
                        value = if (billDiscount > 0) billDiscount.toString() else "",
                        onValueChange = {
                            val parsed = it.toDoubleOrNull() ?: 0.0
                            viewModel.setBillDiscount(parsed)
                        },
                        placeholder = { Text("0%") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.width(80.dp).height(44.dp)
                    )
                }

                BillSummaryRow("Estimated GST Tax", DateUtils.formatCurrency(totalTax))

                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Net Grand Total:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        text = DateUtils.formatCurrency(grandTotal),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Complete Sale Button
                Button(
                    onClick = { viewModel.completeCurrentSale() },
                    enabled = cartItems.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("complete_sale_button")
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Complete Sale (F10 / Enter)", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // Invoice Preview & Print Modal
    if (showInvoiceDialog && lastCompletedSale != null) {
        InvoiceReceiptDialog(
            sale = lastCompletedSale!!,
            settings = pharmacySettings,
            onDismiss = {
                viewModel.showInvoiceDialog.value = false
            }
        )
    }
}

@Composable
private fun BillingProductItem(
    name: String,
    subtitle: String,
    price: Double,
    isExpired: Boolean,
    stock: Int,
    onAdd: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isExpired && stock > 0) { onAdd() }
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(name, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(DateUtils.formatCurrency(price), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodyMedium)
                if (isExpired) {
                    Text("EXPIRED", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                } else if (stock <= 0) {
                    Text("OUT OF STOCK", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                } else {
                    Text("+ Add", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun CartLineItemRow(
    item: CartItem,
    onQtyChange: (Int) -> Unit,
    onDiscountChange: (Double) -> Unit,
    onRemove: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(item.productName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    Text("Batch: ${item.batchNumber} • Max Stock: ${item.availableStock}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                IconButton(onClick = onRemove, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Quantity +/- Controls
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = { onQtyChange(-1) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Minus", modifier = Modifier.size(16.dp))
                    }
                    Text(
                        text = "${item.quantity}",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    IconButton(
                        onClick = { onQtyChange(1) },
                        enabled = item.quantity < item.availableStock,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Plus", modifier = Modifier.size(16.dp))
                    }
                }

                Text(
                    text = "${item.quantity} x ${DateUtils.formatCurrency(item.unitPrice)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = DateUtils.formatCurrency(item.itemTotal),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun BillSummaryRow(label: String, value: String, color: Color = MaterialTheme.colorScheme.onSurface) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = color)
    }
}
