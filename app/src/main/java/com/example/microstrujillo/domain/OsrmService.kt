package com.example.microstrujillo.domain

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.osmdroid.util.GeoPoint
import java.net.HttpURLConnection
import java.net.URL

object OsrmService {
    private data class OsrmResponse(
        @SerializedName("routes") val routes: List<OsrmRoute>?
    )

    private data class OsrmRoute(
        @SerializedName("geometry") val geometry: OsrmGeometry?
    )

    private data class OsrmGeometry(
        @SerializedName("coordinates") val coordinates: List<List<Double>>?
    )

    /**
     * Utiliza el motor OSRM localmente (o la API gratuita global en su defecto)
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
            
            // "continue_straight=true" o "false"
            // "geometries=geojson" para que devuelva el track exacto del asfalto.
            val url = "https://router.project-osrm.org/route/v1/$profile/$coords?overview=full&geometries=geojson&continue_straight=false"
            
            try {
                val connection = (URL(url).openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    connectTimeout = 5000
                    readTimeout = 5000
                    setRequestProperty("User-Agent", "MicrosTrujilloApp")
                }
                
                if (connection.responseCode == 200) {
                    val json = connection.inputStream.bufferedReader().readText()
                    val resp = Gson().fromJson(json, OsrmResponse::class.java)
                    val geom = resp.routes?.firstOrNull()?.geometry?.coordinates
                    
                    if (geom != null) {
                        // GeoJSON retorna [longitud, latitud], OSMDroid requiere (latitud, longitud)
                        val parsed = geom.map { GeoPoint(it[1], it[0]) }
                        if (i == 0) resultShape.addAll(parsed) else resultShape.addAll(parsed.drop(1))
                    } else {
                        if (i == 0) resultShape.addAll(chunk) else resultShape.addAll(chunk.drop(1))
                    }
                } else {
                    if (i == 0) resultShape.addAll(chunk) else resultShape.addAll(chunk.drop(1))
                }
            } catch (e: Exception) {
                if (i == 0) resultShape.addAll(chunk) else resultShape.addAll(chunk.drop(1))
            }
            i += chunkSize - 1 // Avanzamos, pero repetimos el último punto para conectar el siguiente bloque
        }
        
        return@withContext resultShape
    }
}
