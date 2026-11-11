package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TransactionType {
    PURCHASE,
    SALE,
    ADJUSTMENT,
    DISPOSAL,
    RETURN
}

@Entity(tableName = "stock_transactions")
data class StockTransaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val productId: Long,
    val productType: String, // "MEDICINE" or "GENERAL"
    val productName: String,
    val batchNumber: String,
    val transactionType: String, // PURCHASE, SALE, ADJUSTMENT, DISPOSAL, RETURN
    val quantity: Int, // e.g. +50 or -5
    val previousStock: Int,
    val newStock: Int,
    val reason: String = "",
    val date: Long = System.currentTimeMillis(),
    val userId: String = "Admin"
)
