package com.example.weathermate.data.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.weathermate.data.model.AlertItem
import com.example.weathermate.data.model.FavoriteAddress
import com.example.weathermate.data.model.WeatherData

class FakeLocalDataSource(
    private var weatherData: WeatherData? = null,
    private val favoriteAddresses: MutableList<FavoriteAddress> = mutableListOf(),
    private val alerts: MutableList<AlertItem> = mutableListOf()
) : InterfaceLocalDataSource {

    override suspend fun getWeatherDataFromDB(): WeatherData? {
        return weatherData
    }

    override suspend fun insertOrUpdateWeatherData(weatherData: WeatherData) {
        this.weatherData = weatherData
    }

    override fun getAllFavoriteAddresses(): List<FavoriteAddress> {
        return favoriteAddresses
    }

    override suspend fun insertFavoriteAddress(address: FavoriteAddress) {
        favoriteAddresses.add(address)
    }

    override suspend fun deleteFavoriteAddress(address: FavoriteAddress) {
        favoriteAddresses.remove(address)
    }

    override fun getAllAlerts(): LiveData<List<AlertItem>> {
        return MutableLiveData(alerts)
    }

    override suspend fun insertAlert(alert: AlertItem) {
        alerts.add(alert)
    }

    override suspend fun deleteAlert(alert: AlertItem) {
        alerts.remove(alert)
    }
}