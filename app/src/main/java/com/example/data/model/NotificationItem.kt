package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String, // "EXPIRED", "EXPIRING_SOON", "LOW_STOCK", "OUT_OF_STOCK", "INFO"
    val title: String,
    val message: String,
    val productId: Long = 0,
    val productType: String = "",
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
