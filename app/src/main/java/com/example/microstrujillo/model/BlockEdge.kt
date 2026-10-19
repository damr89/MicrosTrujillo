package com.example.microstrujillo.model

import org.osmdroid.util.GeoPoint

data class BlockEdge(
    val edgeId: Int,
    val streetName: String,
    val fromCorner: String,
    val toCorner: String,
    val order: Int,
    val startLocation: GeoPoint,
    val endLocation: GeoPoint
)
