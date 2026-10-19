package com.example.microstrujillo.domain

import android.content.Context
import android.location.Location
import com.google.gson.Gson
import org.osmdroid.util.GeoPoint

object SmartBusEngine {

    fun distanceMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0].toDouble()
    }

    fun cargarRutasDesdeAssets(context: Context): List<SmartBusJson> {
        val rutas = mutableListOf<SmartBusJson>()
        val gson = Gson()
        
        // Rutas descargadas y pre-curvadas con OSRM a JSON
        val archivosRutas = listOf(
            "routes/nuevo_california_v_ida.json",
            "routes/nuevo_california_v_vuelta.json",
            "routes/nuevo_california_b1_ida.json",
            "routes/nuevo_california_b1_vuelta.json",
            "routes/nuevo_california_c_ida.json",
            "routes/nuevo_california_c_vuelta.json",
            "routes/nuevo_california_a_ida.json",
            "routes/nuevo_california_a_vuelta.json",
            "routes/nuevo_california_bc_ida.json",
            "routes/nuevo_california_bc_vuelta.json",
            "routes/nuevo_california_lv_ida.json",
            "routes/nuevo_california_lv_vuelta.json"
        )

        for (path in archivosRutas) {
            try {
                val content = context.assets.open(path).bufferedReader().use { it.readText() }
                val rutaJson = gson.fromJson(content, SmartBusJson::class.java)
                if (rutaJson != null && !rutaJson.stops.isNullOrEmpty()) {
                    rutas.add(rutaJson)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return rutas
    }
}
