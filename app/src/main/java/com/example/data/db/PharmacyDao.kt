package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.GeneralItem
import com.example.data.model.Medicine
import com.example.data.model.NotificationItem
import com.example.data.model.PharmacySettings
import com.example.data.model.Purchase
import com.example.data.model.Sale
import com.example.data.model.StockTransaction
import com.example.data.model.Supplier
import kotlinx.coroutines.flow.Flow

@Dao
interface PharmacyDao {

    // --- Medicines ---
    @Query("SELECT * FROM medicines ORDER BY name ASC")
    fun getAllMedicines(): Flow<List<Medicine>>

    @Query("SELECT * FROM medicines WHERE id = :id")
    suspend fun getMedicineById(id: Long): Medicine?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicine(medicine: Medicine): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicines(medicines: List<Medicine>)

    @Update
    suspend fun updateMedicine(medicine: Medicine)

    @Delete
    suspend fun deleteMedicine(medicine: Medicine)

    @Query("UPDATE medicines SET currentStock = :newStock, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateMedicineStock(id: Long, newStock: Int, updatedAt: Long = System.currentTimeMillis())

    // --- General Items ---
    @Query("SELECT * FROM general_items ORDER BY name ASC")
    fun getAllGeneralItems(): Flow<List<GeneralItem>>

    @Query("SELECT * FROM general_items WHERE id = :id")
    suspend fun getGeneralItemById(id: Long): GeneralItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGeneralItem(item: GeneralItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGeneralItems(items: List<GeneralItem>)

    @Update
    suspend fun updateGeneralItem(item: GeneralItem)

    @Delete
    suspend fun deleteGeneralItem(item: GeneralItem)

    @Query("UPDATE general_items SET currentStock = :newStock, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateGeneralItemStock(id: Long, newStock: Int, updatedAt: Long = System.currentTimeMillis())

    // --- Stock Transactions ---
    @Query("SELECT * FROM stock_transactions ORDER BY date DESC")
    fun getAllStockTransactions(): Flow<List<StockTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStockTransaction(tx: StockTransaction): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStockTransactions(txs: List<StockTransaction>)

    // --- Sales ---
    @Query("SELECT * FROM sales ORDER BY createdAt DESC")
    fun getAllSales(): Flow<List<Sale>>

    @Query("SELECT * FROM sales WHERE id = :id")
    suspend fun getSaleById(id: Long): Sale?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSale(sale: Sale): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSales(sales: List<Sale>)

    // --- Purchases ---
    @Query("SELECT * FROM purchases ORDER BY purchaseDate DESC")
    fun getAllPurchases(): Flow<List<Purchase>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPurchase(purchase: Purchase): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPurchases(purchases: List<Purchase>)

    // --- Suppliers ---
    @Query("SELECT * FROM suppliers ORDER BY name ASC")
    fun getAllSuppliers(): Flow<List<Supplier>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSupplier(supplier: Supplier): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSuppliers(suppliers: List<Supplier>)

    @Delete
    suspend fun deleteSupplier(supplier: Supplier)

    // --- Notifications ---
    @Query("SELECT * FROM notifications ORDER BY createdAt DESC")
    fun getAllNotifications(): Flow<List<NotificationItem>>

    @Query("SELECT COUNT(*) FROM notifications WHERE isRead = 0")
    fun getUnreadNotificationsCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationItem>)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationAsRead(id: Long)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllNotificationsAsRead()

    @Query("DELETE FROM notifications")
    suspend fun clearAllNotifications()

    // --- Pharmacy Settings ---
    @Query("SELECT * FROM pharmacy_settings WHERE id = 1")
    fun getSettings(): Flow<PharmacySettings?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSettings(settings: PharmacySettings)
}
