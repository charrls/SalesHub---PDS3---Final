package com.example.saleshub.model

import EditClientScreen
import EditProductScreen
import UpdateStockScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.saleshub.viewmodel.ProductViewModel
import com.example.saleshub.viewmodel.ClientViewModel
import com.example.saleshub.viewmodel.SalesViewModel
import com.example.saleshub.views.accountsmodule.ViewAccountsScreenContent
import com.example.saleshub.views.accountsmodule.DeadlinesScreen
import com.example.saleshub.views.accountsmodule.DeptPaymentScreen
import com.example.saleshub.views.accountsmodule.RegisterClientScreen
import com.example.saleshub.views.home.HomeScreen
import com.example.saleshub.views.inventorymodule.RegisterProductScreen
import com.example.saleshub.views.inventorymodule.ViewInventoryScreenContent
import com.example.saleshub.views.salesmodule.SalesHistoryScreen
import com.example.saleshub.views.salesmodule.SalesModuleScreen
import com.example.saleshub.views.salesmodule.registerSaleScreen



@Composable
fun MainNavGraph(navController: NavHostController, productViewModel: ProductViewModel, clientViewModel: ClientViewModel,    salesViewModel: SalesViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {

        //Home
        composable(Screen.Home.route) {
            HomeScreen(navController, salesViewModel, productViewModel, clientViewModel)
        }

        //Modulos
        composable(Screen.SalesModule.route) {
            SalesModuleScreen(navController)
        }
        composable(Screen.AccountsModule.route) {
            ViewAccountsScreenContent(navController, clientViewModel)
        }

        //Pantallas secundarias de Modulo ventas
        composable(Screen.RegisterSale.route) {
            registerSaleScreen(navController, productViewModel, clientViewModel, salesViewModel)
        }
        composable(Screen.SalesHistory.route) {
            SalesHistoryScreen(navController, salesViewModel, clientViewModel, productViewModel)
        }

        //Pantallas secundarias de Modulo cuentas

        composable(Screen.Deadlines.route) {
            DeadlinesScreen(navController, clientViewModel)
        }

        composable(Screen.RegisterClient.route) {
            RegisterClientScreen(navController, clientViewModel)
        }
        composable("edit_client/{clientId}") { backStackEntry ->
            val clientId = backStackEntry.arguments?.getString("clientId")
            EditClientScreen(navController, clientViewModel, clientId = clientId, isFromSwipe = true)
        }

        composable("add_payment/{clientId}") {backStackEntry ->
            val clientId = backStackEntry.arguments?.getString("clientId")
            DeptPaymentScreen(navController, clientViewModel, clientId = clientId)
        }

        //Pantallas secundarios de Modulo inventario

        composable("edit_product/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")
            EditProductScreen(navController, productViewModel, productId = productId, isFromSwipe = true)
        }
        composable(Screen.EditProduct.route) {
            EditProductScreen(navController, productViewModel)
        }

        composable(Screen.RegisterProduct.route) {
            RegisterProductScreen(navController, productViewModel)
        }

        composable("update_stock/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")?.toIntOrNull()
            productId?.let {
                UpdateStockScreen(navController, productViewModel, it)
            }
        }



        composable(Screen.viewInventoryContent.route) {
            ViewInventoryScreenContent(navController, productViewModel )
        }
    }
}
