package com.example.microstrujillo.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.microstrujillo.model.RouteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RouteDao {

    // Esta función sirve para traer TODAS las rutas guardadas para mostrarlas en pantalla
    @Query("SELECT * FROM tabla_rutas")
    fun obtenerTodasLasRutas(): Flow<List<RouteEntity>>

    @Query("SELECT * FROM tabla_rutas")
    suspend fun obtenerTodasLasRutasDirecto(): List<RouteEntity>

    // Esta función sirve para guardar una lista de rutas nuevas en el celular
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarRutas(rutas: List<RouteEntity>)

    // Esta función borra todo (útil para cuando se actualiza el catálogo)
    @Query("DELETE FROM tabla_rutas")
    suspend fun limpiarRutas()
}