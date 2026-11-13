package com.example.microstrujillo.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Endpoints REST para operaciones CRUD completas (Semana 08 y 09)
 */
interface IncidenciasApiService {

    // R - READ (Lectura de alertas en Trujillo)
    @GET("posts")
    suspend fun obtenerIncidencias(): List<IncidenciaDto>

    // C - CREATE (Publicación de nueva incidencia o reporte ciudadano)
    @POST("posts")
    suspend fun crearIncidencia(
        @Body incidencia: IncidenciaDto
    ): IncidenciaDto

    // U - UPDATE (Actualización o confirmación del estado de la incidencia)
    @PUT("posts/{id}")
    suspend fun actualizarIncidencia(
        @Path("id") id: Int,
        @Body incidencia: IncidenciaDto
    ): IncidenciaDto

    // D - DELETE (Eliminación de la incidencia resuelta)
    @DELETE("posts/{id}")
    suspend fun eliminarIncidencia(
        @Path("id") id: Int
    ): Response<Unit>
}
