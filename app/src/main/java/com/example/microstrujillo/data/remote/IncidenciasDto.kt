package com.example.microstrujillo.data.remote

import com.google.gson.annotations.SerializedName

/**
 * Contrato DTO para consumo con Retrofit 2 (JSON)
 */
data class IncidenciaDto(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("title") val titulo: String,
    @SerializedName("body") val cuerpo: String,
    @SerializedName("userId") val usuarioId: Int = 1
)

enum class TipoIncidencia(val etiqueta: String, val emoji: String) {
    DEMORA("Demora", "⏳"),
    TRAFICO("Tráfico Pesado", "🚗"),
    DESVIO("Desvío de Ruta", "🚧"),
    ACCIDENTE("Bloqueo / Accidente", "⚠️")
}

data class AlertaIncidencia(
    val id: Int,
    val linea: String,
    val tipo: TipoIncidencia,
    val descripcion: String,
    val fechaHora: String = "Hoy",
    val resuelta: Boolean = false
)
