package com.example.microstrujillo.data

import com.example.microstrujillo.model.MovimientoEntity
import com.example.microstrujillo.model.RouteEntity
import com.example.microstrujillo.model.SaldoEntity
import kotlinx.coroutines.flow.Flow

class RouteRepository(
    private val routeDao: RouteDao,
    private val movimientoDao: MovimientoDao
) {

    // --- RUTAS ---
    val todasLasRutas: Flow<List<RouteEntity>> = routeDao.obtenerTodasLasRutas()

    suspend fun obtenerTodasLasRutasDirecto(): List<RouteEntity> {
        return routeDao.obtenerTodasLasRutasDirecto()
    }

    suspend fun guardarRutaPrueba(rutas: List<RouteEntity>) {
        routeDao.guardarRutas(rutas)
    }

    suspend fun limpiarRutas() {
        routeDao.limpiarRutas()
    }

    // --- BILLETERA NFC E HISTORIAL (SQLITE) ---
    val saldoFlow: Flow<Double?> = movimientoDao.obtenerSaldoFlow()
    val movimientosFlow: Flow<List<MovimientoEntity>> = movimientoDao.obtenerMovimientosFlow()

    suspend fun obtenerSaldoActual(): Double {
        return movimientoDao.obtenerSaldoDirecto() ?: 10.0
    }

    suspend fun inicializarSaldoSiNoExiste(fechaHoraActual: String) {
        val saldoExistente = movimientoDao.obtenerSaldoDirecto()
        if (saldoExistente == null) {
            val saldoInicial = 10.0
            movimientoDao.guardarSaldo(SaldoEntity(id = 1, saldoActual = saldoInicial))
            movimientoDao.insertarMovimiento(
                MovimientoEntity(
                    tipo = "RECARGA",
                    titulo = "Saldo inicial de bienvenida",
                    detalle = "Activación Tarjeta Trujillo Pass",
                    monto = saldoInicial,
                    fechaHora = fechaHoraActual,
                    saldoResultante = saldoInicial
                )
            )
        }
    }

    suspend fun registrarRecarga(monto: Double, fechaHora: String): Double {
        val saldoActual = movimientoDao.obtenerSaldoDirecto() ?: 10.0
        val nuevoSaldo = saldoActual + monto
        movimientoDao.guardarSaldo(SaldoEntity(id = 1, saldoActual = nuevoSaldo))
        movimientoDao.insertarMovimiento(
            MovimientoEntity(
                tipo = "RECARGA",
                titulo = "Recarga en Billetera",
                detalle = "Recarga digital sin contacto",
                monto = monto,
                fechaHora = fechaHora,
                saldoResultante = nuevoSaldo
            )
        )
        return nuevoSaldo
    }

    suspend fun registrarPagoPasajeNFC(
        tarifa: Double,
        lineaDetalle: String,
        fechaHora: String
    ): Result<Double> {
        val saldoActual = movimientoDao.obtenerSaldoDirecto() ?: 10.0
        if (saldoActual < tarifa) {
            return Result.failure(IllegalStateException("Saldo insuficiente"))
        }
        val nuevoSaldo = saldoActual - tarifa
        movimientoDao.guardarSaldo(SaldoEntity(id = 1, saldoActual = nuevoSaldo))
        movimientoDao.insertarMovimiento(
            MovimientoEntity(
                tipo = "PAGO_NFC",
                titulo = "Pago de pasaje (NFC)",
                detalle = lineaDetalle,
                monto = -tarifa,
                fechaHora = fechaHora,
                saldoResultante = nuevoSaldo
            )
        )
        return Result.success(nuevoSaldo)
    }
}