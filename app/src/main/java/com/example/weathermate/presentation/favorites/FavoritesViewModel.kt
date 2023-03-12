package com.example.weathermate.presentation.favorites

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathermate.data.model.FavoriteAddress
import com.example.weathermate.data.Repository
import com.example.weathermate.data.remote.RetrofitStateFavorites
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*

class FavoritesViewModel(private val repository: Repository) : ViewModel() {

    private val TAG = "commonnn"

    val retrofitStateFavorites = MutableStateFlow<RetrofitStateFavorites>(RetrofitStateFavorites.Loading)


    fun getAllFavoriteAddresses() : LiveData<List<FavoriteAddress>> {
        return repository.getAllFavoriteAddresses()
    }

    suspend fun deleteFavoriteAddress(address: FavoriteAddress){
        repository.deleteFavoriteAddress(address)
    }

    fun putStringInSharedPreferences(key: String, stringInput: String){
        repository.putStringInSharedPreferences(key, stringInput)
    }

    fun observeFavorites() : LiveData<List<FavoriteAddress>>{
        return repository.getAllFavoriteAddresses()
    }

    fun reloadFavoritesOnline(favoriteAddresses: List<FavoriteAddress>) {


        var listFavoritesUpdated = mutableListOf<FavoriteAddress>()

        viewModelScope.launch(Dispatchers.IO) {
            Log.i(TAG, "getFavorites: here1")
                Log.i(TAG, "getFavorites: here2")

            if (favoriteAddresses.isNotEmpty()){
                favoriteAddresses.forEach {currentFavoriteFromRepository ->
                    val data = if(repository.getStringFromSharedPreferences("language","")=="english") {
                        repository.getWeatherData(currentFavoriteFromRepository.latitude,
                            currentFavoriteFromRepository.longitude,
                            "en")
                    }  else{
                        repository.getWeatherData(currentFavoriteFromRepository.latitude,
                            currentFavoriteFromRepository.longitude,
                            "ar")
                    }

                    data.catch {
                        retrofitStateFavorites.value = RetrofitStateFavorites.OnFail(Throwable("Error retrieving data"))
                    }
                        .collectLatest{
                            listFavoritesUpdated.add(FavoriteAddress(
                                address = currentFavoriteFromRepository.address,
                                latitude = currentFavoriteFromRepository.latitude,
                                longitude = currentFavoriteFromRepository.longitude,
                                latlngString = currentFavoriteFromRepository.latlngString,
                                currentTemp = it.current.temp,
                                currentDescription = it.current.weather[0].description.capitalize(),
                                lastCheckedTime = System.currentTimeMillis(),
                                icon = it.current.weather[0].icon
                            )
                            )
                        }
                    retrofitStateFavorites.value = RetrofitStateFavorites.OnSuccess(listFavoritesUpdated)
                }
            } else{
                retrofitStateFavorites.value = RetrofitStateFavorites.OnSuccess(emptyList())
            }







        }

    }

}