package com.example.weathermate.network

import com.example.weathermate.model.WeatherData
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    companion object{
        const val BASE_URL_MAP_AUTOCOMPLETE = "https://maps.googleapis.com/"
        const val BASE_URL_WEATHER = "https://api.openweathermap.org/"
    }

    @GET("maps/api/place/autocomplete/json")
    suspend fun getMapsAutoCompleteResponse(
        @Query("key") apiKey: String,
        @Query("input") textInput: CharSequence?) : MapsAutoCompleteResponse

    @GET("data/2.5/onecall")
    suspend fun getWeatherData(
        @Query("appid") apiKey: String,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("lang") language: String
    ): WeatherData

}