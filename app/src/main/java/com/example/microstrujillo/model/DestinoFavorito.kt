package com.example.microstrujillo.model

import org.osmdroid.util.GeoPoint

data class DestinoFavorito(
    val id: Int,
    val nombre: String,
    val detalle: String,
    val emoji: String,
    val latitud: Double,
    val longitud: Double
) {
    fun toGeoPoint(): GeoPoint = GeoPoint(latitud, longitud)
}

object CatalogoFavoritosTrujillo {
    val destinos = listOf(
        DestinoFavorito(
            id = 1,
            nombre = "Casa",
            detalle = "Urb. San Andrés / Larco",
            emoji = "🏠",
            latitud = -8.1285,
            longitud = -79.0353
        ),
        DestinoFavorito(
            id = 2,
            nombre = "UPN El Molino",
            detalle = "Av. Ejército 920",
            emoji = "🎓",
            latitud = -8.1065,
            longitud = -79.0175
        ),
        DestinoFavorito(
            id = 3,
            nombre = "UNT",
            detalle = "Av. Juan Pablo II",
            emoji = "🏛️",
            latitud = -8.1160,
            longitud = -79.0422
        ),
        DestinoFavorito(
            id = 4,
            nombre = "UPAO",
            detalle = "Av. América Sur 3145",
            emoji = "📚",
            latitud = -8.1265,
            longitud = -79.0320
        ),
        DestinoFavorito(
            id = 5,
            nombre = "Mall Plaza",
            detalle = "Av. América Oeste",
            emoji = "🛍️",
            latitud = -8.1130,
            longitud = -79.0442
        ),
        DestinoFavorito(
            id = 6,
            nombre = "Plaza Mayor",
            detalle = "Centro Histórico",
            emoji = "⛲",
            latitud = -8.1116,
            longitud = -79.0287
        )
    )
}