package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pharmacy_settings")
data class PharmacySettings(
    @PrimaryKey
    val id: Int = 1,
    val pharmacyName: String = "ZamZam MEDICAL",
    val address: String = "Zamzam Complex, Medical Center Road",
    val phone: String = "+91 98201 54321",
    val email: String = "care@zamzammedical.com",
    val gstNumber: String = "27AAACZ1020A1Z5",
    val licenseNumber: String = "20B-MH-ZM-994123",
    val defaultMinStock: Int = 15,
    val expiryAlertDays: Int = 30,
    val allowNegativeStock: Boolean = false,
    val invoicePrefix: String = "ZM-",
    val defaultGstPercentage: Double = 12.0,
    val currencySymbol: String = "₹",
    val activeRole: String = "Admin" // "Admin", "Pharmacist", "Cashier"
)
