package com.example.weathermate.presentation.initial_preferences

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weathermate.MyApp
import com.example.weathermate.R
import com.example.weathermate.databinding.FragmentInitialPreferencesBinding
import com.example.weathermate.presentation.map.MapManager
import com.example.weathermate.presentation.map.MapManagerInterface
import com.example.weathermate.presentation.map.SearchSuggestionAdapter
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.*
import java.util.*


class InitialPreferencesFragment : Fragment(), MapManagerInterface {

    private val TAG = "commonnn"

    private val PERMISSION_LOCATION_ID_INITIAL_PREFERENCES = 111

    lateinit var binding: FragmentInitialPreferencesBinding

    private lateinit var myMap: GoogleMap
    private var longitudeDouble: Double = 0.0
    private var latitudeDouble: Double = 0.0

    lateinit var geocoder: Geocoder

    private var isLocationSelectedFromMap = false
    private var isClearing = false

    private lateinit var initialPreferencesViewModel: InitialPreferencesViewModel
    private lateinit var mapManager: MapManager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentInitialPreferencesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val initialPreferencesViewModelFactory =
            InitialPreferencesViewModelFactory(MyApp.getInstanceRepository())
        initialPreferencesViewModel = ViewModelProvider(
            this,
            initialPreferencesViewModelFactory
        )[InitialPreferencesViewModel::class.java]

        initFrag()

        activateGPSAndMapRadioButtonsClickListeners()

        mapManager.checkPermissionsAndIfLocationIsEnabled()

        val layoutManager = LinearLayoutManager(context)
        binding.rvSearchSuggestions.layoutManager = layoutManager

        initializeMapFragment()

        activateSaveButtonListener()

        activateImgCurrentLocation()

        activateSearchIconListener()

        activateClearSearchIconListener()

        addTextChangedListenerToTheSearchEditText()

    }


    private fun initFrag() {
        mapManager = MapManager(requireContext(), this)
        geocoder = Geocoder(requireContext(), Locale.getDefault())
    }

    private fun activateGPSAndMapRadioButtonsClickListeners() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        binding.rbMap.setOnClickListener {
            if (binding.rbMap.isChecked) {
                showMapComponents(mapFragment.requireView(), true)
            }
        }

        binding.rbGps.setOnClickListener {
            if (binding.rbGps.isChecked) {
                mapManager.checkPermissionsAndIfLocationIsEnabled()
                // initialPreferencesViewModel.checkPermissionsAndIfLocationIsEnabled()
                showMapComponents(mapFragment.requireView(), false)
            }
        }
    }

    private fun showMapComponents(mapView: View, show: Boolean) {
        mapView.visibility = if (show) View.VISIBLE else View.GONE
        binding.etSearchMap.visibility = if (show) View.VISIBLE else View.GONE
        binding.imgCurrentLocation.visibility = if (show) View.VISIBLE else View.GONE
        binding.imgClearSearch.visibility = if (show) View.VISIBLE else View.GONE
        binding.imgSearchIcon.visibility = if (show) View.VISIBLE else View.GONE

        binding.imageViewDummy.visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun initializeMapFragment() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync {
            myMap = it
            mapFragment.view?.visibility = View.GONE
            binding.etSearchMap.visibility = View.GONE
            binding.imgCurrentLocation.visibility = View.GONE
            binding.imgClearSearch.visibility = View.GONE
            binding.imgSearchIcon.visibility = View.GONE

            myMap.setOnMapClickListener { latLng ->
                setUpMapOnClick(latLng)
                // initialPreferencesViewModel.setUpMapOnClick(latLng)
            }
        }
    }


    private fun setUpMapOnClick(latLng: LatLng) {

        isLocationSelectedFromMap = true

        latitudeDouble = latLng.latitude
        longitudeDouble = latLng.longitude

        moveMap(latLng)
        convertLatLonToAddressAndSetSearchText(latLng)
    }

    private fun requestPermission() {

        requestPermissions(
            arrayOf<String>(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_LOCATION_ID_INITIAL_PREFERENCES
        )
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_LOCATION_ID_INITIAL_PREFERENCES) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }
        }
    }


    private fun dealWithLocationResult(locationResult: LocationResult) {
        val mLastLocation: Location = locationResult.lastLocation
        longitudeDouble = mLastLocation.longitude
        latitudeDouble = mLastLocation.latitude

        if (binding.rbGps.isChecked) {
            savePreferencesAndGoToHomeFragment()
        } else if (binding.rbMap.isChecked) { //from img current location clicking

            isLocationSelectedFromMap = true

            val latLng = LatLng(latitudeDouble, longitudeDouble)
            moveMap(latLng)
            convertLatLonToAddressAndSetSearchText(latLng)

        }
    }

    private fun addTextChangedListenerToTheSearchEditText() {
        binding.etSearchMap.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                if (!isClearing) {
                    binding.rvSearchSuggestions.visibility = View.VISIBLE
                    binding.rvSearchSuggestions.setBackgroundColor(Color.WHITE)
                    binding.rvSearchSuggestions.alpha = 0.8f
                }
                isClearing = false

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                lifecycleScope.launch(Dispatchers.IO) {
                    val mapsAutoCompleteResponse =
                        initialPreferencesViewModel.getMapsAutoCompleteResponse(s)
                    val predictions = mapsAutoCompleteResponse.predictions
                    val suggestions = mutableListOf<String>()
                    for (i in predictions.indices) {
                        suggestions.add(predictions[i].description)
                    }
                    withContext(Dispatchers.Main) {
                        binding.rvSearchSuggestions.adapter =
                            SearchSuggestionAdapter(
                                suggestions,
                                object : InterfaceInitialPreferences {
                                    override fun onItemClickInitialPreferences(suggestionSelected: String) {
                                        binding.etSearchMap.text = Editable.Factory.getInstance()
                                            .newEditable(suggestionSelected)
                                        binding.rvSearchSuggestions.visibility = View.GONE
                                        setUpMapUsingLocationString(
                                            suggestionSelected
                                        )
                                        isLocationSelectedFromMap = true
                                    }
                                })

                    }
                }

            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    @SuppressLint("MissingPermission")
    fun setUpMapUsingLocationString(locationName: String) {
        val addresses = geocoder.getFromLocationName(locationName, 1)
        if (addresses != null) {
            if (addresses.isNotEmpty()) {

                val latLng = LatLng(addresses[0].latitude, addresses[0].longitude)
                moveMap(latLng)

            }
        }
    }


    private fun activateSaveButtonListener() {

        binding.btnSavePreferences.setOnClickListener {

            if (binding.rbGps.isChecked) {
                mapManager.getLastLocation()
            } else {
                if (isLocationSelectedFromMap) {
                    isLocationSelectedFromMap = false
                    savePreferencesAndGoToHomeFragment()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "You haven't selected a location.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }

        }

    }

    private fun savePreferencesAndGoToHomeFragment() {
        val selectedLanguage = when (binding.rgLanguage.checkedRadioButtonId) {
            R.id.rb_english -> "english"
            R.id.rb_arabic -> "arabic"
            else -> null
        }



        initialPreferencesViewModel.putBooleanInSharedPreferences("preferences_set", true)
        initialPreferencesViewModel.putStringInSharedPreferences("temperature_unit", "celsius")
        initialPreferencesViewModel.putStringInSharedPreferences("wind_speed_unit", "mph")
        initialPreferencesViewModel.putStringInSharedPreferences("language", selectedLanguage!!)
        initialPreferencesViewModel.putStringInSharedPreferences(
            "latitude",
            latitudeDouble.toString()
        )
        initialPreferencesViewModel.putStringInSharedPreferences(
            "longitude",
            longitudeDouble.toString()
        )


        val action =
            InitialPreferencesFragmentDirections.actionPreferencesFragmentToNavigationHome()
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.mobile_navigation, true)
            .build()
        findNavController().navigate(action, navOptions)

    }

    private fun activateImgCurrentLocation() {
        binding.imgCurrentLocation.setOnClickListener {
            if (mapManager.isLocationEnabled()) {
                mapManager.getLastLocation()
            } else {
                Toast.makeText(requireContext(), "Turn on location", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        }

    }

    private fun activateSearchIconListener() {
        binding.imgSearchIcon.setOnClickListener {
            //setUpMapUsingLocationString(binding.etSearchMap.text.toString())
            setUpMapUsingLocationString(binding.etSearchMap.text.toString())
            binding.rvSearchSuggestions.adapter =
                SearchSuggestionAdapter(mutableListOf(), object : InterfaceInitialPreferences {})
        }

    }


    private fun activateClearSearchIconListener() {
        binding.imgClearSearch.setOnClickListener {
            isClearing = true
            binding.etSearchMap.text = Editable.Factory.getInstance().newEditable("")
        }
    }

    private fun moveMap(latLng: LatLng) {
        latitudeDouble = latLng.latitude
        longitudeDouble = latLng.longitude
        val markerOptions = MarkerOptions().position(latLng)
        myMap.clear()
        myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f), 1500, null)
        myMap.addMarker(markerOptions)
    }

    private fun convertLatLonToAddressAndSetSearchText(
        latLng: LatLng
    ) {
        latitudeDouble = latLng.latitude
        longitudeDouble = latLng.longitude
        val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
        if (addresses != null) {
            val address = addresses[0].getAddressLine(0)
            binding.etSearchMap.text = Editable.Factory.getInstance().newEditable(address)
            binding.rvSearchSuggestions.visibility = View.GONE
        }
    }


    override fun onPause() {
        super.onPause()
        binding.etSearchMap.text = Editable.Factory.getInstance().newEditable("")
    }

    override fun mapManagerRequestPermission() {
        requestPermission()
    }

    override fun mapManagerDealWithLocationResult(locationResult: LocationResult) {
        dealWithLocationResult(locationResult)
    }

}