package com.example.ims.ui.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.outlined.Inventory
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.ims.navigation.Screen

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Inventory : BottomNavItem(
        route = Screen.Inventory.route,
        title = "Inventory",
        selectedIcon = Icons.Filled.Inventory,
        unselectedIcon = Icons.Outlined.Inventory
    )
    
    object Shipping : BottomNavItem(
        route = Screen.Shipping.route,
        title = "Shipping",
        selectedIcon = Icons.Filled.LocalShipping,
        unselectedIcon = Icons.Outlined.LocalShipping
    )
}

@Composable
fun BottomNavBar(navController: NavController) {
    val navItems = listOf(
        BottomNavItem.Inventory,
        BottomNavItem.Shipping
    )
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    // Don't show bottom navigation on scanner and product import screens
    if (currentRoute == Screen.ImportScanner.route || 
        currentRoute == Screen.ExportScanner.route ||
        currentRoute?.startsWith("product_import") == true) {
        return
    }
    
    NavigationBar {
        navItems.forEach { navItem ->
            val selected = currentRoute == navItem.route
            
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (currentRoute != navItem.route) {
                        navController.navigate(navItem.route) {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            popUpTo(Screen.Inventory.route) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination when
                            // reselecting the same item
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (selected) navItem.selectedIcon else navItem.unselectedIcon,
                        contentDescription = navItem.title
                    )
                },
                label = { Text(text = navItem.title) }
            )
        }
    }
} 