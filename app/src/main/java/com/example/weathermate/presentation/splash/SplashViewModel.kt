package com.example.weathermate.presentation.splash

import androidx.lifecycle.ViewModel
import com.example.weathermate.data.Repository

class SplashViewModel(private val repository: Repository) : ViewModel(){
    fun isPreferencesSet(): Boolean {
        return repository.getBooleanFromSharedPreferences("preferences_set", false)
    }

}