package com.example.microstrujillo.domain

import kotlin.math.*

data class RoutePoint(val lat: Double, val lng: Double)

data class BusRoute(
    val id: String,
    val name: String,
    val waypoints: List<RoutePoint>
)

object TransitEngine {

    // Fórmula Haversine ultrarrápida para procesar miles de puntos en milisegundos
    fun distanceMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6371000.0 // radios de la tierra en metros
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadius * c
    }

    fun findBestRouteSegment(
        route: BusRoute,
        originLat: Double,
        originLng: Double,
        destLat: Double,
        destLng: Double
    ): List<RoutePoint>? {
        val waypoints = route.waypoints
        if (waypoints.size < 2) return null

        val distToOrigin = DoubleArray(waypoints.size)
        val distToDest = DoubleArray(waypoints.size)

        // 1. Calculamos la distancia de CADA punto del micro hacia tu origen y tu destino
        for (i in waypoints.indices) {
            val pt = waypoints[i]
            distToOrigin[i] = distanceMeters(originLat, originLng, pt.lat, pt.lng)
            distToDest[i] = distanceMeters(destLat, destLng, pt.lat, pt.lng)
        }

        // 2. Filtramos solo las paradas que están a menos de 1500m caminando
        val validOrigins = mutableListOf<Int>()
        val validDests = mutableListOf<Int>()

        for (i in waypoints.indices) {
            if (distToOrigin[i] <= 1500.0) validOrigins.add(i)
            if (distToDest[i] <= 1500.0) validDests.add(i)
        }

        if (validOrigins.isEmpty() || validDests.isEmpty()) return null

        var bestO = -1
        var bestD = -1
        var minScore = Double.MAX_VALUE

        // 3. Evaluamos TODAS las combinaciones de subida y bajada (Garantiza nunca fallar con bucles)
        for (o in validOrigins) {
            for (d in validDests) {
                // Regla de Oro: Solo puedes bajar DESPUÉS de haber subido (Sentido direccional)
                if (o < d) {
                    val walkDist = distToOrigin[o] + distToDest[d]
                    
                    // Aproximación rápida de la distancia del bus basada en la cantidad de cuadras
                    val busDistProxy = (d - o) * 15.0 
                    
                    // Algoritmo: Valoramos que caminar cansa 5 VECES MÁS que estar sentado en el micro.
                    val score = (walkDist * 5.0) + busDistProxy
                    
                    if (score < minScore) {
                        minScore = score
                        bestO = o
                        bestD = d
                    }
                }
            }
        }

        if (bestO == -1 || bestD == -1) return null

        // 4. Devolvemos el tramo ganador
        return waypoints.subList(bestO, bestD + 1)
    }
}
