package com.example.saleshub.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.saleshub.model.Sale
import kotlinx.coroutines.flow.Flow

// SalesDao.kt
@Dao
interface SalesDao {

    @Query("SELECT * FROM sales_table")
    fun getAllSales(): Flow<List<Sale>>

    @Query("SELECT * FROM sales_table WHERE id = :saleId")
    fun getSaleById(saleId: Int): Flow<Sale>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSale(sale: Sale)

    @Delete
    suspend fun deleteSale(sale: Sale)

    @Query("DELETE FROM sales_table WHERE id = :saleId")
    suspend fun deleteSaleById(saleId: Int)
}