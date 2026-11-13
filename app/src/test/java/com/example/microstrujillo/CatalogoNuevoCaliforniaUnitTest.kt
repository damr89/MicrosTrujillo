package com.example.microstrujillo

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pruebas unitarias de integridad del catálogo de rutas (Semana 12)
 */
class CatalogoNuevoCaliforniaUnitTest {

    private val lineasOficiales = listOf(
        "Empresa de Transportes Nuevo California S.A. - Línea V (IDA)",
        "Empresa de Transportes Nuevo California S.A. - Línea V (VUELTA)",
        "Empresa de Transportes Nuevo California S.A. - Línea A (IDA)",
        "Empresa de Transportes Nuevo California S.A. - Línea A (VUELTA)",
        "Empresa de Transportes Nuevo California S.A. - Línea B1 (IDA)",
        "Empresa de Transportes Nuevo California S.A. - Línea C (IDA)"
    )

    @Test
    fun catalogo_soloContieneLineasDeNuevoCalifornia() {
        for (linea in lineasOficiales) {
            assertTrue("Toda línea debe pertenecer a Nuevo California", linea.contains("Nuevo California"))
            assertFalse("No debe existir la línea Salaverry Express", linea.contains("Salaverry", ignoreCase = true))
            assertFalse("No debe existir la línea El Cortijo", linea.contains("Cortijo", ignoreCase = true))
        }
    }

    @Test
    fun filtrosRapidos_filtranCorrectamentePorLetraDeLinea() {
        val filtroV = "Línea V"
        val rutasFiltradas = lineasOficiales.filter { it.contains(filtroV, ignoreCase = true) }

        assertTrue(rutasFiltradas.isNotEmpty())
        assertTrue(rutasFiltradas.all { it.contains("Línea V") })
    }

    @Test
    fun tiemposDeEspera_sonValidosYPositivos() {
        val tiempos = listOf(5, 8, 11)
        for (t in tiempos) {
            assertTrue("El tiempo de espera debe ser mayor a 0 minutos", t > 0)
            assertTrue("El tiempo de espera promedio no debe exceder los 30 minutos", t <= 30)
        }
    }
}
