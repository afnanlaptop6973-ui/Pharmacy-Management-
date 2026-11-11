package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.Medicine
import com.example.util.DateUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Zamzam Medical", appName)
    }

    @Test
    fun `test medicine stock status calculation`() {
        val medicine = Medicine(
            name = "Paracetamol",
            batchNumber = "B-101",
            currentStock = 5,
            minimumStock = 15,
            expiryDate = "2028-12-31"
        )
        assertTrue(medicine.isLowStock)
        assertEquals(com.example.data.model.StockStatus.CRITICAL, medicine.stockStatus)
    }

    @Test
    fun `test currency formatting`() {
        val formatted = DateUtils.formatCurrency(150.50)
        assertTrue(formatted.contains("150.50"))
    }
}
