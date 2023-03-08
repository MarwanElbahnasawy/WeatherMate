package com.example.weathermate.presentation.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*

class MapManager(private val context: Context, private val mapManagerInterface: MapManagerInterface) {
    private  val TAG = "commonnn"

    private lateinit var mfusedLocationProviderClient: FusedLocationProviderClient

    @SuppressLint("MissingPermission")
    fun getLastLocation() {
        Log.i(TAG, "getLastLocation: called")
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                requestNewLocationData()

            } else {
                Toast.makeText(context, "Turn on location", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                context.startActivity(intent)
            }
        } else {
            mapManagerInterface.mapManagerRequestPermission()
        }
    }

    @SuppressLint("MissingPermission")
    fun checkPermissionsAndIfLocationIsEnabled() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {

            } else {
                Toast.makeText(context, "Turn on location", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                context.startActivity(intent)
            }
        } else {
            mapManagerInterface.mapManagerRequestPermission()
        }
    }

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun isLocationEnabled(): Boolean {
        val mlocationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return mlocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && mlocationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mlocationRequest = LocationRequest()
        mlocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        mlocationRequest.setInterval(0)
        mfusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(context)
        mfusedLocationProviderClient.requestLocationUpdates(
            mlocationRequest, mlocationCallback, Looper.myLooper()
        )
    }



    private val mlocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {

            mfusedLocationProviderClient.removeLocationUpdates(this)
            mapManagerInterface.mapManagerDealWithLocationResult(locationResult)

        }
    }


}