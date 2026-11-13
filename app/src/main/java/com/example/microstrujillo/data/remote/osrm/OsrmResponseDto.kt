package com.example.microstrujillo.data.remote.osrm

import com.google.gson.annotations.SerializedName

/**
 * Contrato JSON de la API REST OSRM (Semana 08)
 * Deserializado automáticamente mediante Retrofit 2 + Gson Converter
 */
data class OsrmResponseDto(
    @SerializedName("code") val code: String? = null,
    @SerializedName("routes") val routes: List<OsrmRouteDto>? = null
)

data class OsrmRouteDto(
    @SerializedName("geometry") val geometry: OsrmGeometryDto? = null,
    @SerializedName("weight_name") val weightName: String? = null,
    @SerializedName("weight") val weight: Double? = null,
    @SerializedName("duration") val duration: Double? = null,
    @SerializedName("distance") val distance: Double? = null
)

data class OsrmGeometryDto(
    @SerializedName("type") val type: String? = null,
    @SerializedName("coordinates") val coordinates: List<List<Double>>? = null
)
