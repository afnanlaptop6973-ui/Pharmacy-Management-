package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.GeneralItem
import com.example.data.model.Medicine
import com.example.data.model.NotificationItem
import com.example.data.model.PharmacySettings
import com.example.data.model.Purchase
import com.example.data.model.Sale
import com.example.data.model.StockTransaction
import com.example.data.model.Supplier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Medicine::class,
        GeneralItem::class,
        StockTransaction::class,
        Sale::class,
        Purchase::class,
        Supplier::class,
        NotificationItem::class,
        PharmacySettings::class
    ],
    version = 1,
    exportSchema = false
)
abstract class PharmacyDatabase : RoomDatabase() {

    abstract fun pharmacyDao(): PharmacyDao

    companion object {
        @Volatile
        private var INSTANCE: PharmacyDatabase? = null

        fun getDatabase(context: Context, coroutineScope: CoroutineScope): PharmacyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PharmacyDatabase::class.java,
                    "pharmacy_database.db"
                )
                    .addCallback(PharmacyDatabaseCallback(coroutineScope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class PharmacyDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database.pharmacyDao())
                }
            }
        }

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            // Ensure sample data exists if empty
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    val dao = database.pharmacyDao()
                    // Prepopulate if settings or medicines missing
                    try {
                        dao.insertOrUpdateSettings(SampleData.defaultSettings)
                    } catch (_: Exception) {}
                }
            }
        }

        suspend fun populateDatabase(dao: PharmacyDao) {
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
}
