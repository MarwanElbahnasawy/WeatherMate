package com.example.weathermate

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.example.weathermate.databinding.FragmentSettingsBinding
import com.example.weathermate.initial_preferences.PERMISSION_LOCATION_ID
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.*
import java.util.*


class SettingsFragment : Fragment() {

    private val TAG = "commonnn"

    lateinit var binding: FragmentSettingsBinding

    lateinit var mfusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var myMap: GoogleMap
    private var longitudeDouble: Double = 0.0
    private var latitudeDouble: Double = 0.0

    lateinit var sharedPreferences: SharedPreferences
    lateinit var sharedPreferencesEditor: SharedPreferences.Editor

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initFrag()

        //will remove from here later
        mfusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        activateLanguageClickListener()

        activateTemperatureClickListener()

        activateWindClickListener()

        activateLocationClickListener()

    }


    private fun initFrag() {

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        sharedPreferencesEditor = sharedPreferences.edit()

        latitudeDouble = sharedPreferences.getString("latitude", null)?.toDouble() ?: 0.0
        latitudeDouble = sharedPreferences.getString("longitude", null)?.toDouble() ?: 0.0

        if (sharedPreferences.getString("language", null).equals("english")) {
            binding.rbEnglish.isChecked = true
        } else {
            binding.rbArabic.isChecked = true
        }


        if (sharedPreferences.getString("temperature_unit", null).equals("celsius")) {
            binding.rbCelsius.isChecked = true
        } else if (sharedPreferences.getString("temperature_unit", null).equals("kelvin")) {
            binding.rbKelvin.isChecked = true
        } else {
            binding.rbFahrenheit.isChecked = true
        }

        if (sharedPreferences.getString("wind_speed_unit", null).equals("mph")) {
            binding.rbMph.isChecked = true
        } else {
            binding.rbKph.isChecked = true
        }
    }


    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        Log.i(TAG, "getLastLocation: called")
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                requestNewLocationData()

            } else {
                Toast.makeText(requireContext(), "Turn on location", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermission()
        }
    }

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun isLocationEnabled(): Boolean {
        val mlocationManager =
            requireContext().getSystemService(LOCATION_SERVICE) as LocationManager
        return mlocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && mlocationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }


    private fun requestPermission() {

        requestPermissions(
            arrayOf<String>(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_LOCATION_ID
        )
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_LOCATION_ID) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        Log.i(TAG, "requestNewLocationData: called")
        val mlocationRequest = LocationRequest()
        mlocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        mlocationRequest.setInterval(0)
        mfusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        mfusedLocationProviderClient.requestLocationUpdates(
            mlocationRequest, mlocationCallback, Looper.myLooper()
        )
    }

    private val mlocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            Log.i(TAG, "onLocationResult: called")
            val mLastLocation: Location = locationResult.lastLocation
            longitudeDouble = mLastLocation.longitude
            latitudeDouble = mLastLocation.latitude

            mfusedLocationProviderClient.removeLocationUpdates(this)

            sharedPreferencesEditor.putString("latitude", latitudeDouble.toString())
            sharedPreferencesEditor.putString("longitude", longitudeDouble.toString())

            sharedPreferencesEditor.apply()


        }
    }


    @SuppressLint("MissingPermission")
    private fun setUpMapUsingLocationString(locationName: String) {
        Log.i(TAG, "setUpMapUsingLocationString: called")
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        if (isAdded) {
            val addresses = geocoder.getFromLocationName(locationName, 1)
            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    latitudeDouble = addresses[0].latitude
                    longitudeDouble = addresses[0].longitude
                    Log.i(TAG, "setUpMapUsingLocationString: " + latitudeDouble)
                    Log.i(TAG, "setUpMapUsingLocationString: " + longitudeDouble)
                    val latLng = LatLng(latitudeDouble, longitudeDouble)
                    if (!latLng.equals(myMap.cameraPosition.target)) {
                        myMap.clear()
                        myMap.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(latLng, 16f),
                            1500,
                            null
                        )
                        myMap.addMarker(MarkerOptions().position(latLng))
                    }
                } else {
                }
            }
        }

    }

    private fun activateLanguageClickListener() {
        binding.rbEnglish.setOnClickListener {
            Log.i(TAG, "activateLanguageClickListener: english at activation")
            sharedPreferencesEditor.putString("language", "english")
            sharedPreferencesEditor.apply()
        }
        binding.rbArabic.setOnClickListener {
            sharedPreferencesEditor.putString("language", "arabic")
            sharedPreferencesEditor.apply()
        }
    }

    private fun activateTemperatureClickListener() {
        binding.rbCelsius.setOnClickListener {
            sharedPreferencesEditor.putString("temperature_unit", "celsius")
            sharedPreferencesEditor.apply()
        }
        binding.rbKelvin.setOnClickListener {
            sharedPreferencesEditor.putString("temperature_unit", "kelvin")
            sharedPreferencesEditor.apply()
        }
        binding.rbFahrenheit.setOnClickListener {
            sharedPreferencesEditor.putString("temperature_unit", "fahrenheit")
            sharedPreferencesEditor.apply()
        }
    }

    private fun activateWindClickListener() {
        binding.rbMph.setOnClickListener {
            sharedPreferencesEditor.putString("wind_speed_unit", "mph")
            sharedPreferencesEditor.apply()
        }
        binding.rbKph.setOnClickListener {
            sharedPreferencesEditor.putString("wind_speed_unit", "kph")
            sharedPreferencesEditor.apply()
        }
    }

    private fun activateLocationClickListener() {
        binding.rbGps.setOnClickListener {
            getLastLocation()
        }
        binding.rbMap.setOnClickListener {
            findNavController().navigate(SettingsFragmentDirections.actionNavigationSettingsToMapFragment(isFromSettings = true))
        }
    }


}