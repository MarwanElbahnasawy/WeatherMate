package com.example.weathermate.data.local

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.weathermate.data.model.AlertItem
import com.example.weathermate.data.model.FavoriteAddress
import com.example.weathermate.data.model.WeatherData

class LocalDataSource private constructor(context: Context) {

    private var weatherDataDAO: WeatherDataDAO
    private var favoriteAddressDAO: FavoriteAddressDAO
    private var alertsDAO: AlertsDAO

    init {
        val myDatabase = MyDatabase.getInstance(context.applicationContext)
        weatherDataDAO = myDatabase.getWeatherDataDAO()
        favoriteAddressDAO = myDatabase.getFavoriteAddressDAO()
        alertsDAO = myDatabase.getAlertsDAO()
    }

    companion object{
        @Volatile
        private var localDataSourceInstance: LocalDataSource? = null

        @Synchronized
        fun getInstance(context: Context): LocalDataSource {
            if(localDataSourceInstance == null){
                localDataSourceInstance = LocalDataSource(context)
            }
            return localDataSourceInstance!!
        }
    }

    //Weather

    suspend fun getWeatherDataFromDB(): WeatherData?{
        return weatherDataDAO.getWeatherDataFromDB()
    }
    suspend fun insertOrUpdateWeatherData(weatherData: WeatherData){
        weatherDataDAO.insertOrUpdateWeatherData(weatherData)
    }


    //Favorites
    fun getAllFavoriteAddresses(): LiveData<List<FavoriteAddress>>{
        return favoriteAddressDAO.getAllFavoriteAddresses()
    }

    suspend fun insertFavoriteAddress(address: FavoriteAddress){
        favoriteAddressDAO.insertFavoriteAddress(address)
    }

    suspend fun deleteFavoriteAddress(address: FavoriteAddress){
        favoriteAddressDAO.deleteFavoriteAddress(address)
    }

    //Alerts
    fun getAllAlerts(): LiveData<List<AlertItem>>{
        return alertsDAO.getAllAlerts()
    }

    suspend fun findAlert(idInputLong: Long): AlertItem {
        return alertsDAO.findAlert(idInputLong)
    }

    suspend fun insertAlert(alert: AlertItem){
        alertsDAO.insertAlert(alert)
    }

    suspend fun deleteAlert(alert: AlertItem){
        alertsDAO.deleteAlert(alert)
    }

}