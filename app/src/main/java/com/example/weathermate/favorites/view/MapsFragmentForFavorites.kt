package com.example.weathermate.favorites.view

import android.annotation.SuppressLint
import android.graphics.Color
import android.location.Geocoder
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weathermate.R
import com.example.weathermate.databinding.FragmentMapsForFavoritesBinding
import com.example.weathermate.initial_preferences.OnItemClickPersonal
import com.example.weathermate.initial_preferences.SearchSuggestionAdapter
import com.example.weathermate.network.ApiService
import com.example.weathermate.network.RetrofitHelper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class MapsFragmentForFavorites : Fragment() {

    private val TAG = "MapsFragmentForFavorite"

    lateinit var binding : FragmentMapsForFavoritesBinding
    lateinit var mfusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var myMap: GoogleMap
    private var longitudeDouble: Double = 0.0
    private var latitudeDouble: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapsForFavoritesBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        val layoutManager = LinearLayoutManager(context)
        binding.rvSearchSuggestions.layoutManager = layoutManager


        //will remove from here later
        mfusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        initializeMapFragment()

        addTextChangedListenerToTheSearchEditText()
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

    private fun setUpMapOnClick(latLng: LatLng) {
        latitudeDouble = latLng.latitude
        longitudeDouble = latLng.longitude
        val markerOptions = MarkerOptions().position(latLng)
        myMap.clear()
        myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f) , 1500 , null)
        myMap.addMarker(markerOptions)


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
                                    binding.etSearchMap.text = Editable.Factory.getInstance().newEditable(suggestionSelected)
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
                        myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f) , 1500 , null)
                        myMap.addMarker(MarkerOptions().position(latLng))
                    }
                } else {
                }
            }
        }

    }

}