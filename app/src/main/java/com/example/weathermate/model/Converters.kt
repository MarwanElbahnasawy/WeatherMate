package com.example.weathermate.model

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
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