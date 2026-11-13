package com.example.microstrujillo.data

import android.content.Context
import com.example.microstrujillo.domain.SmartBusEngine
import com.example.microstrujillo.model.RouteEntity
import com.example.microstrujillo.model.Stop
import org.osmdroid.util.GeoPoint

object NuevoCaliforniaData {

    fun obtenerRutas(context: Context): List<RouteEntity> {
        val listaFinal = mutableListOf<RouteEntity>()
        var autoId = 1

        // 1. Cargamos las 12 rutas pre-calculadas de Nuevo California desde assets/routes/
        val smartBusList = SmartBusEngine.cargarRutasDesdeAssets(context)
        if (smartBusList.isNotEmpty()) {
            val rutasNuevoCalifornia = smartBusList.map { sb ->
                val stops = sb.stops.map { Stop(it.name, GeoPoint(it.lat, it.lng)) }
                val shapePoints = if (!sb.shape.isNullOrEmpty()) {
                    sb.shape.map { GeoPoint(it.lat, it.lng) }
                } else {
                    sb.stops.map { GeoPoint(it.lat, it.lng) }
                }

                // Nombres descriptivos de origen y destino según la línea y sentido
                val (origenReal, destinoReal) = obtenerExtremosDescriptivos(sb.letter, sb.direction)

                RouteEntity(
                    id = autoId++,
                    nombreLinea = "${sb.company} - Línea ${sb.letter} (${sb.direction})",
                    origen = origenReal,
                    destino = destinoReal,
                    tiempoEspera = when (sb.letter) {
                        "V", "A" -> 5
                        "B1", "C" -> 8
                        else -> 11
                    },
                    stops = stops,
                    shapePoints = shapePoints,
                    recorrido = shapePoints
                )
            }
            listaFinal.addAll(rutasNuevoCalifornia)
        }

        return listaFinal
    }

    private fun obtenerExtremosDescriptivos(letra: String, direccion: String): Pair<String, String> {
        val parIda = when (letra.uppercase()) {
            "A" -> "El Milagro / Panamericana" to "Víctor Larco / Buenos Aires"
            "B1" -> "Víctor Larco / Av. Larco" to "La Esperanza / Los Tulipanes"
            "BC" -> "Víctor Larco / Av. Larco" to "La Esperanza / Av. San Pedro"
            "C" -> "Víctor Larco / Mall Plaza" to "El Milagro / Panamericana"
            "LV" -> "Laredo / Virgen de la Puerta" to "Víctor Larco / Buenos Aires"
            "V" -> "Víctor Larco / UNT / Mansiche" to "La Esperanza / Av. San Pedro"
            else -> "Inicio de Ruta" to "Fin de Ruta"
        }
        return if (direccion.uppercase() == "IDA") parIda else (parIda.second to parIda.first)
    }
}
