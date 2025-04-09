package com.example.ims.data.dao

import androidx.room.*
import com.example.ims.data.model.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY lastUpdated DESC")
    fun getAllProducts(): Flow<List<Product>>
    
    @Query("SELECT * FROM products WHERE sku = :sku LIMIT 1")
    suspend fun getProductBySku(sku: String): Product?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product): Long
    
    @Update
    suspend fun updateProduct(product: Product)
    
    @Query("UPDATE products SET quantity = quantity - :shippedQuantity WHERE id = :productId")
    suspend fun decreaseQuantity(productId: Long, shippedQuantity: Int)
    
    @Delete
    suspend fun deleteProduct(product: Product)
    
    @Query("SELECT * FROM products WHERE sku LIKE '%' || :query || '%' OR name LIKE '%' || :query || '%'")
    fun searchProducts(query: String): Flow<List<Product>>
} 