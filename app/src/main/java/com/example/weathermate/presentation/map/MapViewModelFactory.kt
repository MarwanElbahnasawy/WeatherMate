package com.example.weathermate.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weathermate.data.Repository

class MapViewModelFactory (private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(MapViewModel::class.java)){
            MapViewModel(repository) as T
        } else{
            throw java.lang.IllegalArgumentException("InitialPreferencesViewModel class not found")
        }
    }
}