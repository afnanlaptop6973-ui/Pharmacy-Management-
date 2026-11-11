package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.json.JSONArray
import org.json.JSONObject

data class CartItem(
    val productId: Long,
    val productType: String, // "MEDICINE" or "GENERAL"
    val productName: String,
    val batchNumber: String,
    val availableStock: Int,
    var quantity: Int,
    val unitPrice: Double,
    var discountPercent: Double = 0.0,
    val gstPercent: Double = 12.0
) {
    val subtotal: Double
        get() = quantity * unitPrice

    val discountAmount: Double
        get() = subtotal * (discountPercent / 100.0)

    val taxableAmount: Double
        get() = subtotal - discountAmount

    val taxAmount: Double
        get() = taxableAmount * (gstPercent / 100.0)

    val itemTotal: Double
        get() = taxableAmount + taxAmount

    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("productId", productId)
            put("productType", productType)
            put("productName", productName)
            put("batchNumber", batchNumber)
            put("availableStock", availableStock)
            put("quantity", quantity)
            put("unitPrice", unitPrice)
            put("discountPercent", discountPercent)
            put("gstPercent", gstPercent)
        }
    }

    companion object {
        fun fromJson(json: JSONObject): CartItem {
            return CartItem(
                productId = json.optLong("productId"),
                productType = json.optString("productType", "MEDICINE"),
                productName = json.optString("productName", ""),
                batchNumber = json.optString("batchNumber", ""),
                availableStock = json.optInt("availableStock", 0),
                quantity = json.optInt("quantity", 1),
                unitPrice = json.optDouble("unitPrice", 0.0),
                discountPercent = json.optDouble("discountPercent", 0.0),
                gstPercent = json.optDouble("gstPercent", 12.0)
            )
        }

        fun listToJson(items: List<CartItem>): String {
            val arr = JSONArray()
            items.forEach { arr.put(it.toJson()) }
            return arr.toString()
        }

        fun listFromJson(jsonStr: String): List<CartItem> {
            val list = mutableListOf<CartItem>()
            if (jsonStr.isBlank()) return list
            try {
                val arr = JSONArray(jsonStr)
                for (i in 0 until arr.length()) {
                    list.add(fromJson(arr.getJSONObject(i)))
                }
            } catch (_: Exception) {}
            return list
        }
    }
}

@Entity(tableName = "sales")
data class Sale(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val invoiceNumber: String,
    val customerName: String = "Walk-in Customer",
    val customerPhone: String = "",
    val customerAddress: String = "",
    val itemsJson: String,
    val subtotal: Double,
    val discount: Double,
    val tax: Double,
    val grandTotal: Double,
    val paymentMethod: String = "Cash", // Cash, UPI, Card, Other
    val createdAt: Long = System.currentTimeMillis(),
    val userId: String = "Cashier"
) {
    val items: List<CartItem>
        get() = CartItem.listFromJson(itemsJson)

    val totalItemCount: Int
        get() = items.sumOf { it.quantity }
}
