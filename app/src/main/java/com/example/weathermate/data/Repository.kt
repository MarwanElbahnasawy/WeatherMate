package com.example.weathermate.data


import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import com.example.weathermate.data.local.InterfaceLocalDataSource
import com.example.weathermate.data.model.AlertItem
import com.example.weathermate.data.model.FavoriteAddress
import com.example.weathermate.data.model.WeatherData
import com.example.weathermate.data.model.MapsAutoCompleteResponse
import com.example.weathermate.data.remote.InterfaceRemoteDataSource
import kotlinx.coroutines.flow.flow

class Repository (private val localDataSource: InterfaceLocalDataSource, private val remoteDataSource: InterfaceRemoteDataSource, private val sharedPreferences : SharedPreferences) :
    InterfaceRepository {

    private val sharedPreferencesEditor = sharedPreferences.edit()

    //From Remote:
    override suspend fun getWeatherDataOnline(lat: Double, lon: Double, language: String) = flow {
        emit(remoteDataSource.getWeatherDataOnline(lat, lon, language))
    }
    override suspend fun getMapsAutoCompleteResponse(textInput: CharSequence?): MapsAutoCompleteResponse {
        return remoteDataSource.getMapsAutoCompleteResponse(textInput)
    }

    //From Local

    //Weather
    override suspend fun getWeatherDataFromDB(): WeatherData?{
        return localDataSource.getWeatherDataFromDB()
    }
    override suspend fun insertOrUpdateWeatherData(weatherData: WeatherData) {
        localDataSource.insertOrUpdateWeatherData(weatherData)
    }

    //Favorites
    override fun getAllFavoriteAddresses(): List<FavoriteAddress> {
        return localDataSource.getAllFavoriteAddresses()
    }
    override suspend fun insertFavoriteAddress(address: FavoriteAddress){
        localDataSource.insertFavoriteAddress(address)
    }
    override suspend fun deleteFavoriteAddress(address: FavoriteAddress){
        localDataSource.deleteFavoriteAddress(address)
    }

    //Alerts
    override fun getAllAlerts(): LiveData<List<AlertItem>> {
        return localDataSource.getAllAlerts()
    }
    override suspend fun insertAlert(alert: AlertItem){
        localDataSource.insertAlert(alert)
    }
    override suspend fun deleteAlert(alert: AlertItem){
        localDataSource.deleteAlert(alert)
    }

    // Shared preferences
    override fun putStringInSharedPreferences(key: String, stringInput: String){
        sharedPreferencesEditor.putString(key, stringInput)
        sharedPreferencesEditor.apply()
    }
    override fun getStringFromSharedPreferences(key: String, stringDefault: String) : String{
        return sharedPreferences.getString(key, stringDefault)!!
    }
    override fun putBooleanInSharedPreferences(key: String, booleanInput: Boolean){
        sharedPreferencesEditor.putBoolean(key, booleanInput)
        sharedPreferencesEditor.apply()
    }
    override fun getBooleanFromSharedPreferences(key: String, booleanDefault: Boolean): Boolean{
        return sharedPreferences.getBoolean(key, booleanDefault)
    }

}

