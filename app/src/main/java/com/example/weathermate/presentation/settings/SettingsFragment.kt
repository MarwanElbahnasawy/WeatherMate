package com.example.weathermate.presentation.settings

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.example.weathermate.MyApp
import com.example.weathermate.R
import com.example.weathermate.databinding.FragmentSettingsBinding
import com.example.weathermate.presentation.map.MapManager
import com.example.weathermate.presentation.map.MapManagerInterface
import com.example.weathermate.util.NetworkManager
import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMap
import nl.joery.animatedbottombar.AnimatedBottomBar
import java.util.*


class SettingsFragment : Fragment() , MapManagerInterface {

    private val TAG = "commonnn"

    private val PERMISSION_LOCATION_ID_SETTINGS_FRAGMENT = 333

    lateinit var binding: FragmentSettingsBinding

    lateinit var mfusedLocationProviderClient: FusedLocationProviderClient
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
            binding.btnEnglish.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.myPurple))
        } else {
            binding.btnArabic.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.myPurple))
        }


        if (settingsViewModel.getStringFromSharedPreferences("temperature_unit", "").equals("celsius")) {
            binding.btnCelsius.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.myPurple))
        } else if (settingsViewModel.getStringFromSharedPreferences("temperature_unit", "").equals("kelvin")) {
            binding.btnKelvin.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.myPurple))
        } else {
            binding.btnFahrenheit.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.myPurple))
        }

        if (settingsViewModel.getStringFromSharedPreferences("wind_speed_unit", "").equals("mps")) {
            binding.btnMps.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.myPurple))
        } else {
            binding.btnMph.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.myPurple))
        }

        startLottieAnimation(binding.imageViewDummy , "settings2.json")
        startLottieAnimation(binding.imgGPS , "gps.json")
        startLottieAnimation(binding.imgMap , "earth.json")
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

        binding.btnEnglish.setOnClickListener {
            settingsViewModel.putStringInSharedPreferences("language", "english")
            binding.btnEnglish.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.myPurple))
            binding.btnArabic.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))

            changeLanguageAndLayout("en")

        }
        binding.btnArabic.setOnClickListener {
            settingsViewModel.putStringInSharedPreferences("language", "arabic")
            binding.btnArabic.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.myPurple))
            binding.btnEnglish.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))

            changeLanguageAndLayout("ar")

        }
    }

    private fun activateTemperatureClickListener() {

        binding.btnCelsius.setOnClickListener {
            settingsViewModel.putStringInSharedPreferences("temperature_unit", "celsius")
            binding.btnCelsius.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.myPurple))
            binding.btnKelvin.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
            binding.btnFahrenheit.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
        }
        binding.btnKelvin.setOnClickListener {
            settingsViewModel.putStringInSharedPreferences("temperature_unit", "kelvin")
            binding.btnKelvin.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.myPurple))
            binding.btnCelsius.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
            binding.btnFahrenheit.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
        }
        binding.btnFahrenheit.setOnClickListener {
            settingsViewModel.putStringInSharedPreferences("temperature_unit", "fahrenheit")
            binding.btnFahrenheit.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.myPurple))
            binding.btnCelsius.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
            binding.btnKelvin.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
        }
    }

    private fun activateWindClickListener() {
        binding.btnMps.setOnClickListener {
            settingsViewModel.putStringInSharedPreferences("wind_speed_unit", "mps")
            binding.btnMps.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.myPurple))
            binding.btnMph.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
        }
        binding.btnMph.setOnClickListener {
            settingsViewModel.putStringInSharedPreferences("wind_speed_unit", "mph")
            binding.btnMph.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.myPurple))
            binding.btnMps.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
        }
    }

    private fun activateLocationClickListener() {
        binding.imgGPS.setOnClickListener {

            if(NetworkManager.isInternetConnected()){
                mapManager.getLastLocation()
            } else{
                Toast.makeText(
                    requireContext(),
                    requireContext().getString(R.string.internetDisconnected),
                    Toast.LENGTH_SHORT
                ).show()
            }


        }
        binding.imgMap.setOnClickListener {

            if(NetworkManager.isInternetConnected()){
                findNavController().navigate(SettingsFragmentDirections.actionNavigationSettingsToMapFragment(isFromSettings = true))
            } else{
                Toast.makeText(
                    requireContext(),
                    requireContext().getString(R.string.internetDisconnected),
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
    }
    private fun startLottieAnimation(animationView: LottieAnimationView, animationName: String) {
        animationView.setAnimation(animationName)
        animationView.repeatCount = LottieDrawable.INFINITE
        animationView.repeatMode = LottieDrawable.RESTART
        animationView.playAnimation()
    }

    private fun changeLanguageAndLayout(language: String) {

        val locale = Locale(language)
        Locale.setDefault(locale)
        val resources = context?.resources
        val configuration = Configuration()
        configuration.setLocale(locale)
        resources?.updateConfiguration(configuration, resources.displayMetrics)

        ViewCompat.setLayoutDirection(requireActivity().window.decorView, if (language == "ar") ViewCompat.LAYOUT_DIRECTION_RTL else ViewCompat.LAYOUT_DIRECTION_LTR)

        val bottomNavView = requireActivity().findViewById<AnimatedBottomBar>(R.id.bottom_nav_view)
        bottomNavView.selectTabAt(0,false)

        settingsViewModel.putBooleanInSharedPreferences("isLayoutChangedBySettings", true)

        activity?.recreate()

    }

    override fun mapManagerRequestPermission() {
        requestPermission()
    }

    override fun mapManagerDealWithLocationResult(locationResult: LocationResult) {
        dealWithLocationResult(locationResult)
    }


}