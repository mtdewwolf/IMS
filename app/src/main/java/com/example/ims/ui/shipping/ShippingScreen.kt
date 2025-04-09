package com.example.ims.ui.shipping

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ims.data.model.Product
import com.example.ims.ui.product.ProductViewModel
import java.text.NumberFormat
import java.util.*

data class ShippingItem(
    val product: Product,
    var quantity: Int = 1
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShippingScreen(
    viewModel: ProductViewModel = viewModel(),
    onScanRequested: () -> Unit,
    onShippingComplete: () -> Unit
) {
    var shippingItems by remember { mutableStateOf(listOf<ShippingItem>()) }
    
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ship Items") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onScanRequested) {
                Icon(Icons.Default.Add, contentDescription = "Scan Item")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (shippingItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Scan items to add to shipment",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(shippingItems) { item ->
                        ShippingItemCard(
                            shippingItem = item,
                            currencyFormat = currencyFormat,
                            onQuantityChanged = { newQuantity ->
                                shippingItems = shippingItems.map {
                                    if (it.product.id == item.product.id) {
                                        it.copy(quantity = newQuantity)
                                    } else {
                                        it
                                    }
                                }
                            },
                            onRemove = {
                                shippingItems = shippingItems.filter {
                                    it.product.id != item.product.id
                                }
                            }
                        )
                    }
                }
                
                Divider(modifier = Modifier.padding(vertical = 16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total Items: ${shippingItems.sumOf { it.quantity }}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    
                    Button(
                        onClick = {
                            // Process the shipment
                            shippingItems.forEach { item ->
                                viewModel.decreaseProductQuantity(
                                    item.product.id,
                                    item.quantity
                                )
                            }
                            onShippingComplete()
                        },
                        enabled = shippingItems.isNotEmpty()
                    ) {
                        Text("Submit Shipment")
                    }
                }
            }
        }
    }
}

@Composable
fun ShippingItemCard(
    shippingItem: ShippingItem,
    currencyFormat: NumberFormat,
    onQuantityChanged: (Int) -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = shippingItem.product.name,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Text(
                        text = "SKU: ${shippingItem.product.sku}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Text(
                        text = "Price: ${currencyFormat.format(shippingItem.product.customerPrice)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                IconButton(onClick = onRemove) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove Item"
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Quantity:",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(end = 8.dp)
                )
                
                OutlinedButton(
                    onClick = {
                        if (shippingItem.quantity > 1) {
                            onQuantityChanged(shippingItem.quantity - 1)
                        }
                    },
                    modifier = Modifier.size(40.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("-")
                }
                
                Text(
                    text = "${shippingItem.quantity}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                
                OutlinedButton(
                    onClick = {
                        if (shippingItem.quantity < shippingItem.product.quantity) {
                            onQuantityChanged(shippingItem.quantity + 1)
                        }
                    },
                    modifier = Modifier.size(40.dp),
                    contentPadding = PaddingValues(0.dp),
                    enabled = shippingItem.quantity < shippingItem.product.quantity
                ) {
                    Text("+")
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                Text(
                    text = "Available: ${shippingItem.product.quantity}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun addProductToShipment(
    shippingItems: List<ShippingItem>,
    product: Product,
    onShippingItemsUpdated: (List<ShippingItem>) -> Unit
) {
    // Check if product is already in the list
    val existingItemIndex = shippingItems.indexOfFirst { it.product.id == product.id }
    
    if (existingItemIndex != -1) {
        // If product already exists, update quantity if possible
        val existingItem = shippingItems[existingItemIndex]
        if (existingItem.quantity < product.quantity) {
            val updatedItems = shippingItems.toMutableList()
            updatedItems[existingItemIndex] = existingItem.copy(quantity = existingItem.quantity + 1)
            onShippingItemsUpdated(updatedItems)
        }
    } else {
        // Otherwise add new item if quantity available
        if (product.quantity > 0) {
            onShippingItemsUpdated(shippingItems + ShippingItem(product, 1))
        }
    }
} 