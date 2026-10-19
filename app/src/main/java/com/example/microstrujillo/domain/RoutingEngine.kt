package com.example.microstrujillo.domain

import com.example.microstrujillo.model.RouteEntity
import org.osmdroid.util.GeoPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

data class RutaCalculada(
    val routeEntity: RouteEntity,
    val paraderoSubida: GeoPoint,
    val paraderoBajada: GeoPoint,
    val caminataInicio: List<GeoPoint>, 
    val viajeMicro: List<GeoPoint>,     
    val caminataFin: List<GeoPoint>,    
    val distanciaCaminataTotal: Double,
    val distanciaMicroTotal: Double,
    val tiempoTotalMinutos: Int
)

object RoutingEngine {

    suspend fun calcularMejorRuta(
        origenUsuario: GeoPoint,
        destinoUsuario: GeoPoint,
        todasLasRutas: List<RouteEntity>
    ): RutaCalculada? = withContext(Dispatchers.Default) {
        val rutasAProcesar = todasLasRutas

        if (rutasAProcesar.isEmpty()) return@withContext null

        var mejorRuta: RutaCalculada? = null
        var menorTiempoDeViaje = Double.MAX_VALUE

        for (ruta in rutasAProcesar) {
            val shapePoints = if (ruta.shapePoints.isNotEmpty()) ruta.shapePoints else ruta.recorrido
            if (shapePoints.size < 2) continue

            val waypoints = shapePoints.map { RoutePoint(it.latitude, it.longitude) }
            val busRoute = BusRoute(
                id = ruta.id.toString(),
                name = ruta.nombreLinea,
                waypoints = waypoints
            )

            // El nuevo motor inteligente buscará el mejor tramo posible dentro de la ruta
            val segmentoPts = TransitEngine.findBestRouteSegment(
                route = busRoute,
                originLat = origenUsuario.latitude,
                originLng = origenUsuario.longitude,
                destLat = destinoUsuario.latitude,
                destLng = destinoUsuario.longitude
            )

            if (segmentoPts != null && segmentoPts.size >= 2) {
                val subidaPt = segmentoPts.first()
                val bajadaPt = segmentoPts.last()

                val subidaGeo = GeoPoint(subidaPt.lat, subidaPt.lng)
                val bajadaGeo = GeoPoint(bajadaPt.lat, bajadaPt.lng)

                val distCam1 = TransitEngine.distanceMeters(
                    origenUsuario.latitude, origenUsuario.longitude,
                    subidaPt.lat, subidaPt.lng
                )
                val distCam2 = TransitEngine.distanceMeters(
                    bajadaPt.lat, bajadaPt.lng,
                    destinoUsuario.latitude, destinoUsuario.longitude
                )
                val caminataTotal = distCam1 + distCam2

                var distMicro = 0.0
                for (i in 0 until segmentoPts.size - 1) {
                    distMicro += TransitEngine.distanceMeters(
                        segmentoPts[i].lat, segmentoPts[i].lng,
                        segmentoPts[i + 1].lat, segmentoPts[i + 1].lng
                    )
                }

                // --- ALGORITMO DE COSTO DE TIEMPO HUMANO (ETA Real) ---
                // Velocidad peatón: ~1.2 m/s -> 72 metros por minuto
                val tiempoCaminandoMin = caminataTotal / 72.0
                // Velocidad micro: ~5.0 m/s (18 km/h en tráfico) -> 300 metros por minuto
                val tiempoEnMicroMin = distMicro / 300.0
                // Tiempo de espera en el paradero
                val tiempoEsperaMin = ruta.tiempoEspera.toDouble()
                
                val tiempoTotalCalculado = tiempoCaminandoMin + tiempoEnMicroMin + tiempoEsperaMin

                // Penalización por cansancio: Caminar duele más que estar sentado en el micro. Multiplicamos la caminata x 2
                val puntuacionCompetitiva = (tiempoCaminandoMin * 2.0) + tiempoEnMicroMin + tiempoEsperaMin

                if (puntuacionCompetitiva < menorTiempoDeViaje) {
                    menorTiempoDeViaje = puntuacionCompetitiva

                    val viajeMicroBruto = segmentoPts.map { GeoPoint(it.lat, it.lng) }
                    
                    val viajeMicroPerfecto = viajeMicroBruto 
                    val caminataInicioPerfecta = listOf(origenUsuario, subidaGeo)
                    val caminataFinPerfecta = listOf(bajadaGeo, destinoUsuario)

                    mejorRuta = RutaCalculada(
                        routeEntity = ruta,
                        paraderoSubida = subidaGeo,
                        paraderoBajada = bajadaGeo,
                        caminataInicio = caminataInicioPerfecta,
                        viajeMicro = viajeMicroPerfecto,
                        caminataFin = caminataFinPerfecta,
                        distanciaCaminataTotal = caminataTotal,
                        distanciaMicroTotal = distMicro,
                        tiempoTotalMinutos = Math.max(1, tiempoTotalCalculado.roundToInt())
                    )
                }
            }
        }

        return@withContext mejorRuta
    }
}
