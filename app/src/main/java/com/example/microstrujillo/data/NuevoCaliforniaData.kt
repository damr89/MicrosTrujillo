package com.example.microstrujillo.data

import android.content.Context
import com.example.microstrujillo.domain.SmartBusEngine
import com.example.microstrujillo.model.RouteEntity
import com.example.microstrujillo.model.Stop
import org.osmdroid.util.GeoPoint

object NuevoCaliforniaData {

    fun obtenerRutas(context: Context): List<RouteEntity> {
        val listaFinal = mutableListOf<RouteEntity>()
        var autoId = 1

        // 1. Cargamos las 12 rutas pre-calculadas de Nuevo California desde assets/routes/
        val smartBusList = SmartBusEngine.cargarRutasDesdeAssets(context)
        if (smartBusList.isNotEmpty()) {
            val rutasNuevoCalifornia = smartBusList.map { sb ->
                val stops = sb.stops.map { Stop(it.name, GeoPoint(it.lat, it.lng)) }
                val shapePoints = if (!sb.shape.isNullOrEmpty()) {
                    sb.shape.map { GeoPoint(it.lat, it.lng) }
                } else {
                    sb.stops.map { GeoPoint(it.lat, it.lng) }
                }

                // Nombres descriptivos de origen y destino según la línea y sentido
                val (origenReal, destinoReal) = obtenerExtremosDescriptivos(sb.letter, sb.direction)

                RouteEntity(
                    id = autoId++,
                    nombreLinea = "${sb.company} - Línea ${sb.letter} (${sb.direction})",
                    origen = origenReal,
                    destino = destinoReal,
                    tiempoEspera = when (sb.letter) {
                        "V", "A" -> 5
                        "B1", "C" -> 8
                        else -> 11
                    },
                    stops = stops,
                    shapePoints = shapePoints,
                    recorrido = shapePoints
                )
            }
            listaFinal.addAll(rutasNuevoCalifornia)
        }

        // 2. Agregamos rutas representativas de otras empresas de Trujillo usando TrujilloCuadraShape
        val shapeSalaverry = TrujilloCuadraShape.generarTrazadoCuadraPorCuadra(
            TrujilloCuadraShape.americaSurGrauCuadras +
                    TrujilloCuadraShape.americaNorteMochicaCuadras.drop(1) +
                    TrujilloCuadraShape.esperanzaCuadras.drop(1)
        )
        listaFinal.add(
            RouteEntity(
                id = autoId++,
                nombreLinea = "Salaverry Express S.A. - M-20 C (IDA)",
                origen = "Salaverry / Óvalo Larco",
                destino = "La Esperanza / Nuevo Horizonte",
                tiempoEspera = 6,
                stops = listOf(
                    Stop("Óvalo Larco", TrujilloCuadraShape.americaSurGrauCuadras.first()),
                    Stop("Óvalo Grau", TrujilloCuadraShape.americaSurGrauCuadras.last()),
                    Stop("Óvalo Mochica", TrujilloCuadraShape.americaNorteMochicaCuadras.last()),
                    Stop("Terminal La Esperanza", TrujilloCuadraShape.esperanzaCuadras.last())
                ),
                shapePoints = shapeSalaverry,
                recorrido = shapeSalaverry
            )
        )

        val shapeCortijo = TrujilloCuadraShape.generarTrazadoCuadraPorCuadra(
            TrujilloCuadraShape.esperanzaCuadras.reversed() +
                    TrujilloCuadraShape.mansicheMetropolitanaCuadras.reversed().drop(1) +
                    TrujilloCuadraShape.jesusNazarethCuadras.reversed().drop(1) +
                    TrujilloCuadraShape.juanPabloUNTCuadras.reversed().drop(1) +
                    TrujilloCuadraShape.americaSurPapalCuadras.reversed().drop(1) +
                    TrujilloCuadraShape.larcoCuadras.reversed().drop(1)
        )
        listaFinal.add(
            RouteEntity(
                id = autoId++,
                nombreLinea = "El Cortijo S.A. - M-14 C3 (IDA)",
                origen = "Ciudad de Dios / El Cortijo",
                destino = "Óvalo Larco / Buenos Aires",
                tiempoEspera = 9,
                stops = listOf(
                    Stop("El Cortijo", TrujilloCuadraShape.esperanzaCuadras.last()),
                    Stop("Óvalo Mansiche", TrujilloCuadraShape.mansicheMetropolitanaCuadras.first()),
                    Stop("UNT Puerta Principal", TrujilloCuadraShape.juanPabloUNTCuadras[2]),
                    Stop("Buenos Aires Sur", TrujilloCuadraShape.larcoCuadras.first())
                ),
                shapePoints = shapeCortijo,
                recorrido = shapeCortijo
            )
        )

        return listaFinal
    }

    private fun obtenerExtremosDescriptivos(letra: String, direccion: String): Pair<String, String> {
        val parIda = when (letra.uppercase()) {
            "A" -> "El Milagro / Panamericana" to "Víctor Larco / Buenos Aires"
            "B1" -> "Víctor Larco / Av. Larco" to "La Esperanza / Los Tulipanes"
            "BC" -> "Víctor Larco / Av. Larco" to "La Esperanza / Av. San Pedro"
            "C" -> "Víctor Larco / Mall Plaza" to "El Milagro / Panamericana"
            "LV" -> "Laredo / Virgen de la Puerta" to "Víctor Larco / Buenos Aires"
            "V" -> "Víctor Larco / UNT / Mansiche" to "La Esperanza / Av. San Pedro"
            else -> "Inicio de Ruta" to "Fin de Ruta"
        }
        return if (direccion.uppercase() == "IDA") parIda else (parIda.second to parIda.first)
    }
}
