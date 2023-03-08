package com.example.weathermate.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.weathermate.data.model.AlertItem
import com.example.weathermate.data.model.FavoriteAddress
import com.example.weathermate.data.model.WeatherData
import com.example.weathermate.util.MyConverters

@Database(entities = [WeatherData::class , FavoriteAddress::class, AlertItem::class], exportSchema = false, version = 1)
@TypeConverters(MyConverters::class)
abstract class MyDatabase : RoomDatabase() {

    abstract fun getWeatherDataDAO(): WeatherDataDAO
    abstract fun getFavoriteAddressDAO(): FavoriteAddressDAO

    abstract fun getAlertsDAO(): AlertsDAO

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
