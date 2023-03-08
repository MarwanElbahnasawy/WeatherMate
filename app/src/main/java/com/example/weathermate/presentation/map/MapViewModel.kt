package com.example.weathermate.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathermate.data.model.FavoriteAddress
import com.example.weathermate.data.model.MapsAutoCompleteResponse
import com.example.weathermate.data.Repository
import kotlinx.coroutines.launch

class MapViewModel(private val repository: Repository) : ViewModel() {

    private  val TAG = "commonnn"

    fun insertFavoriteAddress(favoriteAddress: FavoriteAddress) {
        viewModelScope.launch {
            repository.insertFavoriteAddress(favoriteAddress)
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




}