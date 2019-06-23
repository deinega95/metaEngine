package com.meta_engine.model

data class Human(
    val id: String,
    val type: HumanType,
    val path: ArrayList<Coordinates>
)

data class Coordinates(
    val lat: Double,
    val lng: Double
)