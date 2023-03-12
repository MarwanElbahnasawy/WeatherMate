package com.example.weathermate.data.model

import androidx.annotation.NonNull
import androidx.room.Entity

@Entity(tableName = "WeatherDataTable", primaryKeys = ["lat", "lon"])
data class WeatherData(
    val current: Current,
    val daily: List<Daily>,
    val hourly: List<Hourly>,
    val lat: Double,
    val lon: Double,
    val alerts: List<Alert>
//    val timezone: String,
//    val timezone_offset: Int
)

@Entity(tableName = "FavoriteAddressTable", primaryKeys = ["latlngString"])
data class FavoriteAddress(
    val address: String,
    @NonNull
    val latitude: Double,
    @NonNull
    val longitude: Double,
    val latlngString: String,
    val currentTemp: Double,
    val currentDescription: String,
    val lastCheckedTime: Long,
    val icon: String
)

data class Alert(
    val description: String,
    val start: Int,
    val end: Int,
    val event: String,
    val sender_name: String,
    val tags: List<String>
)
@Entity(tableName = "AlertsTable", primaryKeys = ["idHashLongFromLonLatStartStringEndStringAlertType"])
data class AlertItem(
    val address: String,
    val longitudeString: String,
    val latitudeString: String,
    val startString: String,
    val endString: String,
    val startDT: Int,
    val endDT: Int,
    val idHashLongFromLonLatStartStringEndStringAlertType: Long,
    val alertType: String,
    val timeAdded: Long
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

