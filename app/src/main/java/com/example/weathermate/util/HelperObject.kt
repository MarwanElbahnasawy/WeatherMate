package com.example.weathermate.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.preference.PreferenceManager

object HelperObject {

    private val TAG = "commonnn"

    private var sharedPreferencesInstance: SharedPreferences? = null

    fun createSharedPreferencesInstance(context: Context){
        if (sharedPreferencesInstance == null)
            sharedPreferencesInstance =  PreferenceManager.getDefaultSharedPreferences(context)
    }

    fun getSharedPreferencesInstance() : SharedPreferences{
        return sharedPreferencesInstance!!
    }


}