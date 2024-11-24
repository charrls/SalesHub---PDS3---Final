package com.example.saleshub.repository

import com.example.saleshub.data.SalesDao
import com.example.saleshub.model.Sale
import kotlinx.coroutines.flow.Flow

// SalesRepository.kt


class SalesRepository(private val salesDao: SalesDao) {

    fun getAllSales(): Flow<List<Sale>> = salesDao.getAllSales()

    suspend fun insertSale(sale: Sale) {
        salesDao.insertSale(sale)
    }

    suspend fun getSaleById(saleId: Int): Flow<Sale> {
        return salesDao.getSaleById(saleId)
    }

    // Eliminar una venta por su objeto
    suspend fun deleteSale(sale: Sale) {
        salesDao.deleteSale(sale)
    }

    // Eliminar una venta por su ID
    suspend fun deleteSaleById(saleId: Int) {
        salesDao.deleteSaleById(saleId)
    }

    // Actualizar una venta
    suspend fun updateSale(sale: Sale) {
        salesDao.insertSale(sale) // En Room, usar `insert` con `OnConflictStrategy.REPLACE` actúa como actualización.
    }




}