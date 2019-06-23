package com.meta_engine.model

data class Marker(
    val id: String,
    val lat: Double,
    val lng: Double,
    val type: MarkerType,
    val radius: Int //metres
)