package com.example.weathermate.data.local

import androidx.lifecycle.LiveData
import com.example.weathermate.data.model.AlertItem
import com.example.weathermate.data.model.FavoriteAddress
import com.example.weathermate.data.model.WeatherData

interface InterfaceLocalDataSource {
    suspend fun getWeatherDataFromDB(): WeatherData?
    suspend fun insertOrUpdateWeatherData(weatherData: WeatherData)

    //Favorites
    fun getAllFavoriteAddresses(): List<FavoriteAddress>
    suspend fun insertFavoriteAddress(address: FavoriteAddress)
    suspend fun deleteFavoriteAddress(address: FavoriteAddress)

    //Alerts
    fun getAllAlerts(): LiveData<List<AlertItem>>
    suspend fun insertAlert(alert: AlertItem)
    suspend fun deleteAlert(alert: AlertItem)
}