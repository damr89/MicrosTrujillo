package com.example.microstrujillo.domain

import com.google.gson.annotations.SerializedName

data class SmartBusStop(
    @SerializedName("order") val order: Int,
    @SerializedName("name") val name: String,
    @SerializedName("lat") val lat: Double,
    @SerializedName("lng") val lng: Double
)

data class SmartBusShapePoint(
    @SerializedName("lat") val lat: Double,
    @SerializedName("lng") val lng: Double
)

data class SmartBusJson(
    @SerializedName("company") val company: String,
    @SerializedName("code") val code: String,
    @SerializedName("letter") val letter: String,
    @SerializedName("direction") val direction: String,
    @SerializedName("name") val name: String,
    @SerializedName("color") val color: String,
    @SerializedName("stops") val stops: List<SmartBusStop>,
    @SerializedName("shape") val shape: List<SmartBusShapePoint>?
)
