package com.example.weathermate.presentation.initial_preferences

import android.Manifest
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
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
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.example.weathermate.MyApp
import com.example.weathermate.R
import com.example.weathermate.databinding.FragmentInitialPreferencesBinding
import com.example.weathermate.presentation.map.MapManager
import com.example.weathermate.presentation.map.MapManagerInterface
import com.example.weathermate.presentation.map.SearchSuggestionAdapter
import com.example.weathermate.util.NetworkManager
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.*
import nl.joery.animatedbottombar.AnimatedBottomBar
import java.util.*


class InitialPreferencesFragment : Fragment(), MapManagerInterface {

    private val TAG = "commonnn"

    private val PERMISSION_LOCATION_ID_INITIAL_PREFERENCES = 111
    private lateinit var binding: FragmentInitialPreferencesBinding
    private lateinit var myMap: GoogleMap
    private var longitudeDouble: Double = 0.0
    private var latitudeDouble: Double = 0.0
    lateinit var geocoder: Geocoder
    private var isLocationSelectedFromMap = false
    private var isClearing = false
    private lateinit var initialPreferencesViewModel: InitialPreferencesViewModel
    private lateinit var mapManager: MapManager
    private var selectedLanguage = ""
    private var isMapSelected = false
    private var isGPSSelected = false


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

        activateButtonsListeners()

        mapManager.checkPermissionsAndIfLocationIsEnabled()

        val layoutManager = LinearLayoutManager(context)
        binding.rvSearchSuggestions.layoutManager = layoutManager

        initializeMapFragment()

        activateSaveButtonListener()

        activateImgCurrentLocation()

        activateSearchIconListener()

        activateClearSearchIconListener()

        addTextChangedListenerToTheSearchEditText()

        disableOrEnableSearchBasedOnInternet()

    }

    private fun disableOrEnableSearchBasedOnInternet() {
        lifecycleScope.launch(Dispatchers.Main) {
            while (true) {
                if (NetworkManager.isInternetConnected()) {
                    binding.etSearchMap.isEnabled = true
                    binding.imgClearSearch.isEnabled = true
                } else {
                    binding.etSearchMap.isEnabled = false
                    binding.imgClearSearch.isEnabled = false
                    binding.rvSearchSuggestions.visibility = View.GONE
                }
                delay(250)
            }
        }
    }

    private fun activateButtonsListeners() {

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        binding.btnEnglish.setOnClickListener {
            binding.btnEnglish.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.myPurple
                )
            )
            binding.btnArabic.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
            selectedLanguage = "english"
        }
        binding.btnArabic.setOnClickListener {
            binding.btnArabic.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.myPurple
                )
            )
            binding.btnEnglish.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
            selectedLanguage = "arabic"
        }


        binding.btnMap.setOnClickListener {
            binding.btnMap.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.myPurple
                )
            )
            binding.btnGPS.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )

            showMapComponents(mapFragment.requireView(), true)
            isMapSelected = true
            isGPSSelected = false
        }

        binding.btnGPS.setOnClickListener {
            binding.btnGPS.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.myPurple
                )
            )
            binding.btnMap.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )

            mapManager.checkPermissionsAndIfLocationIsEnabled()
            showMapComponents(mapFragment.requireView(), false)
            isGPSSelected = true
            isMapSelected = false
        }
    }


    private fun initFrag() {
        mapManager = MapManager(requireContext(), this)
        geocoder = Geocoder(requireContext(), Locale.getDefault())

        startLottieAnimation(binding.imageViewDummy, "settings2.json")

        startLottieAnimation(binding.imgSearchIcon, "search.json")
        startLottieAnimation(binding.imgClearSearch, "delete.json")

        val animator = ObjectAnimator.ofFloat(binding.imgCurrentLocation, "rotation", 0f, 360f)
        animator.repeatCount = ValueAnimator.INFINITE
        animator.duration = 2000
        animator.start()
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

                if (NetworkManager.isInternetConnected()) {
                    setUpMapOnClick(latLng)
                } else {
                    Toast.makeText(
                        requireContext(),
                        requireContext().getText(R.string.internetDisconnected),
                        Toast.LENGTH_SHORT
                    ).show()
                }
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

        if (isGPSSelected) {
            savePreferencesAndGoToHomeFragment()
        } else if (isMapSelected) { //from img current location clicking

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
        if (addresses != null && addresses.isNotEmpty()) {
            val latLng = LatLng(addresses[0].latitude, addresses[0].longitude)
            moveMap(latLng)
        }
    }

    private fun activateSaveButtonListener() {

        binding.btnSavePreferences.setOnClickListener {

            if(NetworkManager.isInternetConnected()){
                if ((isGPSSelected || isLocationSelectedFromMap) && !selectedLanguage.equals("")) {
                    if (isGPSSelected) {
                        binding.btnSavePreferences.setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.myPurple
                            )
                        )
                        mapManager.getLastLocation()
                    } else {
                        if (isLocationSelectedFromMap) {
                            binding.btnSavePreferences.setBackgroundColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.myPurple
                                )
                            )

                            isLocationSelectedFromMap = false
                            savePreferencesAndGoToHomeFragment()
                        }
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        requireContext().getString(R.string.choosePreferences),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else{
                Toast.makeText(
                    requireContext(),
                    requireContext().getString(R.string.internetDisconnected),
                    Toast.LENGTH_SHORT
                ).show()
            }


        }

    }

    private fun savePreferencesAndGoToHomeFragment() {

        initialPreferencesViewModel.putBooleanInSharedPreferences("preferences_set", true)
        initialPreferencesViewModel.putStringInSharedPreferences("temperature_unit", "celsius")
        initialPreferencesViewModel.putStringInSharedPreferences("wind_speed_unit", "mps")
        initialPreferencesViewModel.putStringInSharedPreferences("language", selectedLanguage)
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
            .setPopUpTo(R.id.nav_graph, true)
            .build()
        findNavController().navigate(action, navOptions)

        changeLanguageAndLayout(selectedLanguage.substring(0, 2))


    }

    private fun activateImgCurrentLocation() {
        binding.imgCurrentLocation.setOnClickListener {

            if (NetworkManager.isInternetConnected()) {
                if (mapManager.isLocationEnabled()) {
                    mapManager.getLastLocation()
                } else {
                    Toast.makeText(requireContext(), requireContext().getString(R.string.turnOnLocation), Toast.LENGTH_SHORT).show()
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    requireContext().getText(R.string.internetDisconnected),
                    Toast.LENGTH_SHORT
                ).show()
            }


        }

    }

    private fun activateSearchIconListener() {
        binding.imgSearchIcon.setOnClickListener {

            if (NetworkManager.isInternetConnected()) {
                setUpMapUsingLocationString(binding.etSearchMap.text.toString())
                binding.rvSearchSuggestions.adapter =
                    SearchSuggestionAdapter(
                        mutableListOf(),
                        object : InterfaceInitialPreferences {})
            } else {
                Toast.makeText(
                    requireContext(),
                    requireContext().getText(R.string.internetDisconnected),
                    Toast.LENGTH_SHORT
                ).show()
            }


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
        if (addresses != null && addresses.isNotEmpty()) {
            val address = addresses[0].getAddressLine(0)
            binding.etSearchMap.text = Editable.Factory.getInstance().newEditable(address)
            binding.rvSearchSuggestions.visibility = View.GONE
        }
    }

    private fun startLottieAnimation(animationView: LottieAnimationView, animationName: String) {
        animationView.setAnimation(animationName)
        animationView.repeatCount = LottieDrawable.INFINITE
        animationView.repeatMode = LottieDrawable.RESTART
        animationView.playAnimation()
    }

    override fun mapManagerRequestPermission() {
        requestPermission()
    }

    override fun mapManagerDealWithLocationResult(locationResult: LocationResult) {
        dealWithLocationResult(locationResult)
    }

    private fun changeLanguageAndLayout(language: String) {

        val locale = Locale(language)
        Locale.setDefault(locale)
        val resources = context?.resources
        val configuration = Configuration()
        configuration.setLocale(locale)
        resources?.updateConfiguration(configuration, resources.displayMetrics)

        ViewCompat.setLayoutDirection(
            requireActivity().window.decorView,
            if (language == "ar") ViewCompat.LAYOUT_DIRECTION_RTL else ViewCompat.LAYOUT_DIRECTION_LTR
        )

        activity?.recreate()

    }

    override fun onPause() {
        super.onPause()
        binding.etSearchMap.text = Editable.Factory.getInstance().newEditable("")
    }

}