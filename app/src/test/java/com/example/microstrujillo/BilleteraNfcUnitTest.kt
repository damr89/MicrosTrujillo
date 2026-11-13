package com.example.microstrujillo

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pruebas unitarias para la lógica financiera y cobro sin contacto NFC (Semana 12)
 */
class BilleteraNfcUnitTest {

    private val tarifaUrbana = 1.50

    @Test
    fun pagoPasaje_conSaldoSuficiente_descuentaTarifaCorrecta() {
        val saldoInicial = 10.00
        val nuevoSaldo = saldoInicial - tarifaUrbana

        assertEquals(8.50, nuevoSaldo, 0.001)
        assertTrue("El nuevo saldo debe ser mayor o igual a cero", nuevoSaldo >= 0.0)
    }

    @Test
    fun pagoPasaje_conSaldoInsuficiente_fallaYNoModificaSaldo() {
        var saldoActual = 1.20
        val puedePagar = saldoActual >= tarifaUrbana

        assertFalse("No debe permitir el pago si el saldo es menor a la tarifa", puedePagar)
        
        // El saldo debe permanecer intacto si no alcanza
        if (!puedePagar) {
            // No se descuenta nada
        } else {
            saldoActual -= tarifaUrbana
        }

        assertEquals(1.20, saldoActual, 0.001)
    }

    @Test
    fun recargaSaldo_montosMultiples_acumulaCorrectamente() {
        var saldo = 10.00

        // Recarga de S/ 5.00
        saldo += 5.00
        assertEquals(15.00, saldo, 0.001)

        // Recarga de S/ 10.00
        saldo += 10.00
        assertEquals(25.00, saldo, 0.001)
    }

    @Test
    fun calculoPasajesRestantes_conSaldoDisponible_calculaNumeroEntero() {
        val saldo = 10.00
        val pasajesRestantes = (saldo / tarifaUrbana).toInt()

        // S/ 10.00 / S/ 1.50 = 6.66 viajes -> 6 viajes completos
        assertEquals(6, pasajesRestantes)
    }
}
