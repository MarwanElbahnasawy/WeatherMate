package com.example.weathermate.presentation.favorites

import androidx.lifecycle.*
import com.example.weathermate.data.model.FavoriteAddress
import com.example.weathermate.data.Repository
import com.example.weathermate.data.remote.RetrofitStateFavorites
import com.example.weathermate.util.NetworkManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavoritesViewModel(private val repository: Repository) : ViewModel() {

    private val TAG = "commonnn"

    val retrofitStateFavorites = MutableStateFlow<RetrofitStateFavorites>(RetrofitStateFavorites.Loading)


    fun getAllFavoriteAddresses() : List<FavoriteAddress> {
        return repository.getAllFavoriteAddresses()
    }

    suspend fun deleteFavoriteAddress(address: FavoriteAddress){
        repository.deleteFavoriteAddress(address)
    }

    fun putStringInSharedPreferences(key: String, stringInput: String){
        repository.putStringInSharedPreferences(key, stringInput)
    }

   /* fun observeFavorites() : LiveData<List<FavoriteAddress>>{
        return repository.getAllFavoriteAddresses()
    }*/

/*    fun loadAndReloadFavoritesOnline(favoriteAddresses: List<FavoriteAddress>) {


        var listFavoritesUpdated = mutableListOf<FavoriteAddress>()

        viewModelScope.launch(Dispatchers.IO) {

            if (favoriteAddresses.isNotEmpty()){

                if(NetworkManager.isInternetConnected()){

                    favoriteAddresses.forEach {currentFavoriteFromRepository ->
                        val data = if(repository.getStringFromSharedPreferences("language","")=="english") {
                            repository.getWeatherDataOnline(currentFavoriteFromRepository.latitude,
                                currentFavoriteFromRepository.longitude,
                                "en")
                        }  else{
                            repository.getWeatherDataOnline(currentFavoriteFromRepository.latitude,
                                currentFavoriteFromRepository.longitude,
                                "ar")
                        }

                        data.catch {
                            retrofitStateFavorites.value = RetrofitStateFavorites.OnFail(Throwable("Error retrieving data"))
                        }
                            .collectLatest{

                                val favoriteAddress = FavoriteAddress(
                                    address = currentFavoriteFromRepository.address,
                                    latitude = currentFavoriteFromRepository.latitude,
                                    longitude = currentFavoriteFromRepository.longitude,
                                    latlngString = currentFavoriteFromRepository.latlngString,
                                    currentTemp = it.current.temp,
                                    currentDescription = it.current.weather[0].description.capitalize(),
                                    lastCheckedTime = System.currentTimeMillis(),
                                    icon = it.current.weather[0].icon
                                )

                                listFavoritesUpdated.add(favoriteAddress)

                            }
                        retrofitStateFavorites.value = RetrofitStateFavorites.OnSuccess(listFavoritesUpdated)

                    }

                } else{
                    retrofitStateFavorites.value = RetrofitStateFavorites.OnSuccess(favoriteAddresses)
                }


            } else{
                retrofitStateFavorites.value = RetrofitStateFavorites.OnSuccess(emptyList())
            }







        }

    }*/

 /*   fun updateFavoritesDatabase(
        listFavoriteAddresses: List<FavoriteAddress>,
        viewLifecycleOwner: LifecycleOwner,
        favoritesObserver: Observer<List<FavoriteAddress>>
    ) {
        val job = viewModelScope.launch {
            repository.deleteAllFavoriteAddresses()
            listFavoriteAddresses.forEach {
                repository.insertFavoriteAddress(it)
            }
        }
        job.invokeOnCompletion {
            observeFavorites().observe(viewLifecycleOwner, favoritesObserver)
        }
    }*/

    fun loadFavorites() {
        var listFavoritesFromDB = emptyList<FavoriteAddress>()

        val job = viewModelScope.launch(Dispatchers.IO) {
            listFavoritesFromDB = repository.getAllFavoriteAddresses()
        }

        job.invokeOnCompletion {
            if (listFavoritesFromDB.isNotEmpty()){

                if(NetworkManager.isInternetConnected()){

                    var listFavoritesUpdated = mutableListOf<FavoriteAddress>()

                    viewModelScope.launch {
                        listFavoritesFromDB.forEach {currentFavoriteFromRepository ->
                            val data = if(repository.getStringFromSharedPreferences("language","")=="english") {
                                repository.getWeatherDataOnline(currentFavoriteFromRepository.latitude,
                                    currentFavoriteFromRepository.longitude,
                                    "en")
                            }  else{
                                repository.getWeatherDataOnline(currentFavoriteFromRepository.latitude,
                                    currentFavoriteFromRepository.longitude,
                                    "ar")
                            }

                            data.catch {
                                retrofitStateFavorites.value = RetrofitStateFavorites.OnFail(Throwable("Error retrieving data"))
                            }
                                .collectLatest{

                                    val favoriteAddress = FavoriteAddress(
                                        address = currentFavoriteFromRepository.address,
                                        latitude = currentFavoriteFromRepository.latitude,
                                        longitude = currentFavoriteFromRepository.longitude,
                                        latlngString = currentFavoriteFromRepository.latlngString,
                                        currentTemp = it.current.temp,
                                        currentDescription = it.current.weather[0].description.capitalize(),
                                        lastCheckedTime = System.currentTimeMillis(),
                                        icon = it.current.weather[0].icon
                                    )

                                    listFavoritesUpdated.add(favoriteAddress)

                                }
                            retrofitStateFavorites.value = RetrofitStateFavorites.OnSuccess(listFavoritesUpdated)

                        }
                    }


                } else{

                    retrofitStateFavorites.value = RetrofitStateFavorites.OnSuccess(listFavoritesFromDB)

                }
            } else{
                retrofitStateFavorites.value = RetrofitStateFavorites.OnSuccess(listFavoritesFromDB)
            }
        }
    }

}