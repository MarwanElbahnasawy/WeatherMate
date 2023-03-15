package com.example.weathermate.data.remote

import android.content.Context
import com.example.weathermate.R
import com.example.weathermate.data.model.WeatherData
import com.example.weathermate.data.model.MapsAutoCompleteResponse

class RemoteDataSource private constructor(context: Context) : InterfaceRemoteDataSource {

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

    override suspend fun getWeatherDataOnline(lat: Double, lon: Double, language: String): WeatherData {
        return weatherApiService.getWeatherDataOnline(weatherApiKey, lat, lon, language)
    }

    private val mapRetrofit =
        RetrofitHelper.getRetrofitInstance(ApiService.BASE_URL_MAP_AUTOCOMPLETE)
    private val mapApiService = mapRetrofit.create(ApiService::class.java)

    override suspend fun getMapsAutoCompleteResponse(textInput: CharSequence?): MapsAutoCompleteResponse {
        return mapApiService.getMapsAutoCompleteResponse(mapsApiKey, textInput)
    }
}