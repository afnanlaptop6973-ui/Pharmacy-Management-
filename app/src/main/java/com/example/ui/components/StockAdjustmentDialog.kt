package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.window.Dialog

@Composable
fun StockAdjustmentDialog(
    productName: String,
    currentStock: Int,
    onConfirm: (quantityChange: Int, reason: String) -> Unit,
    onDismiss: () -> Unit
) {
    var deltaQtyText by remember { mutableStateOf("") }
    var isNegative by remember { mutableStateOf(false) }
    var reason by remember { mutableStateOf("Physical Inventory Audit Correction") }

    val presetReasons = listOf(
        "Physical Inventory Audit Correction",
        "Damaged / Broken Packaging",
        "Disposal of Expired Stock",
        "Quarantine Item",
        "Supplier Replacement"
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth(0.95f)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Adjust Stock Level",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = productName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Current Available Stock: $currentStock units",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Adjustment Direction (Increase or Decrease)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { isNegative = false },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("adjust_stock_increase"),
                        shape = RoundedCornerShape(8.dp),
                        colors = if (!isNegative) androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ) else androidx.compose.material3.ButtonDefaults.outlinedButtonColors()
                    ) {
                        Text("+ Increase Stock")
                    }
                    OutlinedButton(
                        onClick = { isNegative = true },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("adjust_stock_decrease"),
                        shape = RoundedCornerShape(8.dp),
                        colors = if (isNegative) androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ) else androidx.compose.material3.ButtonDefaults.outlinedButtonColors()
                    ) {
                        Text("- Decrease Stock")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = deltaQtyText,
                    onValueChange = { deltaQtyText = it.filter { char -> char.isDigit() } },
                    label = { Text(if (isNegative) "Quantity to Deduct" else "Quantity to Add") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("adjustment_qty_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Reason for adjustment:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))

                ChipFilterRow(
                    options = presetReasons,
                    selectedOption = reason,
                    onOptionSelected = { reason = it }
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Custom Reason / Note") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                val parsedQty = deltaQtyText.toIntOrNull() ?: 0
                val calculatedNewStock = if (isNegative) (currentStock - parsedQty).coerceAtLeast(0) else (currentStock + parsedQty)

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Resulting Stock: $calculatedNewStock units",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                    Button(
                        onClick = {
                            val change = if (isNegative) -parsedQty else parsedQty
                            if (parsedQty > 0) {
                                onConfirm(change, reason)
                            }
                        },
                        enabled = parsedQty > 0,
                        modifier = Modifier.testTag("confirm_adjustment_button")
                    ) {
                        Text("Apply Adjustment")
                    }
                }
            }
        }
    }
}
