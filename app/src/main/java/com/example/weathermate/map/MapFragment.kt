package com.example.weathermate.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weathermate.R
import com.example.weathermate.databinding.FragmentMapBinding
import com.example.weathermate.initial_preferences.OnItemClickPersonal
import com.example.weathermate.initial_preferences.PERMISSION_LOCATION_ID
import com.example.weathermate.initial_preferences.SearchSuggestionAdapter
import com.example.weathermate.network.ApiService
import com.example.weathermate.network.RetrofitHelper
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class MapFragment : Fragment() {

    private val TAG = "commonnn"

    lateinit var binding: FragmentMapBinding

    lateinit var mfusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var myMap: GoogleMap
    private var longitudeDouble: Double = 0.0
    private var latitudeDouble: Double = 0.0

    lateinit var sharedPreferences: SharedPreferences
    lateinit var sharedPreferencesEditor :  SharedPreferences.Editor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initFrag()

        val layoutManager = LinearLayoutManager(context)
        binding.rvSearchSuggestions.layoutManager = layoutManager


        //will remove from here later
        mfusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        initializeMapFragment()

        addTextChangedListenerToTheSearchEditText()

        activateImgCurrentLocation()

        activateSearchIconListener()

        activateClearSearchIconListener()

    }


    private fun initFrag() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        sharedPreferencesEditor = sharedPreferences.edit()

        latitudeDouble = sharedPreferences.getString("latitude", null)?.toDouble() ?: 0.0
        latitudeDouble = sharedPreferences.getString("longitude", null)?.toDouble() ?: 0.0
    }

    private fun initializeMapFragment() {
        Log.i(TAG, "initializeMapFragment: called")
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync {
            myMap = it
            myMap.setOnMapClickListener { latLng ->
                    setUpMapOnClick(latLng)

            }
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
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
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

            moveMapWithLatAndLon(latitudeDouble , longitudeDouble)


        }
    }



    private fun setUpMapOnClick(latLng: LatLng) {
        latitudeDouble = latLng.latitude
        longitudeDouble = latLng.longitude

        moveMapWithLatAndLon(latitudeDouble , longitudeDouble)

        val geocoder = Geocoder(requireContext())
        val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
        if (addresses != null) {
            val address = addresses[0].getAddressLine(0)
            binding.etSearchMap.text = Editable.Factory.getInstance().newEditable(address)
            binding.rvSearchSuggestions.visibility = View.GONE
        }



    }

    private fun addTextChangedListenerToTheSearchEditText() {
        binding.etSearchMap.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                binding.rvSearchSuggestions.visibility = View.VISIBLE
                binding.rvSearchSuggestions.setBackgroundColor(Color.WHITE)
                binding.rvSearchSuggestions.alpha = 0.8f
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val apiKey = resources.getString(R.string.google_maps_key)
                val retrofit =
                    RetrofitHelper.getRetrofitInstance(ApiService.BASE_URL_MAP_AUTOCOMPLETE)
                val service = retrofit.create(ApiService::class.java)
                lifecycleScope.launch(Dispatchers.IO) {
                    val mapsAutoCompleteResponse = service.getMapsAutoCompleteResponse(apiKey, s)
                    val predictions = mapsAutoCompleteResponse.predictions
                    val suggestions = mutableListOf<String>()
                    for (i in 0 until predictions.size) {
                        suggestions.add(predictions.get(i).description)
                    }
                    withContext(Dispatchers.Main) {
                        binding.rvSearchSuggestions.adapter =
                            SearchSuggestionAdapter(suggestions, object : OnItemClickPersonal {
                                override fun onItemClickPersonal(suggestionSelected: String) {
                                    Log.i(TAG, "onItemClickPersonal: $suggestionSelected")
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

        sharedPreferencesEditor.putString("latitude", latitudeDouble.toString())
        sharedPreferencesEditor.putString("longitude", longitudeDouble.toString())
        sharedPreferencesEditor.apply()

    }

    private fun moveMapWithLatAndLon(latitude: Double, longitude: Double){
        val latLng = LatLng(latitude, longitude)
        val markerOptions = MarkerOptions().position(latLng)
        myMap.clear()
        myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f), 1500, null)
        myMap.addMarker(markerOptions)

        sharedPreferencesEditor.putString("latitude", latitudeDouble.toString())
        sharedPreferencesEditor.putString("longitude", longitudeDouble.toString())
        sharedPreferencesEditor.apply()
    }

    private fun activateImgCurrentLocation() {
        binding.imgCurrentLocation.setOnClickListener {
            if(isLocationEnabled()){
                getLastLocation()
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
                binding.rvSearchSuggestions.adapter = SearchSuggestionAdapter(mutableListOf() , object : OnItemClickPersonal{})

            }
         }

    }


    private fun activateClearSearchIconListener() {
        binding.imgClearSearch.setOnClickListener {
            binding.etSearchMap.text = Editable.Factory.getInstance().newEditable("")
        }
    }

    override fun onPause() {
        super.onPause()
        binding.etSearchMap.text = Editable.Factory.getInstance().newEditable("")
    }

}