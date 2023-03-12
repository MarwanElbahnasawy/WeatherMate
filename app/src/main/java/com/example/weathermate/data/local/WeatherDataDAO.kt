package com.example.weathermate.data.local

import android.util.Log
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
        Log.i("commonnn", "insertOrUpdateWeatherData000 weatherData: $weatherData")
        val existingWeatherData = getWeatherDataFromDB()
        Log.i("commonnn", "insertOrUpdateWeatherData1: $existingWeatherData")
        existingWeatherData?.let {
            Log.i("commonnn", "insertOrUpdateWeatherData2: $existingWeatherData")
            deleteWeatherData(it)
            Log.i("commonnn", "insertOrUpdateWeatherData3: $existingWeatherData")
        }
        Log.i("commonnn", "insertOrUpdateWeatherData4: $existingWeatherData")
        Log.i("commonnn", "insertOrUpdateWeatherData44: $weatherData")
        insertWeatherData(weatherData)
        Log.i("commonnn", "insertOrUpdateWeatherData5: $existingWeatherData")
    }

    @Delete
    suspend fun deleteWeatherData(weatherData: WeatherData)
}
