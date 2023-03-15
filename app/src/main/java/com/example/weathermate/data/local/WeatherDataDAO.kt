package com.example.weathermate.data.local

import androidx.room.*
import com.example.weathermate.data.model.WeatherData

@Dao
interface WeatherDataDAO {

    @Query("SELECT * FROM WeatherDataTable LIMIT 1")
    suspend fun getWeatherDataFromDB(): WeatherData?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherData(weatherData: WeatherData)

    @Transaction
    suspend fun insertOrUpdateWeatherData(weatherData: WeatherData) {
        val existingWeatherData = getWeatherDataFromDB()
        existingWeatherData?.let {
            deleteWeatherData(it)
        }
        insertWeatherData(weatherData)
    }

    @Delete
    suspend fun deleteWeatherData(weatherData: WeatherData)
}
