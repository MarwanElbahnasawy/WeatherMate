package com.example.weathermate.data.remote

import com.example.weathermate.data.model.WeatherData
import com.example.weathermate.data.model.MapsAutoCompleteResponse
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
    suspend fun getWeatherDataOnline(
        @Query("appid") apiKey: String,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("lang") language: String
    ): WeatherData


}
