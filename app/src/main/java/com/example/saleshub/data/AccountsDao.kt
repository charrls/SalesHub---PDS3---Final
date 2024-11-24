package com.example.saleshub.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import com.example.saleshub.model.Client

@Dao
interface ClientDao {

    @Insert
    suspend fun insertClient(client: Client)

    @Query("SELECT * FROM client_table")
    fun getAllClients(): Flow<List<Client>>


    @Query("SELECT * FROM client_table WHERE id = :clientId LIMIT 1")
    fun getClientById(clientId: Int): Flow<Client>

    @Query("DELETE FROM client_table WHERE id = :id")
    suspend fun deleteClient(id: Int)

    @Update
    suspend fun updateClient(client: Client)

    // Actualizar saldo del cliente
    @Query("UPDATE client_table SET balance = :newBalance WHERE id = :clientId")
    suspend fun updateBalance(clientId: Int, newBalance: Double)

    // Actualizar plazo máximo permitido del cliente
    @Query("UPDATE client_table SET balance = :newTermMax WHERE id = :clientId")
    suspend fun updateTermMax(clientId: Int, newTermMax: Int)



    @Query("UPDATE client_table SET maxAmount = :maxAmount, maxTerm = :maxTerm")
    suspend fun updateAllMaxAmountAndTerm(maxAmount: Double, maxTerm: Int)


}
