package com.example.saleshub.model

sealed class Screen(val route: String) {
    object Home : Screen("home")

    object SalesModule : Screen("modulo_ventas")
    object AccountsModule : Screen("modulo_cuentas")
    object InventoryModule : Screen("modulo_inventario")

    object RegisterSale : Screen("register_Sale")
    object SalesHistory : Screen("sales_History")

    object Deadlines : Screen("sales_deadlines")
    object DeptPayment : Screen("sales_deptpayment")
    object RegisterClient : Screen("register_client")


    object DeleteProduct : Screen("delete_product")
    object EditProductSwipe : Screen("delete_product")
    object EditProduct : Screen("edit_product")
    object RegisterProduct : Screen("register_product")
    object UpdateStock : Screen("update_stock")
    object ViewInventory : Screen("view_inventory")
    object viewInventoryContent : Screen("inventory_content")

}
