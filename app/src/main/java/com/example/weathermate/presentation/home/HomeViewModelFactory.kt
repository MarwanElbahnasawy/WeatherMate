package com.example.weathermate.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weathermate.data.Repository

class HomeViewModelFactory (private val repository: Repository) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(HomeViewModel::class.java)){
            HomeViewModel(repository) as T
        } else{
            throw java.lang.IllegalArgumentException("AlertsViewModel class not found")
        }
    }
}