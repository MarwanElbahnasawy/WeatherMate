package com.example.weathermate.data

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.weathermate.data.model.AlertItem
import com.example.weathermate.data.model.FavoriteAddress
import com.example.weathermate.data.model.MapsAutoCompleteResponse
import com.example.weathermate.data.model.WeatherData
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.Flow


class FakeRepository(
    private var weatherData: WeatherData? = null,
    private val favoriteAddresses: MutableList<FavoriteAddress> = mutableListOf(),
    private val alerts: MutableList<AlertItem> = mutableListOf(),
    private val mapsAutoCompleteResponse: MapsAutoCompleteResponse? = null,
    private val weatherDataFlow : Flow<WeatherData> = emptyFlow(),
    private val sharedPreferences : SharedPreferences? = null
) : InterfaceRepository {

    override suspend fun getWeatherDataOnline(lat: Double, lon: Double, language: String): Flow<WeatherData> {
        return weatherDataFlow
    }

    override suspend fun getMapsAutoCompleteResponse(textInput: CharSequence?): MapsAutoCompleteResponse {
        return mapsAutoCompleteResponse ?: throw Exception("No maps auto complete response set")
    }

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


    override fun putStringInSharedPreferences(key: String, stringInput: String) {
        sharedPreferences?.edit()?.putString(key, stringInput)?.apply()
    }

    override fun getStringFromSharedPreferences(key: String, stringDefault: String): String {
        return sharedPreferences?.getString(key, stringDefault) ?: stringDefault
    }

    override fun putBooleanInSharedPreferences(key: String, booleanInput: Boolean) {
        sharedPreferences?.edit()?.putBoolean(key, booleanInput)?.apply()
    }

    override fun getBooleanFromSharedPreferences(key: String, booleanDefault: Boolean): Boolean {
        return sharedPreferences?.getBoolean(key, booleanDefault) ?: booleanDefault
    }
}