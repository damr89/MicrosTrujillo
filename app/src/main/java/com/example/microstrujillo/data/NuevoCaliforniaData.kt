package com.example.microstrujillo.data

import android.content.Context
import com.example.microstrujillo.domain.SmartBusEngine
import com.example.microstrujillo.model.RouteEntity
import com.example.microstrujillo.model.Stop
import org.osmdroid.util.GeoPoint

object NuevoCaliforniaData {

    fun obtenerRutas(context: Context): List<RouteEntity> {
        val smartBusList = SmartBusEngine.cargarRutasDesdeAssets(context)
        if (smartBusList.isNotEmpty()) {
            var autoId = 1
            return smartBusList.map { sb ->
                val stops = sb.stops.map { Stop(it.name, GeoPoint(it.lat, it.lng)) }
                
                // Usamos la geometría pre-calculada y curvada que guardamos en los JSONs (propiedad "shape")
                val shapePoints = if (!sb.shape.isNullOrEmpty()) {
                    sb.shape.map { GeoPoint(it.lat, it.lng) }
                } else {
                    sb.stops.map { GeoPoint(it.lat, it.lng) }
                }

                RouteEntity(
                    id = autoId++,
                    nombreLinea = "${sb.company} ${sb.letter} (${sb.direction})",
                    origen = sb.stops.firstOrNull()?.name ?: "Inicio",
                    destino = sb.stops.lastOrNull()?.name ?: "Fin",
                    tiempoEspera = 8,
                    stops = stops,
                    shapePoints = shapePoints,
                    recorrido = shapePoints
                )
            }
        }
        return emptyList()
    }
}
