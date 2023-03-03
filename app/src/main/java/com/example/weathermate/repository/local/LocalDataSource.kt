package com.example.weathermate.repository.local

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.weathermate.model.FavoriteAddress
import com.example.weathermate.model.WeatherData

class LocalDataSource private constructor(context: Context) {

    private var weatherDataDAO: WeatherDataDAO
    private var favoriteAddressDAO: FavoriteAddressDAO

    init {
        val myDatabase = MyDatabase.getInstance(context.applicationContext)
        weatherDataDAO = myDatabase.getWeatherDataDAO()
        favoriteAddressDAO = myDatabase.getFavoriteAddressDAO()
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

    fun storedWeatherData(): LiveData<List<WeatherData>>{
        return weatherDataDAO.storedWeatherData()
    }

    suspend fun findWeatherDataByLatlon(latitude: Double, longitude: Double): WeatherData{
        return weatherDataDAO.findWeatherDataByLatlon(latitude, longitude)
    }

    suspend fun insertWeatherData(weatherData: WeatherData){
        weatherDataDAO.insertWeatherData(weatherData)
    }

    suspend fun deleteWeatherData(weatherData: WeatherData){
        weatherDataDAO.deleteWeatherData(weatherData)
    }

    fun getAllFavoriteAddresses(): LiveData<List<FavoriteAddress>>{
        return favoriteAddressDAO.getAllFavoriteAddresses()
    }

    suspend fun insertFavoriteAddress(address: FavoriteAddress){
        favoriteAddressDAO.insertFavoriteAddress(address)
    }

    suspend fun deleteFavoriteAddress(address: FavoriteAddress){
        favoriteAddressDAO.deleteFavoriteAddress(address)
    }

}