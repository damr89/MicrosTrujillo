package com.example.microstrujillo.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.microstrujillo.model.MovimientoEntity
import com.example.microstrujillo.model.SaldoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovimientoDao {

    @Query("SELECT saldoActual FROM tabla_saldo WHERE id = 1 LIMIT 1")
    fun obtenerSaldoFlow(): Flow<Double?>

    @Query("SELECT saldoActual FROM tabla_saldo WHERE id = 1 LIMIT 1")
    suspend fun obtenerSaldoDirecto(): Double?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarSaldo(saldoEntity: SaldoEntity)

    @Query("SELECT * FROM tabla_movimientos ORDER BY id DESC")
    fun obtenerMovimientosFlow(): Flow<List<MovimientoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarMovimiento(movimiento: MovimientoEntity)
}