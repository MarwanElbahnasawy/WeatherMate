package com.example.weathermate.data.remote

import com.example.weathermate.data.model.MapsAutoCompleteResponse
import com.example.weathermate.data.model.WeatherData

interface InterfaceRemoteDataSource {
    suspend fun getWeatherDataOnline(lat: Double, lon: Double, language: String): WeatherData
    suspend fun getMapsAutoCompleteResponse(textInput: CharSequence?): MapsAutoCompleteResponse
}