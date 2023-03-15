package com.example.weathermate

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathermate.alerts.view.AlertsManager
import com.example.weathermate.data.InterfaceRepository
import kotlinx.coroutines.launch

class MainViewModel(private val repository: InterfaceRepository) : ViewModel() {

    private val TAG = "commonnn"

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

    fun putBooleanInSharedPreferences(key: String, boolean: Boolean){
        repository.putBooleanInSharedPreferences(key, boolean)
    }

    fun isArabic(): Boolean {
        return repository.getStringFromSharedPreferences("language","") == "arabic"
    }

    fun isLayoutChangedBySettings(): Boolean {
        return repository.getBooleanFromSharedPreferences("isLayoutChangedBySettings", false)
    }

    fun isThemeChangedBySettings(): Boolean {
        return repository.getBooleanFromSharedPreferences("isThemeChangedBySettings",false)
    }

    fun setIsThemeChangedBySettingsToFalse() {
        repository.putBooleanInSharedPreferences("isThemeChangedBySettings",false)
    }
}