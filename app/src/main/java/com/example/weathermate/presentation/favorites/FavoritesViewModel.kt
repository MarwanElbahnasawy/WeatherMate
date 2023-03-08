package com.example.weathermate.presentation.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.weathermate.data.model.FavoriteAddress
import com.example.weathermate.data.Repository

class FavoritesViewModel(private val repository: Repository) : ViewModel() {

    fun getAllFavoriteAddresses() : LiveData<List<FavoriteAddress>> {
        return repository.getAllFavoriteAddresses()
    }

    suspend fun deleteFavoriteAddress(address: FavoriteAddress){
        repository.deleteFavoriteAddress(address)
    }

    fun putStringInSharedPreferences(key: String, stringInput: String){
        repository.putStringInSharedPreferences(key, stringInput)
    }

}