package com.example.saleshub

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.example.saleshub.data.AppDatabase
import com.example.saleshub.repository.ProductRepository
import com.example.saleshub.repository.ClientRepository
import com.example.saleshub.ui.theme.SalesHubTheme
import com.example.saleshub.viewmodel.ProductViewModel
import com.example.saleshub.viewmodel.ClientViewModel
import com.example.saleshub.viewmodel.ProductViewModelFactory
import com.example.saleshub.viewmodel.ClientViewModelFactory
import com.example.saleshub.model.MainNavGraph
import com.example.saleshub.repository.SalesRepository
import com.example.saleshub.viewmodel.SalesViewModel
import com.example.saleshub.viewmodel.SalesViewModelFactory
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configuración de barras de estado y navegación
        configureSystemBars()

        setContent {
            SalesHubTheme {
                val navController = rememberNavController()

                // Configuración de ProductViewModel
                val productDao = AppDatabase.getDatabase(applicationContext).productDao()
                val productRepository = ProductRepository(productDao)
                val productViewModel: ProductViewModel = ViewModelProvider(
                    this,
                    ProductViewModelFactory(productRepository)
                )[ProductViewModel::class.java]

                Log.d("MainActivity", "ProductViewModel initialized: $productViewModel")

                // Configuración de ClientViewModel
                val clientDao = AppDatabase.getDatabase(applicationContext).clientDao()
                val clientRepository = ClientRepository(clientDao)
                val clientViewModel: ClientViewModel = ViewModelProvider(
                    this,
                    ClientViewModelFactory(clientRepository, applicationContext)
                )[ClientViewModel::class.java]

                Log.d("MainActivity", "ClientViewModel initialized: $clientViewModel")

                // Configuración de SalesViewModel
                val salesDao = AppDatabase.getDatabase(applicationContext).salesDao()
                val salesRepository = SalesRepository(salesDao)
                val salesViewModel: SalesViewModel = ViewModelProvider(
                    this,
                    SalesViewModelFactory(salesRepository, clientViewModel)
                )[SalesViewModel::class.java]

                Log.d("MainActivity", "SalesViewModel initialized: $salesViewModel")

                // Pasar todos los ViewModels al gráfico de navegación
                MainNavGraph(
                    navController = navController,
                    productViewModel = productViewModel,
                    clientViewModel = clientViewModel,
                    salesViewModel = salesViewModel
                )
            }
        }
    }

    /**
     * Configura las barras de estado y navegación para mantener colores claros y texto oscuro.
     */
    private fun configureSystemBars() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val insetsController = WindowInsetsControllerCompat(window, window.decorView)

        // Configurar la barra de estado
        window.statusBarColor = ContextCompat.getColor(this, R.color.lightNavigation)
        insetsController.isAppearanceLightStatusBars = true

        // Configurar la barra de navegación
        window.navigationBarColor = android.graphics.Color.WHITE
        insetsController.isAppearanceLightNavigationBars = true
    }
}
