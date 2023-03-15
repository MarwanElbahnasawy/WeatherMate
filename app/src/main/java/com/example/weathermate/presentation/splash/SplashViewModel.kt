package com.example.weathermate.presentation.splash

import androidx.lifecycle.ViewModel
import com.example.weathermate.data.InterfaceRepository

class SplashViewModel(private val repository: InterfaceRepository) : ViewModel(){
    fun isPreferencesSet(): Boolean {
        return repository.getBooleanFromSharedPreferences("preferences_set", false)
    }

    fun isDark(): Boolean {
        return repository.getBooleanFromSharedPreferences("isDarkTheme",false)
    }

}