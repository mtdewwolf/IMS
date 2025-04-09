package com.example.ims.data.repository

import com.example.ims.data.dao.ProductDao
import com.example.ims.data.model.Product
import kotlinx.coroutines.flow.Flow

class ProductRepository(private val productDao: ProductDao) {
    
    fun getAllProducts(): Flow<List<Product>> = productDao.getAllProducts()
    
    fun searchProducts(query: String): Flow<List<Product>> = productDao.searchProducts(query)
    
    suspend fun getProductBySku(sku: String): Product? = productDao.getProductBySku(sku)
    
    suspend fun insertProduct(product: Product): Long = productDao.insertProduct(product)
    
    suspend fun updateProduct(product: Product) = productDao.updateProduct(product)
    
    suspend fun deleteProduct(product: Product) = productDao.deleteProduct(product)
    
    suspend fun decreaseQuantity(productId: Long, shippedQuantity: Int) = 
        productDao.decreaseQuantity(productId, shippedQuantity)
} 