package com.example.data.repository

import com.example.data.db.PharmacyDao
import com.example.data.db.SampleData
import com.example.data.model.CartItem
import com.example.data.model.GeneralItem
import com.example.data.model.Medicine
import com.example.data.model.NotificationItem
import com.example.data.model.PharmacySettings
import com.example.data.model.Purchase
import com.example.data.model.Sale
import com.example.data.model.StockTransaction
import com.example.data.model.Supplier
import com.example.util.DateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlin.random.Random

class PharmacyRepository(private val dao: PharmacyDao) {

    val allMedicines: Flow<List<Medicine>> = dao.getAllMedicines()
    val allGeneralItems: Flow<List<GeneralItem>> = dao.getAllGeneralItems()
    val allStockTransactions: Flow<List<StockTransaction>> = dao.getAllStockTransactions()
    val allSales: Flow<List<Sale>> = dao.getAllSales()
    val allPurchases: Flow<List<Purchase>> = dao.getAllPurchases()
    val allSuppliers: Flow<List<Supplier>> = dao.getAllSuppliers()
    val allNotifications: Flow<List<NotificationItem>> = dao.getAllNotifications()
    val unreadNotificationsCount: Flow<Int> = dao.getUnreadNotificationsCount()
    val settings: Flow<PharmacySettings?> = dao.getSettings()

    suspend fun insertMedicine(medicine: Medicine, userId: String = "Pharmacist"): Long {
        val id = dao.insertMedicine(medicine)
        if (medicine.currentStock > 0) {
            dao.insertStockTransaction(
                StockTransaction(
                    productId = id,
                    productType = "MEDICINE",
                    productName = medicine.name,
                    batchNumber = medicine.batchNumber,
                    transactionType = "PURCHASE",
                    quantity = medicine.currentStock,
                    previousStock = 0,
                    newStock = medicine.currentStock,
                    reason = "Initial stock entry",
                    userId = userId
                )
            )
        }
        return id
    }

    suspend fun updateMedicine(medicine: Medicine) {
        dao.updateMedicine(medicine)
    }

    suspend fun deleteMedicine(medicine: Medicine) {
        dao.deleteMedicine(medicine)
    }

    suspend fun insertGeneralItem(item: GeneralItem, userId: String = "Pharmacist"): Long {
        val id = dao.insertGeneralItem(item)
        if (item.currentStock > 0) {
            dao.insertStockTransaction(
                StockTransaction(
                    productId = id,
                    productType = "GENERAL",
                    productName = item.name,
                    batchNumber = item.batchNumber,
                    transactionType = "PURCHASE",
                    quantity = item.currentStock,
                    previousStock = 0,
                    newStock = item.currentStock,
                    reason = "Initial general item stock",
                    userId = userId
                )
            )
        }
        return id
    }

    suspend fun updateGeneralItem(item: GeneralItem) {
        dao.updateGeneralItem(item)
    }

    suspend fun deleteGeneralItem(item: GeneralItem) {
        dao.deleteGeneralItem(item)
    }

    suspend fun addNewStock(
        productType: String,
        productId: Long,
        productName: String,
        batchNumber: String,
        quantity: Int,
        purchasePrice: Double,
        supplierName: String,
        invoiceNumber: String,
        mfgDate: String,
        expiryDate: String,
        userId: String
    ): Boolean {
        if (quantity <= 0) return false

        var prevStock = 0
        var newStock = 0

        if (productType == "MEDICINE") {
            val existing = dao.getMedicineById(productId) ?: return false
            prevStock = existing.currentStock
            newStock = prevStock + quantity
            val updated = existing.copy(
                currentStock = newStock,
                batchNumber = if (batchNumber.isNotBlank()) batchNumber else existing.batchNumber,
                purchasePrice = if (purchasePrice > 0) purchasePrice else existing.purchasePrice,
                expiryDate = if (expiryDate.isNotBlank()) expiryDate else existing.expiryDate,
                manufacturingDate = if (mfgDate.isNotBlank()) mfgDate else existing.manufacturingDate,
                updatedAt = System.currentTimeMillis()
            )
            dao.updateMedicine(updated)
        } else {
            val existing = dao.getGeneralItemById(productId) ?: return false
            prevStock = existing.currentStock
            newStock = prevStock + quantity
            val updated = existing.copy(
                currentStock = newStock,
                batchNumber = if (batchNumber.isNotBlank()) batchNumber else existing.batchNumber,
                purchasePrice = if (purchasePrice > 0) purchasePrice else existing.purchasePrice,
                expiryDate = if (expiryDate.isNotBlank()) expiryDate else existing.expiryDate,
                manufacturingDate = if (mfgDate.isNotBlank()) mfgDate else existing.manufacturingDate,
                updatedAt = System.currentTimeMillis()
            )
            dao.updateGeneralItem(updated)
        }

        // Record stock transaction
        dao.insertStockTransaction(
            StockTransaction(
                productId = productId,
                productType = productType,
                productName = productName,
                batchNumber = batchNumber,
                transactionType = "PURCHASE",
                quantity = quantity,
                previousStock = prevStock,
                newStock = newStock,
                reason = "Inward Stock - Supplier: $supplierName, Inv: $invoiceNumber",
                userId = userId
            )
        )

        // Record purchase entry
        dao.insertPurchase(
            Purchase(
                supplierName = supplierName.ifBlank { "Direct Vendor" },
                invoiceNumber = invoiceNumber.ifBlank { "PO-${System.currentTimeMillis() % 100000}" },
                itemsSummary = "$productName ($quantity pcs, Batch $batchNumber)",
                totalAmount = quantity * purchasePrice
            )
        )

        // Check if expiry is near and add alert if needed
        if (expiryDate.isNotBlank()) {
            val days = DateUtils.calculateDaysRemaining(expiryDate)
            if (days <= 30) {
                dao.insertNotification(
                    NotificationItem(
                        type = if (days <= 0) "EXPIRED" else "EXPIRING_SOON",
                        title = if (days <= 0) "Stock Added Expired" else "Stock Expiring Soon",
                        message = "$productName (Batch $batchNumber) expires in $days days ($expiryDate)",
                        productId = productId,
                        productType = productType
                    )
                )
            }
        }

        return true
    }

    suspend fun adjustStock(
        productType: String,
        productId: Long,
        quantityChange: Int,
        reason: String,
        userId: String
    ): Boolean {
        var prevStock = 0
        var newStock = 0
        var productName = ""
        var batchNumber = ""

        if (productType == "MEDICINE") {
            val existing = dao.getMedicineById(productId) ?: return false
            prevStock = existing.currentStock
            newStock = (prevStock + quantityChange).coerceAtLeast(0)
            productName = existing.name
            batchNumber = existing.batchNumber
            dao.updateMedicineStock(productId, newStock)
        } else {
            val existing = dao.getGeneralItemById(productId) ?: return false
            prevStock = existing.currentStock
            newStock = (prevStock + quantityChange).coerceAtLeast(0)
            productName = existing.name
            batchNumber = existing.batchNumber
            dao.updateGeneralItemStock(productId, newStock)
        }

        dao.insertStockTransaction(
            StockTransaction(
                productId = productId,
                productType = productType,
                productName = productName,
                batchNumber = batchNumber,
                transactionType = if (quantityChange < 0 && reason.contains("dispos", ignoreCase = true)) "DISPOSAL" else "ADJUSTMENT",
                quantity = quantityChange,
                previousStock = prevStock,
                newStock = newStock,
                reason = reason,
                userId = userId
            )
        )

        return true
    }

    suspend fun markDisposed(medicine: Medicine, userId: String = "Pharmacist") {
        val qty = medicine.currentStock
        if (qty > 0) {
            dao.updateMedicineStock(medicine.id, 0)
            dao.insertStockTransaction(
                StockTransaction(
                    productId = medicine.id,
                    productType = "MEDICINE",
                    productName = medicine.name,
                    batchNumber = medicine.batchNumber,
                    transactionType = "DISPOSAL",
                    quantity = -qty,
                    previousStock = qty,
                    newStock = 0,
                    reason = "Disposed expired medicine (Batch: ${medicine.batchNumber})",
                    userId = userId
                )
            )
        }
    }

    suspend fun completeSale(
        customerName: String,
        customerPhone: String,
        customerAddress: String,
        cartItems: List<CartItem>,
        subtotal: Double,
        discount: Double,
        tax: Double,
        grandTotal: Double,
        paymentMethod: String,
        userId: String
    ): Sale {
        val currentSettings = dao.getSettings().firstOrNull() ?: SampleData.defaultSettings
        val prefix = currentSettings.invoicePrefix
        val randomNum = 1000 + Random.nextInt(9000)
        val invoiceNo = "$prefix$randomNum"

        val sale = Sale(
            invoiceNumber = invoiceNo,
            customerName = customerName.ifBlank { "Walk-in Customer" },
            customerPhone = customerPhone,
            customerAddress = customerAddress,
            itemsJson = CartItem.listToJson(cartItems),
            subtotal = subtotal,
            discount = discount,
            tax = tax,
            grandTotal = grandTotal,
            paymentMethod = paymentMethod,
            userId = userId
        )

        dao.insertSale(sale)

        // Deduct stock for each cart item
        for (item in cartItems) {
            if (item.productType == "MEDICINE") {
                val med = dao.getMedicineById(item.productId)
                if (med != null) {
                    val prev = med.currentStock
                    val next = (prev - item.quantity).coerceAtLeast(0)
                    dao.updateMedicineStock(item.productId, next)
                    dao.insertStockTransaction(
                        StockTransaction(
                            productId = item.productId,
                            productType = "MEDICINE",
                            productName = item.productName,
                            batchNumber = item.batchNumber,
                            transactionType = "SALE",
                            quantity = -item.quantity,
                            previousStock = prev,
                            newStock = next,
                            reason = "Sale Invoice #$invoiceNo",
                            userId = userId
                        )
                    )

                    // Low stock notification
                    if (next <= med.minimumStock) {
                        dao.insertNotification(
                            NotificationItem(
                                type = if (next == 0) "OUT_OF_STOCK" else "LOW_STOCK",
                                title = if (next == 0) "Out of Stock" else "Low Stock Alert",
                                message = "${med.name} stock is down to $next units (min: ${med.minimumStock})",
                                productId = med.id,
                                productType = "MEDICINE"
                            )
                        )
                    }
                }
            } else {
                val gen = dao.getGeneralItemById(item.productId)
                if (gen != null) {
                    val prev = gen.currentStock
                    val next = (prev - item.quantity).coerceAtLeast(0)
                    dao.updateGeneralItemStock(item.productId, next)
                    dao.insertStockTransaction(
                        StockTransaction(
                            productId = item.productId,
                            productType = "GENERAL",
                            productName = item.productName,
                            batchNumber = item.batchNumber,
                            transactionType = "SALE",
                            quantity = -item.quantity,
                            previousStock = prev,
                            newStock = next,
                            reason = "Sale Invoice #$invoiceNo",
                            userId = userId
                        )
                    )

                    if (next <= gen.minimumStock) {
                        dao.insertNotification(
                            NotificationItem(
                                type = if (next == 0) "OUT_OF_STOCK" else "LOW_STOCK",
                                title = if (next == 0) "Out of Stock" else "Low Stock Alert",
                                message = "${gen.name} stock is down to $next units (min: ${gen.minimumStock})",
                                productId = gen.id,
                                productType = "GENERAL"
                            )
                        )
                    }
                }
            }
        }

        return sale
    }

    suspend fun insertSupplier(supplier: Supplier): Long = dao.insertSupplier(supplier)
    suspend fun deleteSupplier(supplier: Supplier) = dao.deleteSupplier(supplier)

    suspend fun markNotificationAsRead(id: Long) = dao.markNotificationAsRead(id)
    suspend fun markAllNotificationsAsRead() = dao.markAllNotificationsAsRead()
    suspend fun clearAllNotifications() = dao.clearAllNotifications()

    suspend fun saveSettings(settings: PharmacySettings) = dao.insertOrUpdateSettings(settings)

    suspend fun resetToSampleData() {
        dao.clearAllNotifications()
        dao.insertOrUpdateSettings(SampleData.defaultSettings)
        dao.insertSuppliers(SampleData.sampleSuppliers)
        dao.insertMedicines(SampleData.sampleMedicines)
        dao.insertGeneralItems(SampleData.sampleGeneralItems)
        dao.insertStockTransactions(SampleData.sampleStockTransactions)
        dao.insertPurchases(SampleData.samplePurchases)
        dao.insertSales(SampleData.sampleSales)
        dao.insertNotifications(SampleData.sampleNotifications)
    }
}
