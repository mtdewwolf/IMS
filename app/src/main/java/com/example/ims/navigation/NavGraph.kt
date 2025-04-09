package com.example.ims.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.ims.ui.inventory.InventoryScreen
import com.example.ims.ui.product.ProductImportScreen
import com.example.ims.ui.scanner.ScannerScreen
import com.example.ims.ui.shipping.ShippingScreen

sealed class Screen(val route: String) {
    object Inventory : Screen("inventory")
    object ImportScanner : Screen("import_scanner")
    object ExportScanner : Screen("export_scanner")
    object ProductImport : Screen("product_import/{sku}")
    object Shipping : Screen("shipping")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    // Variable to store scanned SKU for shipping
    var shippingSku by remember { mutableStateOf<String?>(null) }
    
    NavHost(
        navController = navController,
        startDestination = Screen.Inventory.route,
        modifier = modifier
    ) {
        // Inventory screen (home)
        composable(Screen.Inventory.route) {
            InventoryScreen(
                onScanRequested = { 
                    navController.navigate(Screen.ImportScanner.route)
                },
                onProductClicked = { product -> 
                    navController.navigate("product_import/${product.sku}")
                }
            )
        }
        
        // Scanner for importing products
        composable(Screen.ImportScanner.route) {
            ScannerScreen(
                onBarcodeDetected = { sku ->
                    navController.navigate("product_import/$sku")
                }
            )
        }
        
        // Scanner for exporting/shipping products
        composable(Screen.ExportScanner.route) {
            ScannerScreen(
                onBarcodeDetected = { sku ->
                    shippingSku = sku
                    navController.popBackStack()
                }
            )
        }
        
        // Product import screen
        composable(
            route = Screen.ProductImport.route,
            arguments = listOf(
                navArgument("sku") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val sku = backStackEntry.arguments?.getString("sku") ?: ""
            ProductImportScreen(
                sku = sku,
                onProductSaved = {
                    navController.navigate(Screen.Inventory.route) {
                        popUpTo(Screen.Inventory.route) { inclusive = true }
                    }
                }
            )
        }
        
        // Shipping screen
        composable(Screen.Shipping.route) {
            ShippingScreen(
                onScanRequested = {
                    navController.navigate(Screen.ExportScanner.route)
                },
                onShippingComplete = {
                    navController.navigate(Screen.Inventory.route) {
                        popUpTo(Screen.Inventory.route) { inclusive = true }
                    }
                }
            )
        }
    }
} 