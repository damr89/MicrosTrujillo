package com.example.microstrujillo.domain

import android.content.Context
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import org.osmdroid.util.GeoPoint

object GeoJsonParser {

    private data class GeoJsonFeature(
        @SerializedName("type") val type: String?,
        @SerializedName("geometry") val geometry: GeoJsonGeometry?
    )

    private data class GeoJsonGeometry(
        @SerializedName("type") val type: String?,
        @SerializedName("coordinates") val coordinates: List<List<Double>>?
    )

    /**
     * Lee un archivo GeoJSON desde app/src/main/assets/routes/ y extrae la lista de GeoPoint (lat, lon)
     */
    fun cargarGeoJsonDesdeAssets(context: Context, rutaArchivoAsset: String): List<GeoPoint> {
        try {
            val jsonString = context.assets.open(rutaArchivoAsset).bufferedReader().use { it.readText() }
            return parsearGeoJsonString(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return emptyList()
    }

    fun parsearGeoJsonString(jsonString: String): List<GeoPoint> {
        try {
            val feature = Gson().fromJson(jsonString, GeoJsonFeature::class.java)
            val rawCoords = feature?.geometry?.coordinates
            if (!rawCoords.isNullOrEmpty()) {
                // GeoJSON viene como [longitud, latitud]. Invertimos a GeoPoint(lat, lon)
                return rawCoords.map { coord ->
                    val lon = coord[0]
                    val lat = coord[1]
                    GeoPoint(lat, lon)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return emptyList()
    }
}
