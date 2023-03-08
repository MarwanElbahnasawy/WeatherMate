package com.example.weathermate.util

import androidx.room.TypeConverter
import com.example.weathermate.data.model.Alert
import com.example.weathermate.data.model.Current
import com.example.weathermate.data.model.Daily
import com.example.weathermate.data.model.Hourly
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class MyConverters {
    companion object {
        fun kelvinToCelsius(temp: Double): Double {
            return temp - 273.15
        }

        fun kelvinToFahrenheit(temp: Double): Double {
            return temp * 9/5 - 459.67
        }

        fun meterPerSecondToKilometerPerHour(speed: Double): Double {
            return speed * 3.6
        }

        fun getMonth(month: Int): String {
            val months = arrayOf(
                "January",
                "February",
                "March",
                "April",
                "May",
                "June",
                "July",
                "August",
                "September",
                "October",
                "November",
                "December"
            )
            return months[month]
        }
    }

    @TypeConverter
    fun fromJsonCurrent(value: String): Current {
        return Gson().fromJson(value, Current::class.java)
    }

    @TypeConverter
    fun toJsonCurrent(value: Current): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun fromJsonDailyList(value: String): List<Daily> {
        val listType = object : TypeToken<List<Daily>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun toJsonDailyList(list: List<Daily>): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun fromJsonHourlyList(value: String): List<Hourly> {
        val listType = object : TypeToken<List<Hourly>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun toJsonHourlyList(list: List<Hourly>): String {
        return Gson().toJson(list)
    }
    @TypeConverter
    fun fromJsonAlertList(value: String): List<Alert> {
        val listType = object : TypeToken<List<Alert>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun toJsonAlertList(list: List<Alert>): String {
        return Gson().toJson(list)
    }

}