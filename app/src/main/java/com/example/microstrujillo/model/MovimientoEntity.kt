package com.example.microstrujillo.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tabla_movimientos")
data class MovimientoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val tipo: String,          // "PAGO_NFC" o "RECARGA"
    val titulo: String,        // Ej: "Pasaje Urbano NFC" o "Recarga de Saldo"
    val detalle: String,       // Ej: "Validador Micro Trujillo" o "Billetera Digital"
    val monto: Double,         // Ej: -1.50 o +5.00
    val fechaHora: String,     // Ej: "13/11/2026 13:20"
    val saldoResultante: Double
)

@Entity(tableName = "tabla_saldo")
data class SaldoEntity(
    @PrimaryKey
    val id: Int = 1,           // Registro único para el saldo persistente de la billetera
    val saldoActual: Double
)