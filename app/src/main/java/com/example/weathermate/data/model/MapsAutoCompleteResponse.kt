package com.example.weathermate.data.model

data class MapsAutoCompleteResponse(
    val predictions: List<Prediction>,
    val status: String
)

data class Prediction(
    val description: String
)