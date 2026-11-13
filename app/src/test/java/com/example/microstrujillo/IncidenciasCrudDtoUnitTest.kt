package com.example.microstrujillo

import com.example.microstrujillo.data.remote.IncidenciaDto
import com.example.microstrujillo.data.remote.osrm.OsrmResponseDto
import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pruebas unitarias para contratos JSON y DTOs de Retrofit 2 (Semana 08, 09 y 12)
 */
class IncidenciasCrudDtoUnitTest {

    private val gson = Gson()

    @Test
    fun incidenciaDto_serializacionJson_generaContratoCorrecto() {
        val dto = IncidenciaDto(
            id = 105,
            titulo = "Nuevo California (Línea V) - Demora",
            cuerpo = "Tráfico por obras en Av. España",
            usuarioId = 1
        )

        val json = gson.toJson(dto)

        assertTrue(json.contains("\"id\":105"))
        assertTrue(json.contains("\"title\":\"Nuevo California (Línea V) - Demora\""))
        assertTrue(json.contains("\"body\":\"Tráfico por obras en Av. España\""))
        assertTrue(json.contains("\"userId\":1"))
    }

    @Test
    fun incidenciaDto_deserializacionJson_mapeaCamposCorrectamente() {
        val jsonMock = """
            {
                "id": 42,
                "title": "Nuevo California (Línea A) - Desvío",
                "body": "Desvío provisional por Av. Mansiche",
                "userId": 2
            }
        """.trimIndent()

        val dto = gson.fromJson(jsonMock, IncidenciaDto::class.java)

        assertEquals(42, dto.id)
        assertEquals("Nuevo California (Línea A) - Desvío", dto.titulo)
        assertEquals("Desvío provisional por Av. Mansiche", dto.cuerpo)
        assertEquals(2, dto.usuarioId)
    }

    @Test
    fun osrmResponseDto_deserializacionGeoJson_parseaGeometriaVialCorrectamente() {
        val jsonOsrmMock = """
            {
                "code": "Ok",
                "routes": [
                    {
                        "geometry": {
                            "type": "LineString",
                            "coordinates": [
                                [-79.0258, -8.11599],
                                [-79.0280, -8.11800],
                                [-79.0305, -8.12100]
                            ]
                        },
                        "distance": 850.5,
                        "duration": 180.2
                    }
                ]
            }
        """.trimIndent()

        val response = gson.fromJson(jsonOsrmMock, OsrmResponseDto::class.java)

        assertEquals("Ok", response.code)
        assertNotNull(response.routes)
        val routes = response.routes!!
        assertEquals(1, routes.size)

        val ruta = routes.first()
        assertEquals(850.5, ruta.distance!!, 0.01)
        assertEquals(180.2, ruta.duration!!, 0.01)

        val coords = ruta.geometry?.coordinates
        assertNotNull(coords)
        assertEquals(3, coords!!.size)

        // En GeoJSON OSRM: [longitud, latitud]
        assertEquals(-79.0258, coords[0][0], 0.0001)
        assertEquals(-8.11599, coords[0][1], 0.0001)
    }
}
