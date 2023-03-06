package com.example.weathermate.repository


import androidx.lifecycle.LiveData
import com.example.weathermate.model.Alert
import com.example.weathermate.model.AlertItem
import com.example.weathermate.model.FavoriteAddress
import com.example.weathermate.model.WeatherData
import com.example.weathermate.network.MapsAutoCompleteResponse
import com.example.weathermate.repository.local.LocalDataSource
import com.example.weathermate.repository.remote.RemoteDataSource

class Repository (var localDataSource: LocalDataSource ,var remoteDataSource: RemoteDataSource){

    //From Remote:

    suspend fun getWeatherData(lat: Double, lon: Double, language: String): WeatherData {
        return remoteDataSource.getWeatherData(lat, lon, language)
    }

    suspend fun getAlertsOnly(lat: Double, lon: Double): List<Alert> {
        return remoteDataSource.getAlertsOnly(lat, lon)
    }

    suspend fun getMapsAutoCompleteResponse(textInput: CharSequence?): MapsAutoCompleteResponse {
        return remoteDataSource.getMapsAutoCompleteResponse(textInput)
    }

    //From Local

    //Weather
    fun storedWeatherData(): LiveData<List<WeatherData>> {
        return localDataSource.storedWeatherData()
    }

    suspend fun findWeatherDataByLatlon(latitude: Double, longitude: Double): WeatherData{
        return localDataSource.findWeatherDataByLatlon(latitude, longitude)
    }

    suspend fun insertWeatherData(weatherData: WeatherData){
        localDataSource.insertWeatherData(weatherData)
    }

    suspend fun deleteWeatherData(weatherData: WeatherData){
        localDataSource.deleteWeatherData(weatherData)
    }

    //Favorites
    fun getAllFavoriteAddresses(): LiveData<List<FavoriteAddress>> {
        return localDataSource.getAllFavoriteAddresses()
    }

    suspend fun insertFavoriteAddress(address: FavoriteAddress){
        localDataSource.insertFavoriteAddress(address)
    }

    suspend fun deleteFavoriteAddress(address: FavoriteAddress){
        localDataSource.deleteFavoriteAddress(address)
    }

    //Alerts
    fun getAllAlerts(): LiveData<List<AlertItem>> {
        return localDataSource.getAllAlerts()
    }

    suspend fun findAlert(idInputLong: Long): AlertItem {
        return localDataSource.findAlert(idInputLong)
    }

    suspend fun insertAlert(alert: AlertItem){
        localDataSource.insertAlert(alert)
    }

    suspend fun deleteAlert(alert: AlertItem){
        localDataSource.deleteAlert(alert)
    }

}

