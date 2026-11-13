package com.example.microstrujillo.domain

import com.example.microstrujillo.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.osmdroid.util.GeoPoint

/**
 * Consulta de trayectos viales exactos consumiendo la API OSRM con Retrofit 2 (Semana 08).
 */
object RouteGeometryFetcher {

    suspend fun obtenerRecorridoExactoPorCalles(puntosClave: List<GeoPoint>): List<GeoPoint> = withContext(Dispatchers.IO) {
        if (puntosClave.size < 2) return@withContext puntosClave

        try {
            // Formato OSRM: lon1,lat1;lon2,lat2;lon3,lat3
            val coordString = puntosClave.joinToString(";") { "${it.longitude},${it.latitude}" }

            // Consumo mediante el cliente Retrofit 2
            val responseDto = RetrofitClient.osrmApi.obtenerGeometriaRuta(
                profile = "driving",
                coordinates = coordString,
                overview = "full",
                geometries = "geojson"
            )

            val coords = responseDto.routes?.firstOrNull()?.geometry?.coordinates
            if (!coords.isNullOrEmpty()) {
                return@withContext coords.map { coord ->
                    // OSRM GeoJSON retorna [longitud, latitud], OSMDroid requiere (latitud, longitud)
                    GeoPoint(coord[1], coord[0])
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Retorna puntos clave originales si hay algún problema de red
        return@withContext puntosClave
    }
}
