package com.example.weathermate.network

data class MapsAutoCompleteResponse(
    val predictions: List<Prediction>,
    val status: String
)

data class Prediction(
    val description: String
)