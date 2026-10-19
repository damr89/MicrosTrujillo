package com.example.microstrujillo.domain

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import org.osmdroid.util.GeoPoint
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object OsrmRoutingEngine {

    private data class OsrmResponse(
        @SerializedName("routes") val routes: List<OsrmRoute>?
    )

    private data class OsrmRoute(
        @SerializedName("geometry") val geometry: OsrmGeometry?,
        @SerializedName("distance") val distance: Double?
    )

    private data class OsrmGeometry(
        @SerializedName("coordinates") val coordinates: List<List<Double>>?
    )

    suspend fun obtenerRutaPorPistas(
        puntos: List<GeoPoint>,
        modo: String = "driving"
    ): Pair<List<GeoPoint>, Double> = withContext(Dispatchers.IO) {
        if (puntos.size < 2) return@withContext Pair(puntos, 0.0)

        try {
            val profile = if (modo == "foot") "foot" else "driving"
            
            // Formateamos todos los waypoints intermedios de la ruta
            val coordString = puntos.joinToString(";") { "${it.longitude},${it.latitude}" }
            val urlString = "https://router.project-osrm.org/route/v1/$profile/$coordString?overview=full&geometries=geojson"
            
            val connection = (URL(urlString).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 6000
                readTimeout = 6000
                setRequestProperty("User-Agent", "MicrosTrujilloAppEstudiantil/1.0 (universidad_trujillo_mobile_app@gmail.com)")
            }

            if (connection.responseCode == 200) {
                val jsonText = connection.inputStream.bufferedReader().readText()
                val response = Gson().fromJson(jsonText, OsrmResponse::class.java)
                val route = response?.routes?.firstOrNull()
                val coords = route?.geometry?.coordinates
                val dist = route?.distance ?: calcularDistanciaDirecta(puntos)

                if (!coords.isNullOrEmpty()) {
                    val resultadoPistas = coords.map { coord ->
                        GeoPoint(coord[1], coord[0])
                    }
                    return@withContext Pair(resultadoPistas, dist)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Fallback directo
        return@withContext Pair(puntos, calcularDistanciaDirecta(puntos))
    }

    private fun calcularDistanciaDirecta(puntos: List<GeoPoint>): Double {
        var dist = 0.0
        for (i in 0 until puntos.size - 1) {
            dist += puntos[i].distanceToAsDouble(puntos[i + 1])
        }
        return dist
    }
}
