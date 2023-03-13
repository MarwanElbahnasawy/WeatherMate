package com.example.weathermate.presentation.alerts

import android.app.AlarmManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathermate.data.model.AlertItem
import com.example.weathermate.data.Repository
import kotlinx.coroutines.launch

class AlertsViewModel(private val repository: Repository) : ViewModel(){

    fun insertAlert(alertItem: AlertItem) {
        viewModelScope.launch {
            repository.insertAlert(alertItem)
        }
    }

    fun getAllAlerts(): LiveData<List<AlertItem>> {
        return repository.getAllAlerts()
    }

    suspend fun deleteAlert(alert: AlertItem){
        repository.deleteAlert(alert)
    }

    fun putStringInSharedPreferences(key: String, stringInput: String){
        repository.putStringInSharedPreferences(key, stringInput)
    }

    fun getStringFromSharedPreferences(key: String, stringDefault: String) : String{
        return repository.getStringFromSharedPreferences(key, stringDefault)
    }

    fun putBooleanInSharedPreferences(key: String, booleanInput: Boolean){
        repository.putBooleanInSharedPreferences(key, booleanInput)
    }

    fun getBooleanFromSharedPreferences(key: String, booleanDefault: Boolean): Boolean{
        return repository.getBooleanFromSharedPreferences(key, booleanDefault)
    }

}