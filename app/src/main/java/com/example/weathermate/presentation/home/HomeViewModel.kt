package com.example.weathermate.presentation.home

import android.content.Context
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.*
import com.example.weathermate.data.model.WeatherData
import com.example.weathermate.data.Repository
import com.example.weathermate.data.remote.RetrofitState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*

class HomeViewModel(private val repository: Repository) : ViewModel() {

    private val TAG = "commonnn"

    private lateinit var geocoder: Geocoder

    private var longitudeDouble: Double = 0.0
    private var latitudeDouble: Double = 0.0

    val cityName = MutableLiveData<String>()

    val retrofitState = MutableStateFlow<RetrofitState>(RetrofitState.Loading)

    fun initPreferencesManager(context: Context) {
        geocoder = Geocoder(context, Locale.getDefault())
    }

    suspend fun getWeatherData() {
        viewModelScope.launch(Dispatchers.IO) {
            if (repository.getStringFromSharedPreferences("language", "").equals("english")) {

                val data = repository.getWeatherData(latitudeDouble,
                    longitudeDouble,
                    "en")

                data.catch {
                    retrofitState.value = RetrofitState.onFail(Throwable("Error retrieving data"))
                }
                    .collectLatest{
                        retrofitState.value = RetrofitState.onSuccess(it)
                    }
                Log.i(TAG, "getWeatherData: after english call")
            } else {
                Log.i(TAG, "getWeatherData: ++++++++++++arabbic" )

                val data = repository.getWeatherData(latitudeDouble,
                    longitudeDouble,
                    "ar")

                data.catch {
                    retrofitState.value = RetrofitState.onFail(Throwable("Error retrieving data"))
                }
                    .collectLatest{
                        retrofitState.value = RetrofitState.onSuccess(it)
                    }
            }

        }
    }



    fun setLatitudeAndLongitude() {
        latitudeDouble = repository.getStringFromSharedPreferences("latitude" , "")?.toDouble() ?: 0.0
        longitudeDouble = repository.getStringFromSharedPreferences("longitude" , "")?.toDouble() ?: 0.0

    }

    fun updateCityName(isAdded: Boolean) {
        if (isAdded) {
            val addresses = geocoder.getFromLocation(latitudeDouble, longitudeDouble, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val address = addresses[0]
                val addressName = address.locality ?: address.subAdminArea ?: address.adminArea
                cityName.value = addressName
            }
        }
    }

    fun getTemperatureUnit(): String? {
        return repository.getStringFromSharedPreferences("temperature_unit", "")
    }

    fun getWindSpeedUnit(): String? {
        return repository.getStringFromSharedPreferences("wind_speed_unit", "")
    }

    fun isPreferencesSet(): Boolean {
        return repository.getBooleanFromSharedPreferences("preferences_set", false)
    }

    fun getStringFromSharedPreferences(key: String, stringDefault: String) : String{
        return repository.getStringFromSharedPreferences(key, stringDefault)
    }
    
}