package com.example.microstrujillo.data

import org.osmdroid.util.GeoPoint

object TrujilloCuadraShape {

    private fun pt(lat: Double, lon: Double): GeoPoint {
        return GeoPoint(lat, lon)
    }

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

    fun generarTrazadoCuadraPorCuadra(nodosCuadras: List<GeoPoint>): List<GeoPoint> {
        if (nodosCuadras.size < 2) return nodosCuadras
        val resultado = mutableListOf<GeoPoint>()

        for (i in 0 until nodosCuadras.size - 1) {
            val segmento = interpolarPista(nodosCuadras[i], nodosCuadras[i + 1])
            if (i > 0) {
                resultado.addAll(segmento.drop(1))
            } else {
                resultado.addAll(segmento)
            }
        }

        return resultado
    }

    // --- COORDENADAS EXACTAS SOBRE LAS AVENIDAS ROJAS DE OPENSTREETMAP ---

    // 1. Av. Víctor Larco Herrera (Terminal -> Óvalo Larco)
    // Trasladado 160m al Sureste para salir de Jirón El Palmar y entrar a Av. Víctor Larco Herrera
    val larcoCuadras = listOf(
        pt(-8.1460, -79.0500), // Terminal Buenos Aires Balneario
        pt(-8.1435, -79.0480), // Ca. Bolivia
        pt(-8.1410, -79.0460), // Ca. Libertad
        pt(-8.1385, -79.0435), // Jr. Desamparados
        pt(-8.1355, -79.0405), // Ca. San Martín
        pt(-8.1330, -79.0380), // Ca. Colón
        pt(-8.1310, -79.0360), // Av. Huamán
        pt(-8.1295, -79.0350), // Ca. Los Pinos
        pt(-8.1285, -79.0345)  // Óvalo Larco
    )

    // 2. Av. América Sur (Óvalo Larco -> Óvalo Papal)
    val americaSurPapalCuadras = listOf(
        pt(-8.1285, -79.0345), // Óvalo Larco
        pt(-8.1265, -79.0355), // Ca. Los Ángeles
        pt(-8.1245, -79.0368), // Ca. Los Pinos
        pt(-8.1225, -79.0380), // Av. Antenor Orrego
        pt(-8.1205, -79.0392), // Ca. Los Laureles
        pt(-8.1189, -79.0402)  // Óvalo Papal
    )

    // 3. Av. Juan Pablo II / UNT (Óvalo Papal -> Av. Jesús de Nazareth)
    val juanPabloUNTCuadras = listOf(
        pt(-8.1189, -79.0402), // Óvalo Papal
        pt(-8.1175, -79.0412), // Puerta 1 UNT
        pt(-8.1160, -79.0422), // Puerta Principal UNT
        pt(-8.1148, -79.0432), // Facultad Medicina UNT
        pt(-8.1135, -79.0442)  // Av. Jesús de Nazareth
    )

    // 4. Av. Jesús de Nazareth (Av. Juan Pablo II -> Óvalo Mansiche)
    val jesusNazarethCuadras = listOf(
        pt(-8.1135, -79.0442), // Av. Juan Pablo II
        pt(-8.1118, -79.0418), // Ca. Roma
        pt(-8.1098, -79.0382), // Ca. Pedro Muñiz
        pt(-8.1082, -79.0358), // Av. España
        pt(-8.1070, -79.0338)  // Óvalo Mansiche
    )

    // 5. Av. Mansiche & Av. Metropolitana II (Óvalo Mansiche -> Entrada La Esperanza)
    val mansicheMetropolitanaCuadras = listOf(
        pt(-8.1070, -79.0338), // Óvalo Mansiche
        pt(-8.1055, -79.0353), // Ca. Río de Janeiro
        pt(-8.1040, -79.0368), // Ca. Buenos Aires
        pt(-8.1025, -79.0383), // Av. América Oeste
        pt(-8.1010, -79.0398), // San Isidro
        pt(-8.0995, -79.0418), // Ca. Espinela
        pt(-8.0980, -79.0438), // Av. Metropolitana II
        pt(-8.0955, -79.0450), // Ca. 1 San Isidro
        pt(-8.0930, -79.0468), // Ca. 2 San Isidro
        pt(-8.0905, -79.0483), // Makro
        pt(-8.0870, -79.0498), // Parque Infantil
        pt(-8.0850, -79.0508)  // Entrada La Esperanza
    )

    // 6. La Esperanza (Condorcanqui -> Cahuide -> Egipto -> Terminal Wichanzao)
    val esperanzaCuadras = listOf(
        pt(-8.0850, -79.0508), // Cruce Evitamiento
        pt(-8.0825, -79.0518), // Condorcanqui 1
        pt(-8.0800, -79.0528), // Condorcanqui 2
        pt(-8.0770, -79.0536), // Mateo de Toro y Zambrano
        pt(-8.0750, -79.0548), // Jr. Chancay
        pt(-8.0732, -79.0566), // Av. Egipto & Cahuide
        pt(-8.0710, -79.0578), // Av. Cardenales
        pt(-8.0680, -79.0590), // Ca. Cajabamba
        pt(-8.0650, -79.0608), // Av. Nuevo Trujillo
        pt(-8.0625, -79.0628), // Av. El Sol
        pt(-8.0600, -79.0648)  // Terminal Wichanzao
    )

    // 7. Av. América Sur (Óvalo Larco -> Óvalo Grau)
    val americaSurGrauCuadras = listOf(
        pt(-8.1285, -79.0345), // Óvalo Larco
        pt(-8.1265, -79.0320), // Av. Huamán
        pt(-8.1245, -79.0292), // Ca. F. Villareal
        pt(-8.1225, -79.0267), // Av. Gonzáles Prada
        pt(-8.1205, -79.0240), // Ca. Zela
        pt(-8.1185, -79.0214), // Av. Moche
        pt(-8.1165, -79.0187)  // Óvalo Grau
    )

    // 8. Av. América Norte (Óvalo Grau -> Óvalo Mochica)
    val americaNorteMochicaCuadras = listOf(
        pt(-8.1165, -79.0187), // Óvalo Grau
        pt(-8.1135, -79.0177), // Av. Eguren
        pt(-8.1105, -79.0172), // Av. César Vallejo
        pt(-8.1075, -79.0167), // Av. Perú
        pt(-8.1040, -79.0167), // Av. Villarreal
        pt(-8.1000, -79.0170), // Av. Miraflores
        pt(-8.0960, -79.0182), // Ca. Salvador Lara
        pt(-8.0921, -79.0203)  // Óvalo Mochica
    )

    // 9. Av. América Oeste / Mall Plaza
    val americaOesteMallCuadras = listOf(
        pt(-8.1285, -79.0345), // Óvalo Larco
        pt(-8.1255, -79.0367), // Ca. Los Pinos
        pt(-8.1225, -79.0392), // Antenor Orrego
        pt(-8.1195, -79.0412), // Av. Fátima
        pt(-8.1160, -79.0430), // Parque Infantil
        pt(-8.1130, -79.0442), // Mall Plaza Principal
        pt(-8.1080, -79.0432), // Jesús de Nazareth
        pt(-8.1040, -79.0417)  // Mansiche
    )

    // 10. Av. Pablo Casals
    val pabloCasalsCuadras = listOf(
        pt(-8.1040, -79.0417), // Av. Mansiche
        pt(-8.1015, -79.0390), // Ca. 17
        pt(-8.0985, -79.0352), // Av. Túpac Amaru
        pt(-8.0960, -79.0287), // Ca. Pucará
        pt(-8.0935, -79.0237), // Ca. Los Incas
        pt(-8.0921, -79.0203)  // Óvalo Mochica
    )

    // 11. Av. Túpac Amaru y Tahuantinsuyo
    val tupacTahuantinsuyoCuadras = listOf(
        pt(-8.0921, -79.0203), // Óvalo Mochica
        pt(-8.0900, -79.0230), // Ca. Salvador Lara
        pt(-8.0885, -79.0237), // Ca. Los Granados
        pt(-8.0875, -79.0252), // Ca. Los Laureles
        pt(-8.0860, -79.0264), // Av. Tahuantinsuyo
        pt(-8.0845, -79.0277), // Ca. José Martí
        pt(-8.0830, -79.0290), // Hospital Jerusalén
        pt(-8.0795, -79.0307)  // Av. Condorcanqui
    )

    // 12. Panamericana Norte / El Milagro
    val milagroCuadras = listOf(
        pt(-8.0580, -79.0458), // Evitamiento
        pt(-8.0500, -79.0508), // Av. Miguel Grau / Entrada
        pt(-8.0440, -79.0543), // Penal El Milagro
        pt(-8.0330, -79.0623), // Panamericana Norte PE-1N
        pt(-8.0280, -79.0648)  // Grifo El Reposo
    )

    // 13. Laredo y El Porvenir
    val laredoPorvenirCuadras = listOf(
        pt(-8.0890, -78.9608), // Ca. Virgen de La Puerta (Laredo)
        pt(-8.0925, -78.9758), // Av. Nuevo San Carlos
        pt(-8.0960, -78.9908), // Av. Pedro García García
        pt(-8.1015, -79.0038), // Ca. Las Dalias
        pt(-8.1050, -79.0088), // Jr. San Ignacio / Jr. La Merced
        pt(-8.1100, -79.0138), // Av. Pumacahua
        pt(-8.1165, -79.0187)  // Óvalo Grau
    )
}
