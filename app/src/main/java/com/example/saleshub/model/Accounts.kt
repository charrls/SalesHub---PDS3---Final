package com.example.saleshub.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "client_table")
data class Client(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val phone: String,
    val maxAmount: Double? = null,
    val maxTerm: Int? = null,
    val balance: Double? = null
)
