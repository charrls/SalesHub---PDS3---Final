// viewmodel/ClientViewModel.kt
package com.example.saleshub.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saleshub.model.Client
import com.example.saleshub.repository.ClientRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.content.Context
import kotlinx.coroutines.flow.first


class ClientViewModel(
    private val repository: ClientRepository,
    context: Context
) : ViewModel() {

    // Instancia de SharedPreferences para almacenar los valores globales
    private val sharedPreferences = context.getSharedPreferences("client_preferences", Context.MODE_PRIVATE)

    // Flow para los valores de monto y plazo máximos
    var globalMaxAmount = MutableStateFlow<Double?>(null)
    var globalMaxTerm = MutableStateFlow<Int?>(null)

    private val _clientListState = MutableStateFlow<List<Client>>(emptyList())
    val clientListState: StateFlow<List<Client>> = _clientListState

    private val _selectedClientState = MutableStateFlow<Client?>(null)
    val selectedClientState: StateFlow<Client?> = _selectedClientState

    init {
        loadStoredMaxValues()
        getAllClients()
    }
    fun getClientById(clientId: Int) {
        viewModelScope.launch {
            // Llamada reactiva al repositorio
            repository.getClientById(clientId).collect { client ->
                _selectedClientState.value = client // Actualizar el estado con el cliente obtenido
            }
        }
    }
    // Carga valores guardados de SharedPreferences
    private fun loadStoredMaxValues() {
        globalMaxAmount.value = sharedPreferences.getFloat("maxAmount", 0.0f).toDouble()
        globalMaxTerm.value = sharedPreferences.getInt("maxTerm", 0)
    }

    private fun getAllClients() {
        viewModelScope.launch {
            repository.getAllClients().collect { clients ->
                _clientListState.value = clients
            }
        }
    }
    // En ClientViewModel
    fun processPayment(clientId: Int, paymentAmount: Double) {
        viewModelScope.launch {
            // Obtener el cliente de manera reactiva usando 'first()' para obtener el primer valor
            val client = repository.getClientById(clientId).first()  // 'first' es más adecuado si solo esperas un único cliente
            if (client != null) {
                // Actualizar el saldo (restar el pago al balance actual)
                val newBalance = client.balance!! - paymentAmount
                // Evitar que el saldo sea negativo
                if (newBalance >= 0) {
                    updateBalance(clientId, newBalance)  // Llamada a la función para actualizar el balance
                }
            }
        }
    }



    fun registerClient(name: String, num: String, balance: Double) {
        val newClient = Client(
            name = name,
            phone = num,
            balance = balance,
            maxAmount = globalMaxAmount.value ?: 0.0,
            maxTerm = globalMaxTerm.value ?: 0
        )
        viewModelScope.launch { repository.insertClient(newClient) }
    }

    fun updateClient(client: Client) {
        viewModelScope.launch { repository.updateClient(client) }
    }

    fun updateBalance(clientId: Int, newBalance: Double) {
        viewModelScope.launch { repository.updateBalance(clientId, newBalance) }
    }

    fun updateTermMax(clientId: Int, newTermMax: Int) {
        viewModelScope.launch { repository.updateTermMax(clientId, newTermMax) }
    }

    fun deleteClient(clientId: Int) {
        viewModelScope.launch { repository.deleteClient(clientId) }
    }

    // Actualiza y guarda los valores de monto y plazo máximo
    fun updateAllMaxAmountAndTerm(maxAmount: Double, maxTerm: Int) {
        viewModelScope.launch {
            repository.updateAllMaxAmountAndTerm(maxAmount, maxTerm)
            // Guardar en SharedPreferences
            sharedPreferences.edit().apply {
                putFloat("maxAmount", maxAmount.toFloat())
                putInt("maxTerm", maxTerm)
                apply()
            }
            globalMaxAmount.value = maxAmount
            globalMaxTerm.value = maxTerm
        }
    }
}

