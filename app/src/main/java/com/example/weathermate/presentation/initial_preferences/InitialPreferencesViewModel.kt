package com.example.weathermate.presentation.initial_preferences


import androidx.lifecycle.ViewModel
import com.example.weathermate.data.InterfaceRepository
import com.example.weathermate.data.model.MapsAutoCompleteResponse

class InitialPreferencesViewModel(
    private val repository: InterfaceRepository
) : ViewModel() {

    fun putStringInSharedPreferences(key: String, stringInput: String) {
        repository.putStringInSharedPreferences(key, stringInput)
    }

    fun putBooleanInSharedPreferences(key: String, booleanInput: Boolean) {
        repository.putBooleanInSharedPreferences(key, booleanInput)
    }

    suspend fun getMapsAutoCompleteResponse(textInput: CharSequence?): MapsAutoCompleteResponse {
        return repository.getMapsAutoCompleteResponse(textInput)
    }

    fun setIsDarkTrue() {
        repository.putBooleanInSharedPreferences("isDarkTheme" , true)
    }

}





