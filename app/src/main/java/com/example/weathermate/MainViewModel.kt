package com.example.weathermate

import androidx.appcompat.app.AppCompatActivity
import com.example.weathermate.alerts.view.AlertsManager
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathermate.data.Repository

class MainViewModel(private val repository: Repository) : ViewModel() {

    fun activateAlerts(activity: AppCompatActivity) {
        val alertsManager = AlertsManager(activity)
        viewModelScope.launch {
            repository.getAllAlerts().observe(activity) { it ->
                it.forEach {
                    alertsManager.fireAlert(it)
                }
            }
        }
    }
}