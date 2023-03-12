package com.example.weathermate.data


import androidx.lifecycle.LiveData
import com.example.weathermate.data.model.Alert
import com.example.weathermate.data.model.AlertItem
import com.example.weathermate.data.model.FavoriteAddress
import com.example.weathermate.data.model.WeatherData
import com.example.weathermate.data.model.MapsAutoCompleteResponse
import com.example.weathermate.data.local.LocalDataSource
import com.example.weathermate.data.remote.RemoteDataSource
import com.example.weathermate.util.MyHelper
import kotlinx.coroutines.flow.flow

class Repository (var localDataSource: LocalDataSource, var remoteDataSource: RemoteDataSource){

    private val sharedPreferences = MyHelper.getSharedPreferencesInstance()
    private val sharedPreferencesEditor = sharedPreferences.edit()

    //From Remote:

    suspend fun getWeatherData(lat: Double, lon: Double, language: String) = flow {
        emit(remoteDataSource.getWeatherData(lat, lon, language))
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

    suspend fun findWeatherDataByLatlon(latitude: Double, longitude: Double): WeatherData {
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

    // Shared preferences
    fun putStringInSharedPreferences(key: String, stringInput: String){
        sharedPreferencesEditor.putString(key, stringInput)
        sharedPreferencesEditor.apply()
    }

    fun getStringFromSharedPreferences(key: String, stringDefault: String) : String{
        return sharedPreferences.getString(key, stringDefault)!!
    }

    fun putBooleanInSharedPreferences(key: String, booleanInput: Boolean){
        sharedPreferencesEditor.putBoolean(key, booleanInput)
        sharedPreferencesEditor.apply()
    }

    fun getBooleanFromSharedPreferences(key: String, booleanDefault: Boolean): Boolean{
        return sharedPreferences.getBoolean(key, booleanDefault)
    }

}

