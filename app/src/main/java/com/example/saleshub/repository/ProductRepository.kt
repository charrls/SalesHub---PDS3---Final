package com.example.saleshub.repository

import com.example.saleshub.data.ProductDao
import com.example.saleshub.model.Product
import kotlinx.coroutines.flow.Flow

class ProductRepository(private val productDao: ProductDao) {

    fun getProductsById(id: Int): Flow<List<Product>> {
        return productDao.getProductsById(id.toString())
    }

    fun getAllProducts(): Flow<List<Product>> {
        return productDao.getAllProducts() // Llamada al método de Dao
    }

    suspend fun insertProduct(product: Product) {
        productDao.insertProduct(product)
    }

    suspend fun deleteProduct(id: Int) {
        productDao.deleteProduct(id)
    }

    // Nueva función para actualizar un producto
    suspend fun updateProduct(product: Product) {
        productDao.updateProduct(product)
    }

    suspend fun updateStock(productId: Int, newStock: Int) {
        productDao.updateStock(productId, newStock)
    }
}

