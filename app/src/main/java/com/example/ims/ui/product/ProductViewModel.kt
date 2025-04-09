package com.example.ims.ui.product

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ims.data.database.AppDatabase
import com.example.ims.data.model.Product
import com.example.ims.data.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ProductViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: ProductRepository
    
    val allProducts: Flow<List<Product>>
    
    init {
        val database = AppDatabase.getDatabase(application)
        val productDao = database.productDao()
        repository = ProductRepository(productDao)
        allProducts = repository.getAllProducts()
    }
    
    fun saveProduct(product: Product) {
        viewModelScope.launch {
            repository.insertProduct(product)
        }
    }
    
    fun updateProduct(product: Product) {
        viewModelScope.launch {
            repository.updateProduct(product)
        }
    }
    
    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            repository.deleteProduct(product)
        }
    }
    
    suspend fun getProductBySku(sku: String): Product? {
        return repository.getProductBySku(sku)
    }
    
    fun decreaseProductQuantity(productId: Long, shippedQuantity: Int) {
        viewModelScope.launch {
            repository.decreaseQuantity(productId, shippedQuantity)
        }
    }
    
    fun searchProducts(query: String): Flow<List<Product>> {
        return repository.searchProducts(query)
    }
} 