package com.example.weathermate.presentation.alerts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weathermate.data.Repository

class AlertsViewModelFactory (private val repository: Repository) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(AlertsViewModel::class.java)){
            AlertsViewModel(repository) as T
        } else{
            throw java.lang.IllegalArgumentException("AlertsViewModel class not found")
        }
    }
}