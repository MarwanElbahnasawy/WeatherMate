package com.example.weathermate.presentation.settings

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.weathermate.MyApp
import com.example.weathermate.databinding.FragmentSettingsBinding
import com.example.weathermate.presentation.map.MapManager
import com.example.weathermate.presentation.map.MapManagerInterface
import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMap


class SettingsFragment : Fragment() , MapManagerInterface {

    private val TAG = "commonnn"

    private val PERMISSION_LOCATION_ID_SETTINGS_FRAGMENT = 333

    lateinit var binding: FragmentSettingsBinding

    lateinit var mfusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var myMap: GoogleMap
    private var longitudeDouble: Double = 0.0
    private var latitudeDouble: Double = 0.0

    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var mapManager: MapManager

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

        val settingsViewModelFactory = SettingsViewModelFactory(MyApp.getInstanceRepository())
        settingsViewModel = ViewModelProvider(this,settingsViewModelFactory)[SettingsViewModel::class.java]

        mapManager = MapManager(requireContext(), this)

        latitudeDouble = settingsViewModel.getStringFromSharedPreferences("latitude", "")?.toDouble() ?: 0.0
        latitudeDouble = settingsViewModel.getStringFromSharedPreferences("longitude", "")?.toDouble() ?: 0.0

        if (settingsViewModel.getStringFromSharedPreferences("language", "").equals("english")) {
            binding.rbEnglish.isChecked = true
        } else {
            binding.rbArabic.isChecked = true
        }


        if (settingsViewModel.getStringFromSharedPreferences("temperature_unit", "").equals("celsius")) {
            binding.rbCelsius.isChecked = true
        } else if (settingsViewModel.getStringFromSharedPreferences("temperature_unit", "").equals("kelvin")) {
            binding.rbKelvin.isChecked = true
        } else {
            binding.rbFahrenheit.isChecked = true
        }

        if (settingsViewModel.getStringFromSharedPreferences("wind_speed_unit", "").equals("mph")) {
            binding.rbMph.isChecked = true
        } else {
            binding.rbKph.isChecked = true
        }
    }

    private fun requestPermission() {

        requestPermissions(
            arrayOf<String>(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_LOCATION_ID_SETTINGS_FRAGMENT
        )
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_LOCATION_ID_SETTINGS_FRAGMENT) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }
        }
    }

    private fun dealWithLocationResult(locationResult: LocationResult) {
        val mLastLocation: Location = locationResult.lastLocation
        longitudeDouble = mLastLocation.longitude
        latitudeDouble = mLastLocation.latitude

        settingsViewModel.putStringInSharedPreferences("latitude", latitudeDouble.toString())
        settingsViewModel.putStringInSharedPreferences("longitude", longitudeDouble.toString())

    }


    private fun activateLanguageClickListener() {
        binding.rbEnglish.setOnClickListener {
            Log.i(TAG, "activateLanguageClickListener: english at activation")
            settingsViewModel.putStringInSharedPreferences("language", "english")
        }
        binding.rbArabic.setOnClickListener {
            settingsViewModel.putStringInSharedPreferences("language", "arabic")
        }
    }

    private fun activateTemperatureClickListener() {
        binding.rbCelsius.setOnClickListener {
            settingsViewModel.putStringInSharedPreferences("temperature_unit", "celsius")
        }
        binding.rbKelvin.setOnClickListener {
            settingsViewModel.putStringInSharedPreferences("temperature_unit", "kelvin")
        }
        binding.rbFahrenheit.setOnClickListener {
            settingsViewModel.putStringInSharedPreferences("temperature_unit", "fahrenheit")
        }
    }

    private fun activateWindClickListener() {
        binding.rbMph.setOnClickListener {
            settingsViewModel.putStringInSharedPreferences("wind_speed_unit", "mph")
        }
        binding.rbKph.setOnClickListener {
            settingsViewModel.putStringInSharedPreferences("wind_speed_unit", "kph")
        }
    }

    private fun activateLocationClickListener() {
        binding.rbGps.setOnClickListener {
            mapManager.getLastLocation()
        }
        binding.rbMap.setOnClickListener {
            findNavController().navigate(SettingsFragmentDirections.actionNavigationSettingsToMapFragment(isFromSettings = true))
        }
    }

    override fun mapManagerRequestPermission() {
        requestPermission()
    }

    override fun mapManagerDealWithLocationResult(locationResult: LocationResult) {
        dealWithLocationResult(locationResult)
    }


}