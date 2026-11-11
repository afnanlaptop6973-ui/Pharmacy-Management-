package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "purchases")
data class Purchase(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val supplierName: String,
    val invoiceNumber: String,
    val itemsSummary: String, // e.g. "Paracetamol 500mg (100 pcs), Amoxicillin (50 pcs)"
    val totalAmount: Double,
    val purchaseDate: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
)
