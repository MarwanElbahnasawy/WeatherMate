package com.example.weathermate.presentation.initial_preferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weathermate.data.Repository

class InitialPreferencesViewModelFactory (private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(InitialPreferencesViewModel::class.java)){
            InitialPreferencesViewModel(repository) as T
        } else{
            throw java.lang.IllegalArgumentException("InitialPreferencesViewModel class not found")
        }
    }
}