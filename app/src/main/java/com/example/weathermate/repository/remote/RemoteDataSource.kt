package com.example.weathermate.repository.remote

import android.content.Context
import com.example.weathermate.R
import com.example.weathermate.model.Alert
import com.example.weathermate.model.WeatherData
import com.example.weathermate.network.MapsAutoCompleteResponse

class RemoteDataSource private constructor(context: Context) {

    private val mapsApiKey = context.resources.getString(R.string.google_maps_key)
    private val weatherApiKey = context.resources.getString(R.string.openweathermap_key)

    companion object {
        @Volatile
        private var remoteDataSourceInstance: RemoteDataSource? = null

        @Synchronized
        fun getInstance(context: Context): RemoteDataSource {
            if (remoteDataSourceInstance == null) {
                remoteDataSourceInstance = RemoteDataSource(context)
            }
            return remoteDataSourceInstance!!
        }
    }

    private val weatherRetrofit = RetrofitHelper.getRetrofitInstance(ApiService.BASE_URL_WEATHER)
    private val weatherApiService = weatherRetrofit.create(ApiService::class.java)

    suspend fun getWeatherData(lat: Double, lon: Double, language: String): WeatherData {
        return weatherApiService.getWeatherData(weatherApiKey, lat, lon, language)
    }


    suspend fun getAlertsOnly(lat: Double, lon: Double): List<Alert> {
        return weatherApiService.getAlertsOnly("hourly,daily,current,minutely", weatherApiKey, lat, lon).alerts
    }

    private val mapRetrofit =
        RetrofitHelper.getRetrofitInstance(ApiService.BASE_URL_MAP_AUTOCOMPLETE)
    private val mapApiService = mapRetrofit.create(ApiService::class.java)

    suspend fun getMapsAutoCompleteResponse(textInput: CharSequence?): MapsAutoCompleteResponse {
        return mapApiService.getMapsAutoCompleteResponse(mapsApiKey, textInput)
    }




}