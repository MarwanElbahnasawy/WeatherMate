package com.example.weathermate.data.remote

import com.example.weathermate.data.model.MapsAutoCompleteResponse
import com.example.weathermate.data.model.WeatherData

class FakeRemoteDataSource(
    private val weatherData: WeatherData? = null,
    private val mapsAutoCompleteResponse: MapsAutoCompleteResponse? = null
) : InterfaceRemoteDataSource {

    override suspend fun getWeatherDataOnline(lat: Double, lon: Double, language: String): WeatherData {
        return weatherData ?: throw Exception("No weather data set")
    }

    override suspend fun getMapsAutoCompleteResponse(textInput: CharSequence?): MapsAutoCompleteResponse {
        return mapsAutoCompleteResponse ?: throw Exception("No maps auto complete response set")
    }
}