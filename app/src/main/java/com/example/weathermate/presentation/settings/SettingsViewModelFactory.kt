package com.example.weathermate.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weathermate.data.Repository

class SettingsViewModelFactory (private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(SettingsViewModel::class.java)){
            SettingsViewModel(repository) as T
        } else{
            throw java.lang.IllegalArgumentException("InitialPreferencesViewModel class not found")
        }
    }
}