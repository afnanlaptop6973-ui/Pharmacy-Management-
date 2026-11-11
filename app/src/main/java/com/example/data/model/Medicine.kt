package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.util.DateUtils

enum class ExpiryStatus {
    EXPIRED,
    EXPIRING_7_DAYS,
    EXPIRING_30_DAYS,
    EXPIRING_60_DAYS,
    EXPIRING_90_DAYS,
    GOOD
}

enum class StockStatus {
    OUT_OF_STOCK,
    CRITICAL,
    LOW_STOCK,
    OPTIMAL
}

@Entity(tableName = "medicines")
data class Medicine(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val genericName: String = "",
    val brandName: String = "",
    val category: String = "General",
    val manufacturer: String = "",
    val batchNumber: String,
    val currentStock: Int = 0,
    val minimumStock: Int = 10,
    val purchasePrice: Double = 0.0,
    val sellingPrice: Double = 0.0,
    val mrp: Double = 0.0,
    val manufacturingDate: String = "",
    val expiryDate: String, // YYYY-MM-DD
    val gstPercentage: Double = 12.0,
    val rackLocation: String = "Rack A-1",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val daysRemaining: Long
        get() = DateUtils.calculateDaysRemaining(expiryDate)

    val expiryStatus: ExpiryStatus
        get() {
            val days = daysRemaining
            return when {
                days <= 0 -> ExpiryStatus.EXPIRED
                days <= 7 -> ExpiryStatus.EXPIRING_7_DAYS
                days <= 30 -> ExpiryStatus.EXPIRING_30_DAYS
                days <= 60 -> ExpiryStatus.EXPIRING_60_DAYS
                days <= 90 -> ExpiryStatus.EXPIRING_90_DAYS
                else -> ExpiryStatus.GOOD
            }
        }

    val stockStatus: StockStatus
        get() = when {
            currentStock <= 0 -> StockStatus.OUT_OF_STOCK
            currentStock <= (minimumStock / 2).coerceAtLeast(1) -> StockStatus.CRITICAL
            currentStock <= minimumStock -> StockStatus.LOW_STOCK
            else -> StockStatus.OPTIMAL
        }

    val isExpired: Boolean
        get() = daysRemaining <= 0

    val isLowStock: Boolean
        get() = currentStock <= minimumStock

    val totalStockValue: Double
        get() = currentStock * purchasePrice

    val suggestedReorderQty: Int
        get() = ((minimumStock * 2) - currentStock).coerceAtLeast(minimumStock)
}
