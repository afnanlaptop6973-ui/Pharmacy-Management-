package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.PharmacyDatabase
import com.example.data.model.CartItem
import com.example.data.model.GeneralItem
import com.example.data.model.Medicine
import com.example.data.model.PharmacySettings
import com.example.data.model.Sale
import com.example.data.model.Supplier
import com.example.data.repository.PharmacyRepository
import com.example.ui.navigation.ScreenDestination
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class AppThemeMode {
    SYSTEM, LIGHT, DARK
}

class PharmacyViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PharmacyRepository

    init {
        val database = PharmacyDatabase.getDatabase(application, viewModelScope)
        repository = PharmacyRepository(database.pharmacyDao())
    }

    // --- Navigation & Theme State ---
    private val _currentScreen = MutableStateFlow(ScreenDestination.DASHBOARD)
    val currentScreen: StateFlow<ScreenDestination> = _currentScreen.asStateFlow()

    private val _screenBackStack = MutableStateFlow<List<ScreenDestination>>(listOf(ScreenDestination.DASHBOARD))

    private val _themeMode = MutableStateFlow(AppThemeMode.LIGHT)
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    private val _activeUserRole = MutableStateFlow("Admin") // "Admin", "Pharmacist", "Cashier"
    val activeUserRole: StateFlow<String> = _activeUserRole.asStateFlow()

    private val _globalSearchQuery = MutableStateFlow("")
    val globalSearchQuery: StateFlow<String> = _globalSearchQuery.asStateFlow()

    // --- Message / Toast state ---
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    // --- Data Streams from Repository ---
    val allMedicines = repository.allMedicines.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allGeneralItems = repository.allGeneralItems.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allStockTransactions = repository.allStockTransactions.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allSales = repository.allSales.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allPurchases = repository.allPurchases.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allSuppliers = repository.allSuppliers.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allNotifications = repository.allNotifications.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val unreadNotificationsCount = repository.unreadNotificationsCount.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val pharmacySettings = repository.settings.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // --- Cart & Billing State ---
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _customerName = MutableStateFlow("Walk-in Customer")
    val customerName: StateFlow<String> = _customerName.asStateFlow()

    private val _customerPhone = MutableStateFlow("")
    val customerPhone: StateFlow<String> = _customerPhone.asStateFlow()

    private val _customerAddress = MutableStateFlow("")
    val customerAddress: StateFlow<String> = _customerAddress.asStateFlow()

    private val _paymentMethod = MutableStateFlow("Cash")
    val paymentMethod: StateFlow<String> = _paymentMethod.asStateFlow()

    private val _billDiscountPercent = MutableStateFlow(0.0)
    val billDiscountPercent: StateFlow<Double> = _billDiscountPercent.asStateFlow()

    // Completed Sale for Invoice Preview Modal
    private val _lastCompletedSale = MutableStateFlow<Sale?>(null)
    val lastCompletedSale: StateFlow<Sale?> = _lastCompletedSale.asStateFlow()

    // --- Filters ---
    // Medicine filters
    val medicineSearchQuery = MutableStateFlow("")
    val medicineCategoryFilter = MutableStateFlow("All")
    val medicineManufacturerFilter = MutableStateFlow("All")
    val medicineStockFilter = MutableStateFlow("All") // All, In Stock, Low Stock, Out of Stock
    val medicineExpiryFilter = MutableStateFlow("All") // All, Expired, Expiring Soon, Good

    // General Items filters
    val generalSearchQuery = MutableStateFlow("")
    val generalCategoryFilter = MutableStateFlow("All")
    val generalStockFilter = MutableStateFlow("All")

    // Expiry Tab: "All", "Expired", "7 Days", "30 Days", "60 Days", "90 Days"
    val expirySelectedTab = MutableStateFlow("All")

    // Low stock filter: "All", "Medicines", "General Items", "Critical Only"
    val lowStockFilter = MutableStateFlow("All")

    // Stock management tabs: 0 = Current Stock, 1 = Stock History, 2 = Purchase History
    val stockManagementTab = MutableStateFlow(0)

    // Reports filters
    val reportType = MutableStateFlow("Daily Sales")
    val reportDateRange = MutableStateFlow("Today") // Today, This Week, This Month, All Time

    // --- Dialog visibility states ---
    val showAddEditMedicineDialog = MutableStateFlow(false)
    val editingMedicine = MutableStateFlow<Medicine?>(null)

    val showAddEditGeneralDialog = MutableStateFlow(false)
    val editingGeneralItem = MutableStateFlow<GeneralItem?>(null)

    val showStockAdjustmentDialog = MutableStateFlow(false)
    val adjustingItem = MutableStateFlow<Pair<String, Long>?>(null) // (productType, productId)

    val showNotificationsSheet = MutableStateFlow(false)
    val showInvoiceDialog = MutableStateFlow(false)

    // Navigation methods
    fun navigateTo(destination: ScreenDestination) {
        if (_currentScreen.value != destination) {
            _currentScreen.value = destination
            val stack = _screenBackStack.value.toMutableList()
            stack.add(destination)
            _screenBackStack.value = stack
        }
    }

    fun navigateBack(): Boolean {
        val stack = _screenBackStack.value.toMutableList()
        return if (stack.size > 1) {
            stack.removeAt(stack.size - 1)
            _screenBackStack.value = stack
            _currentScreen.value = stack.last()
            true
        } else {
            false
        }
    }

    fun setThemeMode(mode: AppThemeMode) {
        _themeMode.value = mode
    }

    fun setActiveUserRole(role: String) {
        _activeUserRole.value = role
        showMessage("Switched role to $role")
    }

    fun setGlobalSearchQuery(q: String) {
        _globalSearchQuery.value = q
    }

    fun showMessage(msg: String) {
        _userMessage.value = msg
    }

    fun clearMessage() {
        _userMessage.value = null
    }

    // --- Cart Actions ---
    fun addMedicineToCart(medicine: Medicine): Boolean {
        if (medicine.isExpired) {
            showMessage("⚠️ Cannot sell expired medicine: ${medicine.name} (Expired on ${medicine.expiryDate})")
            return false
        }
        if (medicine.currentStock <= 0) {
            showMessage("⚠️ ${medicine.name} is Out of Stock!")
            return false
        }

        val currentList = _cartItems.value.toMutableList()
        val existingIndex = currentList.indexOfFirst { it.productId == medicine.id && it.productType == "MEDICINE" }
        if (existingIndex >= 0) {
            val existing = currentList[existingIndex]
            if (existing.quantity >= medicine.currentStock) {
                showMessage("Cannot add more: only ${medicine.currentStock} units available in stock")
                return false
            }
            currentList[existingIndex] = existing.copy(quantity = existing.quantity + 1)
        } else {
            currentList.add(
                CartItem(
                    productId = medicine.id,
                    productType = "MEDICINE",
                    productName = medicine.name,
                    batchNumber = medicine.batchNumber,
                    availableStock = medicine.currentStock,
                    quantity = 1,
                    unitPrice = medicine.sellingPrice,
                    discountPercent = 0.0,
                    gstPercent = medicine.gstPercentage
                )
            )
        }
        _cartItems.value = currentList
        showMessage("Added ${medicine.name} to bill")
        return true
    }

    fun addGeneralItemToCart(item: GeneralItem): Boolean {
        if (item.isExpired) {
            showMessage("⚠️ Cannot sell expired item: ${item.name}")
            return false
        }
        if (item.currentStock <= 0) {
            showMessage("⚠️ ${item.name} is Out of Stock!")
            return false
        }

        val currentList = _cartItems.value.toMutableList()
        val existingIndex = currentList.indexOfFirst { it.productId == item.id && it.productType == "GENERAL" }
        if (existingIndex >= 0) {
            val existing = currentList[existingIndex]
            if (existing.quantity >= item.currentStock) {
                showMessage("Cannot add more: only ${item.currentStock} units available in stock")
                return false
            }
            currentList[existingIndex] = existing.copy(quantity = existing.quantity + 1)
        } else {
            currentList.add(
                CartItem(
                    productId = item.id,
                    productType = "GENERAL",
                    productName = item.name,
                    batchNumber = item.batchNumber,
                    availableStock = item.currentStock,
                    quantity = 1,
                    unitPrice = item.sellingPrice,
                    discountPercent = 0.0,
                    gstPercent = item.gstPercentage
                )
            )
        }
        _cartItems.value = currentList
        showMessage("Added ${item.name} to bill")
        return true
    }

    fun updateCartItemQuantity(productId: Long, productType: String, delta: Int) {
        val currentList = _cartItems.value.toMutableList()
        val idx = currentList.indexOfFirst { it.productId == productId && it.productType == productType }
        if (idx >= 0) {
            val item = currentList[idx]
            val newQty = item.quantity + delta
            if (newQty <= 0) {
                currentList.removeAt(idx)
            } else if (newQty > item.availableStock) {
                showMessage("Available stock limit reached (${item.availableStock} pcs)")
                return
            } else {
                currentList[idx] = item.copy(quantity = newQty)
            }
            _cartItems.value = currentList
        }
    }

    fun setCartItemDiscount(productId: Long, productType: String, discount: Double) {
        val currentList = _cartItems.value.toMutableList()
        val idx = currentList.indexOfFirst { it.productId == productId && it.productType == productType }
        if (idx >= 0) {
            currentList[idx] = currentList[idx].copy(discountPercent = discount.coerceIn(0.0, 100.0))
            _cartItems.value = currentList
        }
    }

    fun removeCartItem(productId: Long, productType: String) {
        _cartItems.value = _cartItems.value.filterNot { it.productId == productId && it.productType == productType }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
        _customerName.value = "Walk-in Customer"
        _customerPhone.value = ""
        _customerAddress.value = ""
        _billDiscountPercent.value = 0.0
    }

    fun setCustomerDetails(name: String, phone: String, address: String) {
        _customerName.value = name
        _customerPhone.value = phone
        _customerAddress.value = address
    }

    fun setPaymentMethod(method: String) {
        _paymentMethod.value = method
    }

    fun setBillDiscount(discount: Double) {
        _billDiscountPercent.value = discount.coerceIn(0.0, 100.0)
    }

    fun completeCurrentSale() {
        val items = _cartItems.value
        if (items.isEmpty()) {
            showMessage("Cart is empty! Add products first.")
            return
        }

        val subtotal = items.sumOf { it.subtotal }
        val itemsDiscount = items.sumOf { it.discountAmount }
        val overallDiscount = (subtotal - itemsDiscount) * (_billDiscountPercent.value / 100.0)
        val totalDiscount = itemsDiscount + overallDiscount
        val totalTax = items.sumOf { it.taxAmount }
        val grandTotal = (subtotal - totalDiscount + totalTax).coerceAtLeast(0.0)

        viewModelScope.launch {
            try {
                val createdSale = repository.completeSale(
                    customerName = _customerName.value,
                    customerPhone = _customerPhone.value,
                    customerAddress = _customerAddress.value,
                    cartItems = items,
                    subtotal = subtotal,
                    discount = totalDiscount,
                    tax = totalTax,
                    grandTotal = grandTotal,
                    paymentMethod = _paymentMethod.value,
                    userId = _activeUserRole.value
                )
                _lastCompletedSale.value = createdSale
                showInvoiceDialog.value = true
                clearCart()
                showMessage("Sale recorded successfully! Invoice #${createdSale.invoiceNumber}")
            } catch (e: Exception) {
                showMessage("Failed to complete sale: ${e.message}")
            }
        }
    }

    // --- Product CRUD ---
    fun saveMedicine(medicine: Medicine) {
        viewModelScope.launch {
            if (medicine.id == 0L) {
                repository.insertMedicine(medicine, userId = _activeUserRole.value)
                showMessage("Medicine '${medicine.name}' added successfully")
            } else {
                repository.updateMedicine(medicine)
                showMessage("Medicine '${medicine.name}' updated")
            }
            showAddEditMedicineDialog.value = false
            editingMedicine.value = null
        }
    }

    fun deleteMedicine(medicine: Medicine) {
        viewModelScope.launch {
            repository.deleteMedicine(medicine)
            showMessage("Medicine '${medicine.name}' deleted")
        }
    }

    fun saveGeneralItem(item: GeneralItem) {
        viewModelScope.launch {
            if (item.id == 0L) {
                repository.insertGeneralItem(item, userId = _activeUserRole.value)
                showMessage("General item '${item.name}' added successfully")
            } else {
                repository.updateGeneralItem(item)
                showMessage("General item '${item.name}' updated")
            }
            showAddEditGeneralDialog.value = false
            editingGeneralItem.value = null
        }
    }

    fun deleteGeneralItem(item: GeneralItem) {
        viewModelScope.launch {
            repository.deleteGeneralItem(item)
            showMessage("Item '${item.name}' deleted")
        }
    }

    // --- Stock Actions ---
    fun submitNewStock(
        productType: String,
        productId: Long,
        productName: String,
        batchNumber: String,
        quantity: Int,
        purchasePrice: Double,
        supplierName: String,
        invoiceNumber: String,
        mfgDate: String,
        expiryDate: String
    ) {
        viewModelScope.launch {
            val ok = repository.addNewStock(
                productType = productType,
                productId = productId,
                productName = productName,
                batchNumber = batchNumber,
                quantity = quantity,
                purchasePrice = purchasePrice,
                supplierName = supplierName,
                invoiceNumber = invoiceNumber,
                mfgDate = mfgDate,
                expiryDate = expiryDate,
                userId = _activeUserRole.value
            )
            if (ok) {
                showMessage("Stock added: +$quantity units of $productName")
            } else {
                showMessage("Failed to add stock. Please check product selection.")
            }
        }
    }

    fun adjustStock(
        productType: String,
        productId: Long,
        quantityChange: Int,
        reason: String
    ) {
        viewModelScope.launch {
            val ok = repository.adjustStock(
                productType = productType,
                productId = productId,
                quantityChange = quantityChange,
                reason = reason,
                userId = _activeUserRole.value
            )
            if (ok) {
                showMessage("Stock adjustment recorded: $quantityChange units ($reason)")
            } else {
                showMessage("Failed to adjust stock.")
            }
            showStockAdjustmentDialog.value = false
            adjustingItem.value = null
        }
    }

    fun markDisposed(medicine: Medicine) {
        viewModelScope.launch {
            repository.markDisposed(medicine, userId = _activeUserRole.value)
            showMessage("Expired medicine ${medicine.name} marked as disposed")
        }
    }

    // --- Notifications ---
    fun markNotificationAsRead(id: Long) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }

    fun markAllNotificationsAsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsAsRead()
            showMessage("All notifications marked as read")
        }
    }

    fun clearAllNotifications() {
        viewModelScope.launch {
            repository.clearAllNotifications()
            showMessage("Notifications cleared")
        }
    }

    // --- Settings ---
    fun saveSettings(settings: PharmacySettings) {
        viewModelScope.launch {
            repository.saveSettings(settings)
            showMessage("Pharmacy settings saved successfully")
        }
    }

    fun resetSampleData() {
        viewModelScope.launch {
            repository.resetToSampleData()
            clearCart()
            showMessage("Reset completed with fresh Marg ERP sample data")
        }
    }
}
