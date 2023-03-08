package com.example.weathermate.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weathermate.data.Repository

class FavoritesViewModelFactory (private val repository: Repository) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(FavoritesViewModel::class.java)){
            FavoritesViewModel(repository) as T
        } else{
            throw java.lang.IllegalArgumentException("AlertsViewModel class not found")
        }
    }
}