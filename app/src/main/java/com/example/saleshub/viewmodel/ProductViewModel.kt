package com.example.saleshub.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saleshub.data.ProductDao
import com.example.saleshub.model.Product
import com.example.saleshub.repository.ProductRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProductViewModel(private val repository: ProductRepository) : ViewModel() {

    private var productName: String = ""
    private var productDescription: String = ""
    private var productPrice: Double = 0.0
    private var productStock: Int = 0
    private var productStockmin: Int = 0
    private var productType: String = ""

    private val _productListState = MutableStateFlow<List<Product>>(emptyList())
    val productListState: StateFlow<List<Product>> = _productListState

    init {
        getAllProducts()
    }

    private fun getAllProducts() {
        viewModelScope.launch {
            repository.getAllProducts().collect { products ->
                _productListState.value = products
            }
        }
    }




    fun updateProductFields(name: String, description: String, price: Double, stock: Int, stockmin: Int, type: String) {
        productName = name
        productDescription = description
        productPrice = price
        productStock = stock
        productStockmin = stockmin
        productType = type
    }


     fun registerProduct() {
            viewModelScope.launch {
                try {
                    val newProduct = Product(
                        name = productName,
                        description = productDescription,
                        price = productPrice,
                        stock = productStock,
                        stockmin = productStockmin,
                        type = productType
                    )
                    repository.insertProduct(newProduct)
                } catch (e: Exception) {
                    Log.e("ProductViewModel", "Error al registrar el producto: ${e.message}")
                }
            }
        }


    fun deleteProduct(productId: Int) {
        viewModelScope.launch {
            try {
                repository.deleteProduct(productId)
                getAllProducts()
            } catch (e: Exception) {
                Log.e("ProductViewModel", "Error al eliminar el producto: ${e.message}")
            }
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            try {
                repository.updateProduct(product)
            } catch (e: Exception) {
                Log.e("ProductViewModel", "Error al actualizar el producto: ${e.message}")
            }
        }
    }

    fun updateStock(productId: Int, newStock: Int) {
        viewModelScope.launch {
            try {
                repository.updateStock(productId, newStock)
            } catch (e: Exception) {
                Log.e("ProductViewModel", "Error al actualizar el stock: ${e.message}")
            }
        }
    }

     fun clearProductFields() {
        productName = ""
        productDescription = ""
        productPrice = 0.0
        productStock = 0
        productStockmin = 0
        productType = ""
    }

    }


