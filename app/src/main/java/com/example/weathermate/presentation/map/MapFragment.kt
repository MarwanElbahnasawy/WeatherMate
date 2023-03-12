package com.example.weathermate.presentation.map

import android.Manifest
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
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
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.example.weathermate.MyApp
import com.example.weathermate.R
import com.example.weathermate.data.model.FavoriteAddress
import com.example.weathermate.data.remote.RetrofitStateWeather
import com.example.weathermate.databinding.FragmentMapBinding
import com.example.weathermate.presentation.initial_preferences.InterfaceInitialPreferences
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class MapFragment : Fragment(), MapManagerInterface {

    private val TAG = "commonnn"

    private val PERMISSION_LOCATION_ID_MAP_FRAGMENT = 222

    lateinit var binding: FragmentMapBinding

    private lateinit var myMap: GoogleMap

    private var longitudeDouble: Double = 0.0
    private var latitudeDouble: Double = 0.0
    private var myAddress : String = ""

    lateinit var geocoder: Geocoder

    private var isLocationChosenOnMap = false

    private var isClearing = false

    private val args: MapFragmentArgs by navArgs()

    private lateinit var mapViewModel: MapViewModel
    private lateinit var mapManager: MapManager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapViewModelFactory =
            MapViewModelFactory(MyApp.getInstanceRepository())
        mapViewModel = ViewModelProvider(
            this,
            mapViewModelFactory
        )[MapViewModel::class.java]

        mapManager = MapManager(requireContext(), this)

        initFrag()

        //mapViewModel.checkPermissionsAndIfLocationIsEnabled()
        mapManager.checkPermissionsAndIfLocationIsEnabled()

        val layoutManager = LinearLayoutManager(context)
        binding.rvSearchSuggestions.layoutManager = layoutManager


        initializeMapFragment()

        activateAddButtonListener()

        activateImgCurrentLocation()

        activateSearchIconListener()

        activateClearSearchIconListener()

        addTextChangedListenerToTheSearchEditText()

        insertToFavorites()

    }
    private fun initFrag() {

        geocoder = Geocoder(requireContext(), Locale.getDefault())

        latitudeDouble = mapViewModel.getStringFromSharedPreferences("latitude", "")?.toDouble() ?: 0.0
        longitudeDouble = mapViewModel.getStringFromSharedPreferences("longitude", "")?.toDouble() ?: 0.0
        if (latitudeDouble != 0.0){
            setMyAddressFromLatAndLon(latitudeDouble,longitudeDouble)
        }


        if(args.isFromAlerts || args.isFromSettings){
            binding.btnSelectOrAddOnMap.text = "Select"
        }

        startLottieAnimation(binding.imgSearchIcon , "search.json")
        startLottieAnimation(binding.imgClearSearch , "delete.json")

        val animator = ObjectAnimator.ofFloat(binding.imgCurrentLocation, "rotation", 0f, 360f)
        animator.repeatCount = ValueAnimator.INFINITE
        animator.duration = 2000
        animator.start()
    }

    private fun initializeMapFragment() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync {
            myMap = it
            myMap.setOnMapClickListener { latLng ->
                    setUpMapOnClick(latLng)
            }
        }

        if (mapManager.isLocationEnabled()){
            mapManager.getLastLocation()
        }

    }

    fun setUpMapOnClick(latLng: LatLng) {
        isLocationChosenOnMap = true
        moveMap(latLng)
        convertLatLonToAddressAndSetSearchText(latLng)
    }

    private fun requestPermission() {

        requestPermissions(
            arrayOf<String>(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_LOCATION_ID_MAP_FRAGMENT
        )
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_LOCATION_ID_MAP_FRAGMENT) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }
        }
    }

    private fun dealWithLocationResult(locationResult: LocationResult) {
        binding.btnSelectOrAddOnMap.isEnabled = true
        isLocationChosenOnMap = true
        val mLastLocation: Location = locationResult.lastLocation
        longitudeDouble = mLastLocation.longitude
        latitudeDouble = mLastLocation.latitude

        setMyAddressFromLatAndLon(latitudeDouble, longitudeDouble)


        val latLng = LatLng(latitudeDouble, longitudeDouble)
        convertLatLonToAddressAndSetSearchText(latLng)
        moveMap(latLng)

    }

    private fun moveMap(latLng: LatLng) {

        latitudeDouble = latLng.latitude
        longitudeDouble = latLng.longitude

        val markerOptions = MarkerOptions().position(latLng)
        myMap.clear()
        myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f), 1500, null)
        myMap.addMarker(markerOptions)

     /*   if(args.isFromSettings){
            mapViewModel.putStringInSharedPreferences("latitude", latitudeDouble.toString())
            mapViewModel.putStringInSharedPreferences("longitude", longitudeDouble.toString())
        }*/

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


    private fun addTextChangedListenerToTheSearchEditText() {
        binding.etSearchMap.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                if(!isClearing) {
                    binding.rvSearchSuggestions.visibility = View.VISIBLE
                    binding.rvSearchSuggestions.setBackgroundColor(Color.WHITE)
                    binding.rvSearchSuggestions.alpha = 0.8f
                }
                isClearing = false
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                lifecycleScope.launch(Dispatchers.IO) {
                    val mapsAutoCompleteResponse = mapViewModel.getMapsAutoCompleteResponse(s)
                    val predictions = mapsAutoCompleteResponse.predictions
                    val suggestions = mutableListOf<String>()
                    for (i in 0 until predictions.size) {
                        suggestions.add(predictions.get(i).description)
                    }
                    withContext(Dispatchers.Main) {
                        binding.rvSearchSuggestions.adapter =
                            SearchSuggestionAdapter(suggestions, object :
                                InterfaceInitialPreferences {
                                override fun onItemClickInitialPreferences(suggestionSelected: String) {
                                    binding.etSearchMap.text = Editable.Factory.getInstance()
                                        .newEditable(suggestionSelected)

                                    binding.rvSearchSuggestions.visibility = View.GONE
                                    setUpMapUsingLocationString(suggestionSelected)

                                }
                            })
                    }
                }

            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }


    @SuppressLint("MissingPermission")
    private fun setUpMapUsingLocationString(locationName: String) {
        isLocationChosenOnMap = true
        if (isAdded) {
            val addresses = geocoder.getFromLocationName(locationName, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                if (addresses.isNotEmpty()) {
                    latitudeDouble = addresses[0].latitude
                    longitudeDouble = addresses[0].longitude

                    setMyAddressFromLatAndLon(latitudeDouble, longitudeDouble)
                    val latLng = LatLng(latitudeDouble, longitudeDouble)
                    moveMap(latLng)

                }
            }
        }
/*        if(args.isFromSettings){
            mapViewModel.putStringInSharedPreferences("latitude", latitudeDouble.toString())
            mapViewModel.putStringInSharedPreferences("longitude", longitudeDouble.toString())
        }*/


    }

    private fun setMyAddressFromLatAndLon(latitude: Double, longitude: Double){
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        if (addresses != null && addresses.isNotEmpty()) {
            val address = addresses[0]
            val addressName =
                address.locality ?: address.subAdminArea ?: address.adminArea
            myAddress = addressName
        }
    }

    private fun activateImgCurrentLocation() {
        binding.imgCurrentLocation.setOnClickListener {
            if(mapManager.isLocationEnabled()){
                mapManager.getLastLocation()
            } else{
                Toast.makeText(requireContext(), "Turn on location", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        }

    }

    private fun activateSearchIconListener() {
        binding.imgSearchIcon.setOnClickListener {
            if(binding.etSearchMap.text.isNotBlank()){
                setUpMapUsingLocationString(binding.etSearchMap.text.toString())
                binding.rvSearchSuggestions.adapter = SearchSuggestionAdapter(mutableListOf() , object :
                    InterfaceInitialPreferences {})
                isLocationChosenOnMap = true

            }
         }

    }


    private fun activateClearSearchIconListener() {
        binding.imgClearSearch.setOnClickListener {
            isClearing = true
            binding.etSearchMap.text = Editable.Factory.getInstance().newEditable("")
        }
    }

    private fun activateAddButtonListener() {
        
        if(args.isFromFavorites){
            binding.btnSelectOrAddOnMap.visibility = View.VISIBLE
            binding.btnSelectOrAddOnMap.setOnClickListener {
                if (isLocationChosenOnMap){
                    isLocationChosenOnMap = false
                    mapViewModel.insertFavoriteAddressInternetCall(
                        latitudeDouble,
                        longitudeDouble
                    )
                    Toast.makeText(requireContext(),"Added to favorites.", Toast.LENGTH_SHORT).show()
                } else{
                    Toast.makeText(requireContext(),"Location is already in favorites.", Toast.LENGTH_SHORT).show()
                }
            }

        } else if (args.isFromAlerts){
            binding.btnSelectOrAddOnMap.visibility = View.VISIBLE
            binding.btnSelectOrAddOnMap.setOnClickListener {
                if (isLocationChosenOnMap) {
                    isLocationChosenOnMap = false
                    mapViewModel.putStringInSharedPreferences("ALERT_LATITUDE_FROM_MAP", latitudeDouble.toString())
                    mapViewModel.putStringInSharedPreferences("ALERT_LONGITUDE_FROM_MAP", longitudeDouble.toString())
                    mapViewModel.putStringInSharedPreferences("ALERT_ADDRESS" , myAddress)
                    Toast.makeText(requireContext(),"Location selected.", Toast.LENGTH_SHORT).show()
                } else{
                    Toast.makeText(requireContext(),"Location is already selected.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        else if (args.isFromSettings){

            binding.btnSelectOrAddOnMap.setOnClickListener {
                mapViewModel.putStringInSharedPreferences("latitude", latitudeDouble.toString())
                mapViewModel.putStringInSharedPreferences("longitude", longitudeDouble.toString())
                Toast.makeText(requireContext(),"Location selected.", Toast.LENGTH_SHORT).show()
            }
          //  binding.btnAddToFavorites.visibility = View.GONE

        }
    }

    private fun insertToFavorites() {
        lifecycleScope.launch {
            mapViewModel.retrofitStateWeather.collectLatest {
                when (it){
                    is RetrofitStateWeather.Loading -> {

                    }
                    is RetrofitStateWeather.OnFail -> {
                        Log.i(TAG, "insertToFavorites: ${it.errorMessage}")
                    }
                    is RetrofitStateWeather.OnSuccess -> {
                        mapViewModel.insertToFavorites(FavoriteAddress(
                            address = myAddress,
                            latitude = latitudeDouble,
                            longitude = longitudeDouble,
                            latlngString = (latitudeDouble.toString()+longitudeDouble.toString()),
                            currentTemp = it.weatherData.current.temp,
                            currentDescription = it.weatherData.current.weather[0].description,
                            lastCheckedTime = System.currentTimeMillis(),
                            icon = it.weatherData.current.weather[0].icon
                        ))
                    }
                }
            }
        }

    }

    private fun startLottieAnimation(animationView: LottieAnimationView, animationName: String) {
        animationView.setAnimation(animationName)
        animationView.repeatCount = LottieDrawable.INFINITE
        animationView.repeatMode = LottieDrawable.RESTART
        animationView.playAnimation()
    }

    override fun onPause() {
        super.onPause()
        binding.etSearchMap.text = Editable.Factory.getInstance().newEditable("")
    }

    override fun onDestroy() {
        super.onDestroy()
        arguments?.putBoolean("isFromFavorites" , false)
        arguments?.putBoolean("isFromSettings" , false)
        arguments?.putBoolean("isFromAlerts" , false)
    }

    override fun mapManagerRequestPermission() {
        requestPermission()
    }

    override fun mapManagerDealWithLocationResult(locationResult: LocationResult) {
        dealWithLocationResult(locationResult)
    }

}