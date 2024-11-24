package com.example.saleshub.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saleshub.model.Sale
import com.example.saleshub.repository.SalesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class SalesViewModel(private val repository: SalesRepository, private val clientViewModel: ClientViewModel // Agregado
) : ViewModel() {

    private val _salesListState = MutableStateFlow<List<Sale>>(emptyList())
    val salesListState: StateFlow<List<Sale>> = _salesListState

    private val _filteredSalesListState = MutableStateFlow<List<Sale>>(emptyList())
    val filteredSalesListState: StateFlow<List<Sale>> = _filteredSalesListState

    init {
        getAllSales()
    }

    private fun getAllSales() {
        viewModelScope.launch {
            repository.getAllSales().collect { sales ->
                _salesListState.value = sales
                _filteredSalesListState.value = sales // Al principio, mostramos todas las ventas
            }
        }
    }

    fun registerSale(
        productos: List<String>,
        cantidades: List<Int>,
        precioTotal: Double,
        esFiada: Boolean? = false,
        idCliente: Int? = null
    ) {
        val esFiadaFinal = esFiada ?: false

        val nuevaVenta = Sale(
            productos = productos,
            cantidades = cantidades,
            precioTotal = precioTotal,
            fecha = System.currentTimeMillis(),
            idCliente = idCliente,
            esFiada = esFiadaFinal
        )

        viewModelScope.launch {
            repository.insertSale(nuevaVenta)

            // Si la venta es fiada, actualizar el balance del cliente
            if (esFiadaFinal && idCliente != null) {
                updateClientBalance(idCliente, precioTotal)
            }
        }
    }

    private suspend fun updateClientBalance(clientId: Int, amount: Double) {
        // Llama al método de ClientViewModel para actualizar el balance
        val currentBalance = clientViewModel.clientListState.value
            .find { it.id == clientId }?.balance ?: 0.0
        val updatedBalance = currentBalance + amount

        clientViewModel.updateBalance(clientId, updatedBalance)
    }

    fun filterSalesByDate(period: String) {
        val filteredSales = when (period) {
            "Hoy" -> _salesListState.value.filter { isSameDay(it.fecha) }
            "Semana" -> _salesListState.value.filter { isSameWeek(it.fecha) }
            "Quincena" -> _salesListState.value.filter { isSameBiweek(it.fecha) }
            else -> _salesListState.value
        }
        _filteredSalesListState.value = filteredSales
    }


    private fun isSameDay(dateMillis: Long): Boolean {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        val today = calendar.get(Calendar.DAY_OF_YEAR)

        calendar.timeInMillis = dateMillis
        val saleDay = calendar.get(Calendar.DAY_OF_YEAR)

        return today == saleDay
    }


    private fun isSameWeek(dateMillis: Long): Boolean {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        val today = calendar.timeInMillis

        // Calcula la fecha de hace 7 días
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        val weekStart = calendar.timeInMillis

        return dateMillis >= weekStart && dateMillis <= today
    }


    private fun isSameBiweek(dateMillis: Long): Boolean {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        val today = calendar.timeInMillis

        // Calcula la fecha de hace 15 días
        calendar.add(Calendar.DAY_OF_YEAR, -15)
        val biweekStart = calendar.timeInMillis

        return dateMillis >= biweekStart && dateMillis <= today
    }

    fun deleteSaleById(saleId: Int) {
        viewModelScope.launch {
            try {
                repository.deleteSaleById(saleId)
            } catch (e: Exception) {
                println("Error al eliminar la venta: ${e.message}")
            }
        }
    }



}
