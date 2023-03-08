package com.example.weathermate.presentation.settings

import androidx.lifecycle.ViewModel
import com.example.weathermate.data.Repository

class SettingsViewModel(private val repository: Repository) : ViewModel() {

    private val TAG = "commonnn"

    fun putStringInSharedPreferences(key: String, stringInput: String){
        repository.putStringInSharedPreferences(key, stringInput)
    }

    fun getStringFromSharedPreferences(key: String, stringDefault: String) : String{
        return repository.getStringFromSharedPreferences(key, stringDefault)
    }
}