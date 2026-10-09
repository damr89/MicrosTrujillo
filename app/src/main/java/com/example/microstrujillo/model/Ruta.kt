package com.example.microstrujillo.model

data class Ruta(
    val id: Int = 0,
    val nombreLinea: String,   // Ej: "Línea 10 - Nuevo California"
    val origen: String,        // Ej: "Centro"
    val destino: String,       // Ej: "Huanchaco"
    val tiempoEspera: Int      // Ej: 12 (minutos)
)
