package com.example.ims.ui.inventory

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    viewModel: ProductViewModel = viewModel(),
    onScanRequested: () -> Unit,
    onProductClicked: (Product) -> Unit
) {
    val products by viewModel.allProducts.collectAsState(initial = emptyList())
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    
    val filteredProducts = if (searchQuery.isNotEmpty()) {
        products.filter { 
            it.name.contains(searchQuery, ignoreCase = true) || 
            it.sku.contains(searchQuery, ignoreCase = true) 
        }
    } else {
        products
    }
    
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)
    
    Scaffold(
        topBar = {
            if (isSearchActive) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onSearch = { isSearchActive = false },
                    active = isSearchActive,
                    onActiveChange = { isSearchActive = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search by name or SKU") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") }
                ) {
                    LazyColumn {
                        items(filteredProducts) { product ->
                            ListItem(
                                headlineText = { Text(product.name) },
                                supportingText = { Text("SKU: ${product.sku}") },
                                modifier = Modifier.clickable {
                                    searchQuery = ""
                                    isSearchActive = false
                                    onProductClicked(product)
                                }
                            )
                        }
                    }
                }
            } else {
                TopAppBar(
                    title = { Text("Inventory") },
                    actions = {
                        IconButton(onClick = { isSearchActive = true }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search"
                            )
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onScanRequested) {
                Icon(Icons.Default.Add, contentDescription = "Scan Product")
            }
        }
    ) { paddingValues ->
        if (filteredProducts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No products found",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(filteredProducts) { product ->
                    ProductItem(
                        product = product,
                        currencyFormat = currencyFormat,
                        onClick = { onProductClicked(product) }
                    )
                }
            }
        }
    }
}

@Composable
fun ProductItem(
    product: Product,
    currencyFormat: NumberFormat,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.headlineSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "SKU: ${product.sku}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Qty: ${product.quantity}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Cost: ${currencyFormat.format(product.cost)}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Price: ${currencyFormat.format(product.customerPrice)}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
} 