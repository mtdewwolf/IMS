package com.example.ims.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sku: String,
    val name: String,
    val dateReceived: Date,
    val cost: Double,
    val customerPrice: Double,
    val vendor: String,
    val quantity: Int = 0,
    val lastUpdated: Date = Date()
) 