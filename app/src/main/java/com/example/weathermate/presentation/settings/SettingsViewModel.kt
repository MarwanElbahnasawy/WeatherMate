package com.example.weathermate.presentation.settings

import androidx.lifecycle.ViewModel
import com.example.weathermate.data.InterfaceRepository

class SettingsViewModel(private val repository: InterfaceRepository) : ViewModel() {

    private val TAG = "commonnn"

    fun putStringInSharedPreferences(key: String, stringInput: String){
        repository.putStringInSharedPreferences(key, stringInput)
    }

    fun putBooleanInSharedPreferences(key: String, boolean: Boolean){
        repository.putBooleanInSharedPreferences(key, boolean)
    }

    fun getStringFromSharedPreferences(key: String, stringDefault: String) : String{
        return repository.getStringFromSharedPreferences(key, stringDefault)
    }

    fun checkIsDarkSharedPreferences(): Boolean{
        return repository.getBooleanFromSharedPreferences("isDarkTheme", false)
    }

}