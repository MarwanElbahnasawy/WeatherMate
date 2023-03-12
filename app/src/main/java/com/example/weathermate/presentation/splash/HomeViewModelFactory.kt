package com.example.weathermate.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weathermate.data.Repository

class SplashViewModelFactory (private val repository: Repository) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(SplashViewModel::class.java)){
            SplashViewModel(repository) as T
        } else{
            throw java.lang.IllegalArgumentException("AlertsViewModel class not found")
        }
    }
}