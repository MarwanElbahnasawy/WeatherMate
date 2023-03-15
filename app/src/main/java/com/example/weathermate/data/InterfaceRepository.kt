package com.example.weathermate.data

import androidx.lifecycle.LiveData
import com.example.weathermate.data.model.AlertItem
import com.example.weathermate.data.model.FavoriteAddress
import com.example.weathermate.data.model.MapsAutoCompleteResponse
import com.example.weathermate.data.model.WeatherData
import kotlinx.coroutines.flow.Flow

interface InterfaceRepository {
    //From Remote:
    suspend fun getWeatherDataOnline(lat: Double, lon: Double, language: String): Flow<WeatherData>

    suspend fun getMapsAutoCompleteResponse(textInput: CharSequence?): MapsAutoCompleteResponse

    //Weather
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

    // Shared preferences
    fun putStringInSharedPreferences(key: String, stringInput: String)
    fun getStringFromSharedPreferences(key: String, stringDefault: String): String
    fun putBooleanInSharedPreferences(key: String, booleanInput: Boolean)
    fun getBooleanFromSharedPreferences(key: String, booleanDefault: Boolean): Boolean
}