package com.example.ims.ui.product

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ims.data.model.Product
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductImportScreen(
    sku: String,
    viewModel: ProductViewModel = viewModel(),
    onProductSaved: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    var customerPrice by remember { mutableStateOf("") }
    var vendor by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("1") }
    var dateReceived by remember { mutableStateOf(Date()) }
    
    var showDatePicker by remember { mutableStateOf(false) }
    
    val dateFormatter = SimpleDateFormat("MM/dd/yyyy", Locale.US)
    
    LaunchedEffect(sku) {
        // Check if product with this SKU already exists
        viewModel.getProductBySku(sku)?.let { product ->
            name = product.name
            cost = product.cost.toString()
            customerPrice = product.customerPrice.toString()
            vendor = product.vendor
            quantity = product.quantity.toString()
            dateReceived = product.dateReceived
        }
    }
    
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = dateReceived.time
        )
        
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        dateReceived = Date(it)
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Product Import") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // SKU display
            OutlinedTextField(
                value = sku,
                onValueChange = {},
                label = { Text("SKU") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )
            
            // Product name
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Product Name") },
                modifier = Modifier.fillMaxWidth()
            )
            
            // Date received
            OutlinedTextField(
                value = dateFormatter.format(dateReceived),
                onValueChange = {},
                label = { Text("Date Received") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Select Date"
                        )
                    }
                }
            )
            
            // Cost
            OutlinedTextField(
                value = cost,
                onValueChange = { cost = it },
                label = { Text("Cost") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
            
            // Customer price
            OutlinedTextField(
                value = customerPrice,
                onValueChange = { customerPrice = it },
                label = { Text("Customer Price") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
            
            // Vendor
            OutlinedTextField(
                value = vendor,
                onValueChange = { vendor = it },
                label = { Text("Vendor") },
                modifier = Modifier.fillMaxWidth()
            )
            
            // Quantity
            OutlinedTextField(
                value = quantity,
                onValueChange = { quantity = it },
                label = { Text("Quantity") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            
            // Save button
            Button(
                onClick = {
                    val product = Product(
                        sku = sku,
                        name = name,
                        dateReceived = dateReceived,
                        cost = cost.toDoubleOrNull() ?: 0.0,
                        customerPrice = customerPrice.toDoubleOrNull() ?: 0.0,
                        vendor = vendor,
                        quantity = quantity.toIntOrNull() ?: 0
                    )
                    viewModel.saveProduct(product)
                    onProductSaved()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                enabled = name.isNotBlank() && cost.isNotBlank() && 
                          customerPrice.isNotBlank() && vendor.isNotBlank()
            ) {
                Text("Save Product")
            }
        }
    }
} 