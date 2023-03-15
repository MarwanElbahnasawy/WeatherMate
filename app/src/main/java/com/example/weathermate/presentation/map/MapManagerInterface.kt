package com.example.weathermate.presentation.map

import com.google.android.gms.location.LocationResult

interface MapManagerInterface {
    fun mapManagerRequestPermission()
    fun mapManagerDealWithLocationResult(locationResult: LocationResult)
}