package com.example.weathermate.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathermate.data.model.MapsAutoCompleteResponse
import com.example.weathermate.data.Repository
import com.example.weathermate.data.model.FavoriteAddress
import com.example.weathermate.data.remote.RetrofitStateWeather
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MapViewModel(private val repository: Repository) : ViewModel() {

    private  val TAG = "commonnn"

    val retrofitStateWeather = MutableStateFlow<RetrofitStateWeather>(RetrofitStateWeather.Loading)


//    fun insertFavoriteAddress(favoriteAddress: FavoriteAddress) {
//        viewModelScope.launch {
//            repository.insertFavoriteAddress(favoriteAddress)
//        }
//    }

    fun insertFavoriteAddressInternetCall(
        latitudeDouble: Double,
        longitudeDouble: Double
    ) {

        viewModelScope.launch(Dispatchers.IO) {

            val data = repository.getWeatherData(latitudeDouble,
                longitudeDouble,
                "en")

            data.catch {
                retrofitStateWeather.value = RetrofitStateWeather.OnFail(Throwable("Error retrieving data"))
            }
                .collectLatest{
                    retrofitStateWeather.value = RetrofitStateWeather.OnSuccess(it)
                }
        }

    }



    fun putStringInSharedPreferences(key: String, stringInput: String){
        repository.putStringInSharedPreferences(key, stringInput)
    }

    fun getStringFromSharedPreferences(key: String, stringDefault: String) : String{
        return repository.getStringFromSharedPreferences(key, stringDefault)
    }

    fun putBooleanInSharedPreferences(key: String, booleanInput: Boolean){
        repository.putBooleanInSharedPreferences(key, booleanInput)
    }

    fun getBooleanFromSharedPreferences(key: String, booleanDefault: Boolean): Boolean{
        return repository.getBooleanFromSharedPreferences(key, booleanDefault)
    }

    suspend fun getMapsAutoCompleteResponse(textInput: CharSequence?): MapsAutoCompleteResponse {
        return repository.getMapsAutoCompleteResponse(textInput)
    }

    fun insertToFavorites(favoriteAddress: FavoriteAddress) {
        viewModelScope.launch {
            repository.insertFavoriteAddress(favoriteAddress)
        }
    }


}