package com.example.weathermate.util

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.example.weathermate.R

object MyHelper {

    private val TAG = "commonnn"

    private var sharedPreferencesInstance: SharedPreferences? = null

    fun createSharedPreferencesInstance(context: Context){
        if (sharedPreferencesInstance == null)
            sharedPreferencesInstance =  PreferenceManager.getDefaultSharedPreferences(context)
    }

    fun getSharedPreferencesInstance() : SharedPreferences{
        return sharedPreferencesInstance!!
    }

    fun getWeatherLottie(icon: String): String {
        when (icon){
            "01d" -> return "i01d.json"
            "02d" -> return "i02d.json"
            "03d" -> return "i03d.json"
            "04d" -> return "i04d.json"
            "09d" -> return "i09d.json"
            "10d" -> return "i10d.json"
            "11d" -> return "i11d.json"
            "13d" -> return "i13d.json"
            "50d" -> return "i50d.json"
            "01n" -> return "i01n.json"
            "02n" -> return "i02n.json"
            "03n" -> return "i03n.json"
            "04n" -> return "i04n.json"
            "09n" -> return "i09n.json"
            "10n" -> return "i10n.json"
            "11n" -> return "i11n.json"
            "13n" -> return "i13n.json"
            "50n" -> return "i50n.json"
                    else -> return ""
        }
    }

    fun getWeatherImage(icon: String): Int {
        when (icon){
            "01d" -> return R.drawable.i01d
            "02d" -> return R.drawable.i02d
            "03d" -> return R.drawable.i03d
            "04d" -> return R.drawable.i04d
            "09d" -> return R.drawable.i09d
            "10d" -> return R.drawable.i10d
            "11d" -> return R.drawable.i11d
            "13d" -> return R.drawable.i13d
            "50d" -> return R.drawable.i50d
            "01n" -> return R.drawable.i01n
            "02n" -> return R.drawable.i02n
            "03n" -> return R.drawable.i03n
            "04n" -> return R.drawable.i04n
            "09n" -> return R.drawable.i09n
            "10n" -> return R.drawable.i10n
            "11n" -> return R.drawable.i11n
            "13n" -> return R.drawable.i13n
            "50n" -> return R.drawable.i50n
            else -> return R.drawable.baseline_add_alert_24
        }
    }

//    fun getWeatherVector(icon: String): Int {
//        when (icon){
//            "01d" -> return R.drawable.i01d
//            "02d" -> return R.drawable.i02d
//            "03d" -> return R.drawable.i03d
//            "04d" -> return R.drawable.i04d
//            "09d" -> return R.drawable.i09d
//            "10d" -> return R.drawable.i10d
//            "11d" -> return R.drawable.i11d
//            "13d" -> return R.drawable.i13d
//            "50d" -> return R.drawable.i50d
//            "01n" -> return R.drawable.i01n
//            "02n" -> return R.drawable.i02n
//            "03n" -> return R.drawable.i03n
//            "04n" -> return R.drawable.i04n
//            "09n" -> return R.drawable.i09n
//            "10n" -> return R.drawable.i10n
//            "11n" -> return R.drawable.i11n
//            "13n" -> return R.drawable.i13n
//            "50n" -> return R.drawable.i50n
//            else -> return R.drawable.baseline_add_alert_24
//        }
//    }


}