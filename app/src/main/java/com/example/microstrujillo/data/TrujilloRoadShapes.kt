package com.example.microstrujillo.data

import org.osmdroid.util.GeoPoint

object TrujilloRoadShapes {

    private fun interpolarPista(p1: GeoPoint, p2: GeoPoint, pasoMetros: Double = 3.0): List<GeoPoint> {
        val distTotal = p1.distanceToAsDouble(p2)
        if (distTotal <= pasoMetros) return listOf(p1, p2)

        val numPasos = (distTotal / pasoMetros).toInt()
        val resultado = mutableListOf<GeoPoint>()

        for (i in 0..numPasos) {
            val fraction = i.toDouble() / numPasos
            val lat = p1.latitude + (p2.latitude - p1.latitude) * fraction
            val lon = p1.longitude + (p2.longitude - p1.longitude) * fraction
            resultado.add(GeoPoint(lat, lon))
        }

        return resultado
    }

    fun generarShapePista(puntosClave: List<GeoPoint>): List<GeoPoint> {
        if (puntosClave.size < 2) return puntosClave
        val resultado = mutableListOf<GeoPoint>()

        for (i in 0 until puntosClave.size - 1) {
            val segmento = interpolarPista(puntosClave[i], puntosClave[i + 1])
            if (i > 0) {
                resultado.addAll(segmento.drop(1))
            } else {
                resultado.addAll(segmento)
            }
        }

        return resultado
    }

    // --- CORREDORES DE ASFALTO DENSOS ---

    // 1. Av. Víctor Larco Herrera
    val larcoRoad = listOf(
        GeoPoint(-8.1460, -79.0520),
        GeoPoint(-8.1445, -79.0505),
        GeoPoint(-8.1430, -79.0490),
        GeoPoint(-8.1415, -79.0475),
        GeoPoint(-8.1400, -79.0460),
        GeoPoint(-8.1385, -79.0440),
        GeoPoint(-8.1370, -79.0425),
        GeoPoint(-8.1350, -79.0410),
        GeoPoint(-8.1335, -79.0395),
        GeoPoint(-8.1320, -79.0380),
        GeoPoint(-8.1300, -79.0365),
        GeoPoint(-8.1285, -79.0353) // Óvalo Larco
    )

    // 2. Av. América Sur (Óvalo Larco -> Óvalo Papal)
    val americaSurPapal = listOf(
        GeoPoint(-8.1285, -79.0353),
        GeoPoint(-8.1270, -79.0358),
        GeoPoint(-8.1255, -79.0363),
        GeoPoint(-8.1238, -79.0368),
        GeoPoint(-8.1215, -79.0374),
        GeoPoint(-8.1198, -79.0378),
        GeoPoint(-8.1186, -79.0381) // Óvalo Papal
    )

    // 3. Av. Juan Pablo II (UNT)
    val juanPabloUNT = listOf(
        GeoPoint(-8.1186, -79.0381),
        GeoPoint(-8.1175, -79.0395),
        GeoPoint(-8.1160, -79.0410), // UNT
        GeoPoint(-8.1148, -79.0425),
        GeoPoint(-8.1135, -79.0440), // Av. Jesús de Nazareth
        GeoPoint(-8.1115, -79.0458),
        GeoPoint(-8.1095, -79.0472)
    )

    // 4. Av. Jesús de Nazareth
    val jesusNazareth = listOf(
        GeoPoint(-8.1135, -79.0440),
        GeoPoint(-8.1122, -79.0422),
        GeoPoint(-8.1110, -79.0405),
        GeoPoint(-8.1100, -79.0388),
        GeoPoint(-8.1090, -79.0370),
        GeoPoint(-8.1080, -79.0358),
        GeoPoint(-8.1070, -79.0350) // Óvalo Mansiche
    )

    // 5. Av. Mansiche & Av. Metropolitana II
    val mansicheMetropolitana = listOf(
        GeoPoint(-8.1070, -79.0350),
        GeoPoint(-8.1055, -79.0365),
        GeoPoint(-8.1040, -79.0380),
        GeoPoint(-8.1025, -79.0395),
        GeoPoint(-8.1010, -79.0410),
        GeoPoint(-8.0995, -79.0430),
        GeoPoint(-8.0980, -79.0450),
        GeoPoint(-8.0955, -79.0462),
        GeoPoint(-8.0930, -79.0480),
        GeoPoint(-8.0905, -79.0495),
        GeoPoint(-8.0870, -79.0510),
        GeoPoint(-8.0850, -79.0520)
    )

    // 6. La Esperanza / Nuevo Trujillo
    val esperanzaRoad = listOf(
        GeoPoint(-8.0850, -79.0520),
        GeoPoint(-8.0825, -79.0530),
        GeoPoint(-8.0800, -79.0540),
        GeoPoint(-8.0770, -79.0548),
        GeoPoint(-8.0750, -79.0560),
        GeoPoint(-8.0732, -79.0578),
        GeoPoint(-8.0710, -79.0590),
        GeoPoint(-8.0680, -79.0602),
        GeoPoint(-8.0650, -79.0620),
        GeoPoint(-8.0625, -79.0640),
        GeoPoint(-8.0600, -79.0660)
    )

    // 7. Av. América Sur (Óvalo Larco -> Óvalo Grau)
    val americaSurGrau = listOf(
        GeoPoint(-8.1285, -79.0353),
        GeoPoint(-8.1265, -79.0328),
        GeoPoint(-8.1245, -79.0300),
        GeoPoint(-8.1225, -79.0275),
        GeoPoint(-8.1205, -79.0248),
        GeoPoint(-8.1185, -79.0222),
        GeoPoint(-8.1165, -79.0195) // Óvalo Grau
    )

    // 8. Av. América Norte (Óvalo Grau -> Óvalo Mochica)
    val americaNorteMochica = listOf(
        GeoPoint(-8.1165, -79.0195),
        GeoPoint(-8.1135, -79.0185),
        GeoPoint(-8.1105, -79.0180),
        GeoPoint(-8.1075, -79.0175),
        GeoPoint(-8.1040, -79.0175),
        GeoPoint(-8.1000, -79.0178),
        GeoPoint(-8.0960, -79.0190),
        GeoPoint(-8.0921, -79.0211) // Óvalo Mochica
    )

    // 9. Av. América Oeste (Mall Plaza)
    val americaOesteMall = listOf(
        GeoPoint(-8.1285, -79.0353),
        GeoPoint(-8.1255, -79.0375),
        GeoPoint(-8.1225, -79.0400),
        GeoPoint(-8.1195, -79.0420),
        GeoPoint(-8.1160, -79.0438),
        GeoPoint(-8.1130, -79.0450), // Mall Plaza
        GeoPoint(-8.1080, -79.0440),
        GeoPoint(-8.1040, -79.0425)
    )

    // 10. Av. Pablo Casals
    val pabloCasalsRoad = listOf(
        GeoPoint(-8.1040, -79.0425),
        GeoPoint(-8.1015, -79.0398),
        GeoPoint(-8.0985, -79.0360),
        GeoPoint(-8.0960, -79.0295),
        GeoPoint(-8.0935, -79.0245),
        GeoPoint(-8.0921, -79.0211)
    )
}
