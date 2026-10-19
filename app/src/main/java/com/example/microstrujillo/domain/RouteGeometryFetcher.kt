package com.example.microstrujillo.domain

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import org.osmdroid.util.GeoPoint
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object RouteGeometryFetcher {

    private data class OsrmResponse(
        @SerializedName("routes") val routes: List<OsrmRoute>?
    )

    private data class OsrmRoute(
        @SerializedName("geometry") val geometry: OsrmGeometry?
    )

    private data class OsrmGeometry(
        @SerializedName("coordinates") val coordinates: List<List<Double>>?
    )

    suspend fun obtenerRecorridoExactoPorCalles(puntosClave: List<GeoPoint>): List<GeoPoint> = withContext(Dispatchers.IO) {
        if (puntosClave.size < 2) return@withContext puntosClave

        try {
            // Formato OSRM: lon1,lat1;lon2,lat2;lon3,lat3
            val coordString = puntosClave.joinToString(";") { "${it.longitude},${it.latitude}" }
            val urlString = "https://router.project-osrm.org/route/v1/driving/$coordString?overview=full&geometries=geojson"
            
            val connection = (URL(urlString).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 6000
                readTimeout = 6000
                setRequestProperty("User-Agent", "MicrosTrujilloAppEstudiantil/1.0 (universidad_trujillo_mobile_app@gmail.com)")
            }

            if (connection.responseCode == 200) {
                val jsonText = connection.inputStream.bufferedReader().readText()
                val response = Gson().fromJson(jsonText, OsrmResponse::class.java)
                val coords = response?.routes?.firstOrNull()?.geometry?.coordinates

                if (!coords.isNullOrEmpty()) {
                    return@withContext coords.map { coord ->
                        // OSRM GeoJSON retorna [longitud, latitud]
                        GeoPoint(coord[1], coord[0])
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Retorna puntos clave originales si hay algún problema de red
        return@withContext puntosClave
    }
}
