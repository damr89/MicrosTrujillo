package com.example.microstrujillo.data.remote.osrm

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Interfaz Retrofit 2 para el consumo de la API REST de ruteo OSRM (Semana 08).
 * Define endpoints HTTP y parámetros con tipado estricto.
 */
interface OsrmApiService {

    @GET("route/v1/{profile}/{coordinates}")
    suspend fun obtenerGeometriaRuta(
        @Path("profile") profile: String = "driving",
        @Path(value = "coordinates", encoded = true) coordinates: String,
        @Query("overview") overview: String = "full",
        @Query("geometries") geometries: String = "geojson",
        @Query("continue_straight") continueStraight: Boolean = false,
        @Header("User-Agent") userAgent: String = "MicrosTrujilloAppEstudiantil/1.0 (universidad_trujillo_mobile_app@gmail.com)"
    ): OsrmResponseDto
}
