package com.example.data.db

import com.example.data.model.CartItem
import com.example.data.model.GeneralItem
import com.example.data.model.Medicine
import com.example.data.model.NotificationItem
import com.example.data.model.PharmacySettings
import com.example.data.model.Purchase
import com.example.data.model.Sale
import com.example.data.model.StockTransaction
import com.example.data.model.Supplier

object SampleData {
    val defaultSettings = PharmacySettings(
        id = 1,
        pharmacyName = "ZamZam MEDICAL",
        address = "Zamzam Complex, Medical Center Road",
        phone = "+91 98201 54321",
        email = "care@zamzammedical.com",
        gstNumber = "27AAACZ1020A1Z5",
        licenseNumber = "20B-MH-ZM-994123",
        defaultMinStock = 15,
        expiryAlertDays = 30,
        allowNegativeStock = false,
        invoicePrefix = "ZM-",
        defaultGstPercentage = 12.0,
        currencySymbol = "₹",
        activeRole = "Admin"
    )

    val sampleSuppliers = listOf(
        Supplier(name = "Sun Pharma Distributorship Ltd.", phone = "+91 98210 11223", email = "orders@sunpharma-dist.com", address = "Sector 18, Andheri East, Mumbai", gstin = "27AAACS1122A1Z1"),
        Supplier(name = "Cipla Healthcare Logistics", phone = "+91 98220 33445", email = "sales@ciplalogistics.in", address = "MIDC Industrial Area, Thane", gstin = "27AACCC3344B1Z2"),
        Supplier(name = "Apollo Med Supply Corp.", phone = "+91 98230 55667", email = "supply@apollomed.com", address = "Lower Parel Commercial Hub, Mumbai", gstin = "27AAACA5566C1Z3"),
        Supplier(name = "GlaxoSmithKline Wholesale", phone = "+91 98240 77889", email = "gsk.orders@gskwholesale.com", address = "Bandra Kurla Complex, Mumbai", gstin = "27AAACG7788D1Z4")
    )

    val sampleMedicines = listOf(
        Medicine(
            name = "Paracetamol 650mg",
            genericName = "Paracetamol / Acetaminophen",
            brandName = "Dolo 650",
            category = "Analgesics & Antipyretic",
            manufacturer = "Micro Labs Ltd.",
            batchNumber = "ML-2024-D65",
            currentStock = 180,
            minimumStock = 25,
            purchasePrice = 18.50,
            sellingPrice = 30.00,
            mrp = 34.00,
            manufacturingDate = "2024-05-10",
            expiryDate = "2027-10-15",
            gstPercentage = 12.0,
            rackLocation = "Rack A-01"
        ),
        Medicine(
            name = "Amoxicillin 500mg",
            genericName = "Amoxicillin Trihydrate",
            brandName = "Novamox 500",
            category = "Antibiotics",
            manufacturer = "Cipla Ltd.",
            batchNumber = "CP-8842-NV",
            currentStock = 24,
            minimumStock = 20,
            purchasePrice = 62.00,
            sellingPrice = 95.00,
            mrp = 110.00,
            manufacturingDate = "2024-02-15",
            expiryDate = "2026-11-14", // Expiring in 5 days! (<7 days)
            gstPercentage = 12.0,
            rackLocation = "Rack B-04"
        ),
        Medicine(
            name = "Azithromycin 500mg",
            genericName = "Azithromycin Dihydrate",
            brandName = "Azee 500",
            category = "Antibiotics",
            manufacturer = "Cipla Ltd.",
            batchNumber = "AZ-9910-CP",
            currentStock = 16,
            minimumStock = 15,
            purchasePrice = 75.00,
            sellingPrice = 115.00,
            mrp = 132.00,
            manufacturingDate = "2024-04-12",
            expiryDate = "2026-11-28", // Expiring in 19 days! (<30 days)
            gstPercentage = 12.0,
            rackLocation = "Rack B-06"
        ),
        Medicine(
            name = "Metformin 500mg",
            genericName = "Metformin Hydrochloride",
            brandName = "Glycomet 500",
            category = "Antidiabetic",
            manufacturer = "USV Ltd.",
            batchNumber = "GM-5512-US",
            currentStock = 8, // Low stock (min 20)
            minimumStock = 20,
            purchasePrice = 24.00,
            sellingPrice = 42.00,
            mrp = 48.00,
            manufacturingDate = "2024-01-20",
            expiryDate = "2026-12-25", // Expiring in 46 days! (<60 days)
            gstPercentage = 12.0,
            rackLocation = "Rack C-02"
        ),
        Medicine(
            name = "Pantoprazole 40mg",
            genericName = "Pantoprazole Sodium",
            brandName = "Pan 40",
            category = "Antacid / Gastro",
            manufacturer = "Alkem Laboratories",
            batchNumber = "ALK-4011-P",
            currentStock = 5, // Critical stock (min 25)
            minimumStock = 25,
            purchasePrice = 55.00,
            sellingPrice = 88.00,
            mrp = 104.00,
            manufacturingDate = "2024-03-01",
            expiryDate = "2027-01-20", // Expiring in 72 days! (<90 days)
            gstPercentage = 12.0,
            rackLocation = "Rack C-05"
        ),
        Medicine(
            name = "Atorvastatin 10mg",
            genericName = "Atorvastatin Calcium",
            brandName = "Atorva 10",
            category = "Cardiovascular",
            manufacturer = "Zydus Cadila",
            batchNumber = "ZY-1022-AT",
            currentStock = 28,
            minimumStock = 20,
            purchasePrice = 70.00,
            sellingPrice = 110.00,
            mrp = 125.00,
            manufacturingDate = "2023-10-10",
            expiryDate = "2026-10-10", // EXPIRED!
            gstPercentage = 12.0,
            rackLocation = "Rack D-01"
        ),
        Medicine(
            name = "Cetirizine 10mg",
            genericName = "Cetirizine Hydrochloride",
            brandName = "Cetzine",
            category = "Antihistamine",
            manufacturer = "Dr. Reddy's Lab",
            batchNumber = "DR-7731-CT",
            currentStock = 90,
            minimumStock = 20,
            purchasePrice = 12.00,
            sellingPrice = 22.00,
            mrp = 25.00,
            manufacturingDate = "2024-06-18",
            expiryDate = "2027-08-30",
            gstPercentage = 12.0,
            rackLocation = "Rack A-05"
        ),
        Medicine(
            name = "Telmisartan 40mg",
            genericName = "Telmisartan",
            brandName = "Telma 40",
            category = "Cardiovascular / Hypertension",
            manufacturer = "Glenmark Pharmaceuticals",
            batchNumber = "GM-4040-TL",
            currentStock = 0, // OUT OF STOCK
            minimumStock = 20,
            purchasePrice = 68.00,
            sellingPrice = 105.00,
            mrp = 122.00,
            manufacturingDate = "2024-05-01",
            expiryDate = "2027-11-30",
            gstPercentage = 12.0,
            rackLocation = "Rack D-03"
        ),
        Medicine(
            name = "Montelukast 10mg + Levocetirizine",
            genericName = "Montelukast + Levocetirizine",
            brandName = "Montair LC",
            category = "Respiratory & Allergy",
            manufacturer = "Cipla Ltd.",
            batchNumber = "CP-9014-ML",
            currentStock = 75,
            minimumStock = 15,
            purchasePrice = 98.00,
            sellingPrice = 155.00,
            mrp = 180.00,
            manufacturingDate = "2024-07-10",
            expiryDate = "2027-09-15",
            gstPercentage = 12.0,
            rackLocation = "Rack B-02"
        ),
        Medicine(
            name = "Insulin Glargine 100IU/ml",
            genericName = "Insulin Glargine Recombinant",
            brandName = "Lantus SoloStar",
            category = "Antidiabetic / Injectable",
            manufacturer = "Sanofi India",
            batchNumber = "SF-5501-LT",
            currentStock = 4, // Low stock & near expiry
            minimumStock = 10,
            purchasePrice = 520.00,
            sellingPrice = 690.00,
            mrp = 760.00,
            manufacturingDate = "2024-01-10",
            expiryDate = "2026-11-12", // Expiring in 3 days! (<7 days)
            gstPercentage = 5.0,
            rackLocation = "Cold Storage Refrigerator 1"
        ),
        Medicine(
            name = "Cough Syrup Dextromethorphan",
            genericName = "Dextromethorphan HBr + Chlorpheniramine",
            brandName = "Ascoril D Junior",
            category = "Cough & Cold",
            manufacturer = "Glenmark Pharmaceuticals",
            batchNumber = "GM-3021-AS",
            currentStock = 12,
            minimumStock = 15,
            purchasePrice = 54.00,
            sellingPrice = 85.00,
            mrp = 98.00,
            manufacturingDate = "2023-10-20",
            expiryDate = "2026-10-25", // EXPIRED!
            gstPercentage = 12.0,
            rackLocation = "Rack E-02"
        ),
        Medicine(
            name = "Ibuprofen 400mg",
            genericName = "Ibuprofen",
            brandName = "Brufen 400",
            category = "Analgesics & Anti-inflammatory",
            manufacturer = "Abbott Healthcare",
            batchNumber = "AB-4008-BF",
            currentStock = 110,
            minimumStock = 20,
            purchasePrice = 11.00,
            sellingPrice = 19.50,
            mrp = 23.00,
            manufacturingDate = "2024-08-01",
            expiryDate = "2028-02-15",
            gstPercentage = 12.0,
            rackLocation = "Rack A-03"
        )
    )

    val sampleGeneralItems = listOf(
        GeneralItem(
            name = "Dettol Antiseptic Liquid 250ml",
            category = "Personal Care",
            brand = "Dettol",
            supplier = "Reckitt Benckiser",
            batchNumber = "DT-250-RB",
            currentStock = 42,
            minimumStock = 10,
            purchasePrice = 110.00,
            sellingPrice = 145.00,
            mrp = 155.00,
            gstPercentage = 18.0,
            manufacturingDate = "2024-06-01",
            expiryDate = "2027-06-01"
        ),
        GeneralItem(
            name = "Omron Digital Thermometer MC-246",
            category = "Medical Equipment",
            brand = "Omron",
            supplier = "Apollo Med Supply Corp.",
            batchNumber = "OM-TH-990",
            currentStock = 14,
            minimumStock = 5,
            purchasePrice = 180.00,
            sellingPrice = 280.00,
            mrp = 320.00,
            gstPercentage = 18.0,
            manufacturingDate = "2024-01-15",
            expiryDate = ""
        ),
        GeneralItem(
            name = "3M N95 Respirator Masks (Pack of 5)",
            category = "Medical Equipment",
            brand = "3M",
            supplier = "Apollo Med Supply Corp.",
            batchNumber = "3M-N95-PK5",
            currentStock = 6, // Low Stock (min 15)
            minimumStock = 15,
            purchasePrice = 190.00,
            sellingPrice = 299.00,
            mrp = 350.00,
            gstPercentage = 12.0,
            manufacturingDate = "2024-03-10",
            expiryDate = "2028-03-10"
        ),
        GeneralItem(
            name = "Accu-Chek Active Test Strips (50 Strips)",
            category = "Medical Equipment",
            brand = "Roche Accu-Chek",
            supplier = "Sun Pharma Distributorship Ltd.",
            batchNumber = "AC-50S-RC",
            currentStock = 2, // Critical Stock (min 10)
            minimumStock = 10,
            purchasePrice = 720.00,
            sellingPrice = 950.00,
            mrp = 1099.00,
            gstPercentage = 12.0,
            manufacturingDate = "2024-05-20",
            expiryDate = "2026-12-15"
        ),
        GeneralItem(
            name = "Johnson's Baby Powder 200g",
            category = "Baby Care",
            brand = "Johnson & Johnson",
            supplier = "GlaxoSmithKline Wholesale",
            batchNumber = "JJ-BP-200",
            currentStock = 32,
            minimumStock = 8,
            purchasePrice = 140.00,
            sellingPrice = 190.00,
            mrp = 210.00,
            gstPercentage = 18.0,
            manufacturingDate = "2024-07-01",
            expiryDate = "2027-07-01"
        ),
        GeneralItem(
            name = "Pampers Baby Diapers Medium (44 Pants)",
            category = "Baby Care",
            brand = "Pampers",
            supplier = "Procter & Gamble Dist.",
            batchNumber = "PM-MD-44P",
            currentStock = 18,
            minimumStock = 10,
            purchasePrice = 490.00,
            sellingPrice = 649.00,
            mrp = 749.00,
            gstPercentage = 12.0,
            manufacturingDate = "2024-06-15",
            expiryDate = "2027-06-15"
        ),
        GeneralItem(
            name = "Beurer Pulse Oximeter PO 30",
            category = "Medical Equipment",
            brand = "Beurer",
            supplier = "Apollo Med Supply Corp.",
            batchNumber = "BR-PO30-GE",
            currentStock = 0, // OUT OF STOCK
            minimumStock = 5,
            purchasePrice = 1200.00,
            sellingPrice = 1750.00,
            mrp = 1999.00,
            gstPercentage = 18.0,
            manufacturingDate = "2024-02-01",
            expiryDate = ""
        ),
        GeneralItem(
            name = "Hansaplast Elastic Cotton Bandage 10cm",
            category = "Health Accessories",
            brand = "Hansaplast",
            supplier = "Cipla Healthcare Logistics",
            batchNumber = "HP-EB-10C",
            currentStock = 55,
            minimumStock = 15,
            purchasePrice = 45.00,
            sellingPrice = 75.00,
            mrp = 85.00,
            gstPercentage = 12.0,
            manufacturingDate = "2024-08-01",
            expiryDate = "2029-08-01"
        ),
        GeneralItem(
            name = "Lifebuoy Hand Sanitizer 500ml Pump",
            category = "Personal Care",
            brand = "Lifebuoy",
            supplier = "Hindustan Unilever Dist.",
            batchNumber = "LB-HS-500",
            currentStock = 26,
            minimumStock = 10,
            purchasePrice = 135.00,
            sellingPrice = 199.00,
            mrp = 225.00,
            gstPercentage = 18.0,
            manufacturingDate = "2024-05-15",
            expiryDate = "2027-05-15"
        ),
        GeneralItem(
            name = "MuscleBlaze 100% Whey Protein 1kg",
            category = "Health Accessories",
            brand = "MuscleBlaze",
            supplier = "Bright Lifecare Dist.",
            batchNumber = "MB-WP-1KG",
            currentStock = 7, // Low Stock (min 12)
            minimumStock = 12,
            purchasePrice = 1850.00,
            sellingPrice = 2499.00,
            mrp = 2899.00,
            gstPercentage = 18.0,
            manufacturingDate = "2024-08-10",
            expiryDate = "2026-08-10"
        ),
        GeneralItem(
            name = "Himalaya Purifying Neem Face Wash 150ml",
            category = "Cosmetics",
            brand = "Himalaya Wellness",
            supplier = "Cipla Healthcare Logistics",
            batchNumber = "HM-FW-150",
            currentStock = 38,
            minimumStock = 10,
            purchasePrice = 120.00,
            sellingPrice = 170.00,
            mrp = 190.00,
            gstPercentage = 18.0,
            manufacturingDate = "2024-07-15",
            expiryDate = "2027-07-15"
        ),
        GeneralItem(
            name = "Compact Emergency First Aid Kit Box",
            category = "Medical Equipment",
            brand = "Apex FirstAid",
            supplier = "Apollo Med Supply Corp.",
            batchNumber = "AP-FAK-01",
            currentStock = 15,
            minimumStock = 5,
            purchasePrice = 320.00,
            sellingPrice = 499.00,
            mrp = 550.00,
            gstPercentage = 12.0,
            manufacturingDate = "2024-04-10",
            expiryDate = ""
        )
    )

    val sampleStockTransactions = listOf(
        StockTransaction(
            productId = 1,
            productType = "MEDICINE",
            productName = "Paracetamol 650mg",
            batchNumber = "ML-2024-D65",
            transactionType = "PURCHASE",
            quantity = 100,
            previousStock = 80,
            newStock = 180,
            reason = "Monthly stock replenishment from Sun Pharma",
            date = System.currentTimeMillis() - 86400000L * 2,
            userId = "Pharmacist"
        ),
        StockTransaction(
            productId = 2,
            productType = "MEDICINE",
            productName = "Amoxicillin 500mg",
            batchNumber = "CP-8842-NV",
            transactionType = "SALE",
            quantity = -6,
            previousStock = 30,
            newStock = 24,
            reason = "Sale Invoice #INV-1002",
            date = System.currentTimeMillis() - 86400000L,
            userId = "Cashier"
        ),
        StockTransaction(
            productId = 4,
            productType = "MEDICINE",
            productName = "Metformin 500mg",
            batchNumber = "GM-5512-US",
            transactionType = "SALE",
            quantity = -12,
            previousStock = 20,
            newStock = 8,
            reason = "Sale Invoice #INV-1001",
            date = System.currentTimeMillis() - 86400000L,
            userId = "Cashier"
        ),
        StockTransaction(
            productId = 6,
            productType = "MEDICINE",
            productName = "Atorvastatin 10mg",
            batchNumber = "ZY-1022-AT",
            transactionType = "ADJUSTMENT",
            quantity = -2,
            previousStock = 30,
            newStock = 28,
            reason = "Damaged packaging quarantine",
            date = System.currentTimeMillis() - 3600000L * 5,
            userId = "Admin"
        ),
        StockTransaction(
            productId = 1,
            productType = "GENERAL",
            productName = "Dettol Antiseptic Liquid 250ml",
            batchNumber = "DT-250-RB",
            transactionType = "PURCHASE",
            quantity = 25,
            previousStock = 17,
            newStock = 42,
            reason = "Inward purchase PO-2024-91",
            date = System.currentTimeMillis() - 86400000L * 3,
            userId = "Pharmacist"
        )
    )

    val samplePurchases = listOf(
        Purchase(
            supplierName = "Sun Pharma Distributorship Ltd.",
            invoiceNumber = "SUN-2024-884",
            itemsSummary = "Paracetamol 650mg (100 pcs), Cetirizine 10mg (50 pcs)",
            totalAmount = 2450.00,
            purchaseDate = System.currentTimeMillis() - 86400000L * 2
        ),
        Purchase(
            supplierName = "Cipla Healthcare Logistics",
            invoiceNumber = "CIP-90812",
            itemsSummary = "Novamox 500 (40 pcs), Azee 500 (30 pcs)",
            totalAmount = 4730.00,
            purchaseDate = System.currentTimeMillis() - 86400000L * 4
        ),
        Purchase(
            supplierName = "Apollo Med Supply Corp.",
            invoiceNumber = "AP-SUP-3012",
            itemsSummary = "Omron Thermometers (10 pcs), N95 Masks (25 packs)",
            totalAmount = 6550.00,
            purchaseDate = System.currentTimeMillis() - 86400000L * 6
        )
    )

    val sampleSales = listOf(
        Sale(
            invoiceNumber = "INV-1001",
            customerName = "Rajesh Sharma",
            customerPhone = "+91 98334 11223",
            customerAddress = "Flat 402, Sea View Apts",
            itemsJson = CartItem.listToJson(
                listOf(
                    CartItem(productId = 1, productType = "MEDICINE", productName = "Paracetamol 650mg", batchNumber = "ML-2024-D65", availableStock = 180, quantity = 2, unitPrice = 30.00, discountPercent = 5.0, gstPercent = 12.0),
                    CartItem(productId = 4, productType = "MEDICINE", productName = "Metformin 500mg", batchNumber = "GM-5512-US", availableStock = 8, quantity = 2, unitPrice = 42.00, discountPercent = 0.0, gstPercent = 12.0)
                )
            ),
            subtotal = 144.00,
            discount = 3.00,
            tax = 16.92,
            grandTotal = 157.92,
            paymentMethod = "UPI",
            createdAt = System.currentTimeMillis() - 3600000L * 4,
            userId = "Cashier"
        ),
        Sale(
            invoiceNumber = "INV-1002",
            customerName = "Priya Mehta",
            customerPhone = "+91 97665 44332",
            customerAddress = "12 Hill Road, Bandra",
            itemsJson = CartItem.listToJson(
                listOf(
                    CartItem(productId = 2, productType = "MEDICINE", productName = "Amoxicillin 500mg", batchNumber = "CP-8842-NV", availableStock = 24, quantity = 1, unitPrice = 95.00, discountPercent = 10.0, gstPercent = 12.0),
                    CartItem(productId = 1, productType = "GENERAL", productName = "Dettol Antiseptic Liquid 250ml", batchNumber = "DT-250-RB", availableStock = 42, quantity = 1, unitPrice = 145.00, discountPercent = 0.0, gstPercent = 18.0)
                )
            ),
            subtotal = 240.00,
            discount = 9.50,
            tax = 36.36,
            grandTotal = 266.86,
            paymentMethod = "Card",
            createdAt = System.currentTimeMillis() - 3600000L * 2,
            userId = "Cashier"
        )
    )

    val sampleNotifications = listOf(
        NotificationItem(
            type = "EXPIRED",
            title = "Medicine Expired",
            message = "Atorvastatin 10mg (Batch ZY-1022-AT) expired on 10 Oct 2026. Please quarantine or dispose.",
            productId = 6,
            productType = "MEDICINE",
            isRead = false
        ),
        NotificationItem(
            type = "EXPIRING_SOON",
            title = "Expiring in 3 Days",
            message = "Insulin Glargine 100IU/ml (Batch SF-5501-LT) expires on 12 Nov 2026.",
            productId = 10,
            productType = "MEDICINE",
            isRead = false
        ),
        NotificationItem(
            type = "OUT_OF_STOCK",
            title = "Out of Stock Alert",
            message = "Telmisartan 40mg is completely out of stock. Reorder suggested: 40 units.",
            productId = 8,
            productType = "MEDICINE",
            isRead = false
        ),
        NotificationItem(
            type = "LOW_STOCK",
            title = "Low Stock Warning",
            message = "Pantoprazole 40mg has reached critical level (5 units left, min 25).",
            productId = 5,
            productType = "MEDICINE",
            isRead = false
        )
    )
}
