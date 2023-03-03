package com.example.weathermate.model

import androidx.annotation.NonNull
import androidx.room.Entity

@Entity(tableName = "WeatherDataTable", primaryKeys = ["lat", "lon"])
data class WeatherData(
    val current: Current,
    val daily: List<Daily>,
    val hourly: List<Hourly>,
    val lat: Double,
    val lon: Double,
//    val timezone: String,
//    val timezone_offset: Int
)

@Entity(tableName = "FavoriteAddressTable", primaryKeys = ["latlon"])
data class FavoriteAddress(
    var address: String,
    @NonNull
    var latitude: Double,
    @NonNull
    var longitude: Double,
    var latlon: String
)

data class Current(
    val clouds: Int,
    val humidity: Int,
    val pressure: Int,
    val dt: Int,
    val temp: Double,
    val wind_speed: Double,
    val weather: List<Weather>

//    , val dew_point: Double,
//    val feels_like: Double,
//    val sunrise: Int,
//    val sunset: Int,
//    val uvi: Double,
//    val visibility: Int,
//    val wind_deg: Int,
//    val wind_gust: Double

)

data class Daily(
    val dt: Int,
    val temp: Temp,
    val weather: List<Weather>

//   , val clouds: Int,
//    val dew_point: Double,
//    val feels_like: FeelsLike,
//    val humidity: Int,
//    val moon_phase: Double,
//    val moonrise: Int,
//    val moonset: Int,
//    val pop: Double,
//    val pressure: Int,
//    val rain: Double,
//    val sunrise: Int,
//    val sunset: Int,
//    val uvi: Double,
//    val wind_deg: Int,
//    val wind_gust: Double,
//    val wind_speed: Double
)


data class Hourly(
    val dt: Int,
    val weather: List<Weather>,
    val temp: Double

//   , val clouds: Int,
//    val dew_point: Double,
//    val feels_like: Double,
//    val humidity: Int,
//    val pop: Double,
//    val pressure: Int,
//    val uvi: Double,
//    val visibility: Int,
//    val wind_deg: Int,
//    val wind_gust: Double,
//    val wind_speed: Double
)

data class Temp(
    val max: Double,
    val min: Double

//  ,  val day: Double,
//    val eve: Double,
//    val morn: Double,
//    val night: Double
)

data class Weather(
    val description: String,
    val icon: String,
    val id: Int
// ,   val main: String
)

//data class FeelsLike(
//    val day: Double,
//    val eve: Double,
//    val morn: Double,
//    val night: Double
//)

