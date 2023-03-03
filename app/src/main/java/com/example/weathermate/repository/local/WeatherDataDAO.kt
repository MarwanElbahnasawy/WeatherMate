package com.example.weathermate.repository.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.weathermate.model.WeatherData

@Dao
interface WeatherDataDAO {
    @Query("SELECT * FROM WeatherDataTable")
    fun storedWeatherData(): LiveData<List<WeatherData>>

    @Query("SELECT * FROM WeatherDataTable WHERE lat LIKE :latitude AND lon LIKE :longitude")
    suspend fun findWeatherDataByLatlon(latitude: Double, longitude: Double): WeatherData

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherData(weatherData: WeatherData)

    @Delete
    suspend fun deleteWeatherData(weatherData: WeatherData)

}
