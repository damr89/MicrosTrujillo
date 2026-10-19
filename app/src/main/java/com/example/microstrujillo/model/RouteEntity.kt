package com.example.microstrujillo.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.osmdroid.util.GeoPoint

data class Stop(
    val name: String,
    val location: GeoPoint
)

@Entity(tableName = "tabla_rutas")
data class RouteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombreLinea: String,
    val origen: String,
    val destino: String,
    val tiempoEspera: Int,
    val stops: List<Stop> = emptyList(),
    val shapePoints: List<GeoPoint> = emptyList(),
    val recorrido: List<GeoPoint> = emptyList()
)
