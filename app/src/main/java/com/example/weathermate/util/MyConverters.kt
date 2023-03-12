package com.example.weathermate.util

import android.content.Context
import androidx.room.TypeConverter
import com.example.weathermate.R
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

        fun meterPerSecondToMilePerHour(speed: Double): Double {
            return speed * 2.237
        }

        fun convertTemperature(temp: Double, context: Context): String {

                if (MyHelper.getSharedPreferencesInstance().getString("language", "")=="english"){
                    return when (MyHelper.getSharedPreferencesInstance().getString("temperature_unit", "")){
                        "celsius" -> "${(kelvinToCelsius(temp).toInt().toString())} ${context.getString(R.string.celsiusUnit)}"
                        "fahrenheit" -> "${(kelvinToFahrenheit(temp).toInt().toString())} ${context.getString(R.string.fahrenheitUnit)}"
                        else -> "${((temp).toInt().toString())} ${context.getString(R.string.kelvinUnit)}"

                    }
                } else{
                    return when (MyHelper.getSharedPreferencesInstance().getString("temperature_unit", "")){
                        "celsius" -> "${convertToArabicNumber(kelvinToCelsius(temp).toInt().toString())} ${context.getString(R.string.celsiusUnit)}"
                        "fahrenheit" -> "${convertToArabicNumber(kelvinToFahrenheit(temp).toInt().toString())} ${context.getString(R.string.fahrenheitUnit)}"
                        else -> "${convertToArabicNumber((temp).toInt().toString())} ${context.getString(R.string.kelvinUnit)}"

                    }
                }
        }

        fun convertWind(wind: Double, context: Context): String {
            if (MyHelper.getSharedPreferencesInstance().getString("language", "")=="english"){
                return when (MyHelper.getSharedPreferencesInstance().getString("wind_speed_unit", "")){
                    "mph" -> "${(meterPerSecondToMilePerHour(wind).toInt().toString())} ${context.getString(R.string.mphUnit)}"
                    else -> "${((wind).toInt().toString())} ${context.getString(R.string.mpsUnit)}"
                }
            } else{
                return when (MyHelper.getSharedPreferencesInstance().getString("wind_speed_unit", "")){
                    "mph" -> "${convertToArabicNumber(meterPerSecondToMilePerHour(wind).toInt().toString())} ${context.getString(R.string.mphUnit)}"
                    else -> "${convertToArabicNumber((wind).toInt().toString())} ${context.getString(R.string.mpsUnit)}"
                }
            }

        }

        fun convertHumidtyOrPressureOrTemperature(input: Int): String {
            if (MyHelper.getSharedPreferencesInstance().getString("language", "")=="english"){
                return input.toString()
            } else{
                return convertToArabicNumber(input.toString())
            }

        }

        fun convertToArabicNumber(englishNumberInput: String): String {
            val arabicNumbers = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
            val englishNumbers = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
            val builder = StringBuilder()
            for (i in englishNumberInput) {
                if (englishNumbers.contains(i)) {
                    builder.append(arabicNumbers[englishNumbers.indexOf(i)])
                } else {
                    builder.append(i) // point
                }
            }
            return builder.toString()
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
    fun fromJsonAlertList(value: String): List<Alert>? {
        return if (value.isNullOrEmpty()) {
            null
        } else {
            val listType = object : TypeToken<List<Alert>>() {}.type
            Gson().fromJson(value, listType)
        }
    }

    @TypeConverter
    fun toJsonAlertList(list: List<Alert>?): String {
        return list?.let { Gson().toJson(it) } ?: ""
    }

}