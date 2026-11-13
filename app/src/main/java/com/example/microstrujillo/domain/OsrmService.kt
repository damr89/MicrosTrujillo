package com.example.microstrujillo.domain

import com.example.microstrujillo.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.osmdroid.util.GeoPoint

/**
 * Servicio de ruteo y ajuste vial que consume la API REST OSRM mediante Retrofit 2 (Semana 08).
 * Elimina HttpURLConnection y estandariza las llamadas bajo el patrón cliente-servicio Retrofit.
 */
object OsrmService {

    /**
     * Utiliza la API REST OSRM consumida con Retrofit 2
     * para forzar que los puntos (por ejemplo, cuadras rectas) se peguen a la malla vial real de OpenStreetMap.
     */
    suspend fun fetchRouteGeometry(points: List<GeoPoint>, profile: String = "driving"): List<GeoPoint> = withContext(Dispatchers.IO) {
        if (points.size < 2) return@withContext points

        val resultShape = mutableListOf<GeoPoint>()

        // Reducimos el chunkSize para asegurar que OSRM acepte la petición HTTP (URL length limits)
        val chunkSize = 15
        var i = 0

        while (i < points.size - 1) {
            val chunk = mutableListOf<GeoPoint>()
            val endIdx = Math.min(i + chunkSize, points.size)
            for (j in i until endIdx) {
                chunk.add(points[j])
            }

            // OSRM Format: lon,lat;lon,lat...
            val coords = chunk.joinToString(";") { "${it.longitude},${it.latitude}" }

            try {
                // Consumo formal de la API REST mediante la interfaz Retrofit 2
                val responseDto = RetrofitClient.osrmApi.obtenerGeometriaRuta(
                    profile = profile,
                    coordinates = coords,
                    overview = "full",
                    geometries = "geojson",
                    continueStraight = false
                )

                val geom = responseDto.routes?.firstOrNull()?.geometry?.coordinates
                if (!geom.isNullOrEmpty()) {
                    // GeoJSON retorna [longitud, latitud], OSMDroid requiere (latitud, longitud)
                    val parsed = geom.map { GeoPoint(it[1], it[0]) }
                    if (i == 0) resultShape.addAll(parsed) else resultShape.addAll(parsed.drop(1))
                } else {
                    if (i == 0) resultShape.addAll(chunk) else resultShape.addAll(chunk.drop(1))
                }
            } catch (e: Exception) {
                // Resiliencia: si hay error de red o timeout, se preservan los puntos originales
                if (i == 0) resultShape.addAll(chunk) else resultShape.addAll(chunk.drop(1))
            }
            i += chunkSize - 1 // Conectamos el siguiente bloque
        }

        return@withContext resultShape
    }
}
