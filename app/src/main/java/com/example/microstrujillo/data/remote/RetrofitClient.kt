package com.example.microstrujillo.data.remote

import com.example.microstrujillo.data.remote.osrm.OsrmApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Cliente Singleton de Retrofit 2 con conversor Gson.
 * Administra los servicios web REST del proyecto:
 * 1. API de Alertas e Incidencias (CRUD de transporte - Semana 09)
 * 2. API de Ruteo OSRM (Trazado y ajuste vial de micros en Trujillo - Semana 08)
 */
object RetrofitClient {

    private const val BASE_URL_JSONPLACEHOLDER = "https://jsonplaceholder.typicode.com/"
    private const val BASE_URL_OSRM = "https://router.project-osrm.org/"

    val incidenciasApi: IncidenciasApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_JSONPLACEHOLDER)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(IncidenciasApiService::class.java)
    }

    val osrmApi: OsrmApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_OSRM)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OsrmApiService::class.java)
    }
}
