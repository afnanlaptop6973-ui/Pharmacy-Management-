package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.util.DateUtils

@Entity(tableName = "general_items")
data class GeneralItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String = "Personal Care", // Personal Care, Baby Care, Cosmetics, Medical Equipment, Health Accessories, Other
    val brand: String = "",
    val supplier: String = "",
    val batchNumber: String = "GEN-001",
    val currentStock: Int = 0,
    val minimumStock: Int = 10,
    val purchasePrice: Double = 0.0,
    val sellingPrice: Double = 0.0,
    val mrp: Double = 0.0,
    val gstPercentage: Double = 18.0,
    val manufacturingDate: String = "",
    val expiryDate: String = "", // Optional
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val daysRemaining: Long?
        get() = if (expiryDate.isNotBlank()) DateUtils.calculateDaysRemaining(expiryDate) else null

    val isExpired: Boolean
        get() = (daysRemaining ?: 999L) <= 0

    val stockStatus: StockStatus
        get() = when {
            currentStock <= 0 -> StockStatus.OUT_OF_STOCK
            currentStock <= (minimumStock / 2).coerceAtLeast(1) -> StockStatus.CRITICAL
            currentStock <= minimumStock -> StockStatus.LOW_STOCK
            else -> StockStatus.OPTIMAL
        }

    val isLowStock: Boolean
        get() = currentStock <= minimumStock

    val totalStockValue: Double
        get() = currentStock * purchasePrice

    val suggestedReorderQty: Int
        get() = ((minimumStock * 2) - currentStock).coerceAtLeast(minimumStock)
}
