package com.example.weathermate.presentation.map

import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng

interface MapManagerInterface {
    fun mapManagerRequestPermission()
    fun mapManagerDealWithLocationResult(locationResult: LocationResult)
}