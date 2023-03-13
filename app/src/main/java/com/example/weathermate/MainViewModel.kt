package com.example.weathermate

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.weathermate.alerts.view.AlertsManager
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathermate.data.Repository
import java.util.*

class MainViewModel(private val repository: Repository) : ViewModel() {

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

    fun getLangaugeInSharedPreference(): String {
        return repository.getStringFromSharedPreferences("language","")
    }

}