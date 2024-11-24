package com.example.saleshub.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sales_table")
data class Sale(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "productos") val productos: List<String>,
    @ColumnInfo(name = "cantidades") val cantidades: List<Int>,
    @ColumnInfo(name = "precio_total") val precioTotal: Double,
    @ColumnInfo(name = "fecha") val fecha: Long,
    @ColumnInfo(name = "id_cliente") val idCliente: Int? = null,
    @ColumnInfo(name = "es_fiada") val esFiada: Boolean? = null  // Permite valores nulos
)