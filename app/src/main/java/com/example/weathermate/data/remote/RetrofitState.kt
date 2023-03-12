package com.example.weathermate.data.remote

import com.example.weathermate.data.model.FavoriteAddress
import com.example.weathermate.data.model.WeatherData

sealed class RetrofitStateWeather {
    class OnSuccess(val weatherData: WeatherData ):RetrofitStateWeather()
    class OnFail(val errorMessage: Throwable ):RetrofitStateWeather()
    object Loading : RetrofitStateWeather()
}

sealed class RetrofitStateFavorites {
    class OnSuccess(val listFavoriteAddresses: List<FavoriteAddress> ):RetrofitStateFavorites()
    class OnFail(val errorMessage: Throwable ):RetrofitStateFavorites()
    object Loading : RetrofitStateFavorites()
}

