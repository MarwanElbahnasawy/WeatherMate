package com.example.weathermate.repository.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.weathermate.model.Converters
import com.example.weathermate.model.FavoriteAddress
import com.example.weathermate.model.WeatherData

@Database(entities = [WeatherData::class , FavoriteAddress::class], exportSchema = false, version = 1)
@TypeConverters(Converters::class)
abstract class MyDatabase : RoomDatabase() {

    abstract fun getWeatherDataDAO(): WeatherDataDAO
    abstract fun getFavoriteAddressDAO(): FavoriteAddressDAO

    companion object {
        private var instance: MyDatabase? = null
        @Synchronized
        fun getInstance(context: Context): MyDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    MyDatabase::class.java, "WeatherDataDatabase"
                ).build()
            }
            return instance!!
        }
    }
}
