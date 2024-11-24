package com.example.saleshub.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import com.example.saleshub.model.Product


@Dao
interface ProductDao {

    @Insert
    suspend fun insertProduct(product: Product)

    @Query("SELECT * FROM product_table")
    fun getAllProducts(): Flow<List<Product>>

    @Query("SELECT * FROM product_table WHERE id = :id")
    fun getProductsById(id: String): Flow<List<Product>>

    @Query("DELETE FROM product_table WHERE id = :id")
    suspend fun deleteProduct(id: Int)

    // Función para actualizar producto
    @Update
    suspend fun updateProduct(product: Product)

    @Query("UPDATE product_table SET stock = :newStock WHERE id = :productId")
    suspend fun updateStock(productId: Int, newStock: Int)
}
