package com.example.microstrujillo.data

import com.example.microstrujillo.model.RouteEntity
import kotlinx.coroutines.flow.Flow

class RouteRepository(private val routeDao: RouteDao) {

    // El frontend llamará a esto para obtener la lista de micros
    val todasLasRutas: Flow<List<RouteEntity>> = routeDao.obtenerTodasLasRutas()

    suspend fun obtenerTodasLasRutasDirecto(): List<RouteEntity> {
        return routeDao.obtenerTodasLasRutasDirecto()
    }

    // Usaremos esto para inyectar datos falsos por ahora y que se vean en pantalla
    suspend fun guardarRutaPrueba(rutas: List<RouteEntity>) {
        routeDao.guardarRutas(rutas)
    }

    // Borra todas las rutas de la base de datos
    suspend fun limpiarRutas() {
        routeDao.limpiarRutas()
    }
}