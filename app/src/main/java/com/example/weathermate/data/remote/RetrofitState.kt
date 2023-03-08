package com.example.weathermate.data.remote

import com.example.weathermate.data.model.WeatherData

sealed class RetrofitState {
    class onSuccess(val weatherData: WeatherData ):RetrofitState()
    class onFail(val errorMessage: Throwable ):RetrofitState()
    object Loading : RetrofitState()
}