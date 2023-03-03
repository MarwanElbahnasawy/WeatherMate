package com.example.weathermate.home.view


import android.content.SharedPreferences
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weathermate.R
import com.example.weathermate.databinding.FragmentHomeBinding
import com.example.weathermate.model.Daily
import com.example.weathermate.model.Hourly
import com.example.weathermate.model.WeatherData
import com.example.weathermate.network.ApiService
import com.example.weathermate.network.RetrofitHelper
import com.example.weathermate.util.Constants
import com.example.weathermate.util.UnitsConverter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private val TAG = "commonnn"

    private lateinit var binding: FragmentHomeBinding

    private var longitudeDouble: Double = 0.0
    private var latitudeDouble: Double = 0.0

    lateinit var sharedPreferences: SharedPreferences

    lateinit var hourlyRecyclerView: RecyclerView
    lateinit var hourlyRecyclerAdapter: HourlyAdapter

    lateinit var dailyRecyclerView: RecyclerView
    lateinit var dailyRecyclerAdapter: DailyAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initPreferencesManager()

        if (sharedPreferences.getBoolean("preferences_set", false)) {

            setLatitudeAndLongitude()


            initFrag(view)



            getWeatherData()
        } else{
            val action =
                HomeFragmentDirections.actionNavigationHomeToPreferencesFragment()
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.mobile_navigation, true)
                .build()
            findNavController().navigate(action, navOptions)
        }










    }

    private fun initPreferencesManager() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
    }

    private fun initFrag(view: View) {
        hourlyRecyclerView = view.findViewById(R.id.recyclerViewHourlyForecast)
        dailyRecyclerView = view.findViewById(R.id.recyclerViewDailyForecast)

        Log.i(TAG, "initFrag: " + sharedPreferences.getBoolean("preferences_set", false))

    }

    private fun getWeatherData() {
        val retrofit = RetrofitHelper.getRetrofitInstance(ApiService.BASE_URL_WEATHER)
        val service = retrofit.create(ApiService::class.java)
        lifecycleScope.launch(Dispatchers.IO) {
            var weatherData: WeatherData
            if (sharedPreferences.getString("language", null).equals("english")) {
                Log.i(TAG, "getWeatherData: ++++++++++++english" )
                Log.i(TAG, "getWeatherData: " + latitudeDouble.toString())
                Log.i(TAG, "getWeatherData: " + longitudeDouble.toString())
                weatherData = service.getWeatherData(
                    getString(R.string.openweathermap_key),
                    latitudeDouble,
                    longitudeDouble,
                    "en"
                )
                Log.i(TAG, "getWeatherData: after english call")
            } else {
                Log.i(TAG, "getWeatherData: ++++++++++++arabbic" )
                weatherData = service.getWeatherData(
                    getString(R.string.openweathermap_key),
                    latitudeDouble,
                    longitudeDouble,
                    "ar"
                )
            }

            withContext(Dispatchers.Main) {

                var tempInKelvinByDefault = weatherData.current.temp
                if (sharedPreferences.getString("temperature_unit", null).equals("celsius")){
                    tempInKelvinByDefault = UnitsConverter.kelvinToCelsius(tempInKelvinByDefault)
                    binding.textViewCurrentTemp.text = tempInKelvinByDefault.toInt().toString() + " C"
                }

                else if (sharedPreferences.getString("temperature_unit", null).equals("fahrenheit")){
                    tempInKelvinByDefault = UnitsConverter.kelvinToFahrenheit(tempInKelvinByDefault)
                    binding.textViewCurrentTemp.text = tempInKelvinByDefault.toInt().toString() + " F"
                } else{
                    binding.textViewCurrentTemp.text = tempInKelvinByDefault.toInt().toString() + " K"
                }


                val date = Date(weatherData.current.dt * 1000L)  // converts seconds to milliseconds
                val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                format.timeZone = TimeZone.getTimeZone("GMT+2")
                binding.textViewCurrentDateTime.text = format.format(date)

                binding.textViewHumidity.text = weatherData.current.humidity.toString() + " %"

                var windSpeedInMPHByDefault = weatherData.current.wind_speed
                if (sharedPreferences.getString("wind_speed_unit", null).equals("kph")){
                    windSpeedInMPHByDefault =
                        UnitsConverter.meterPerSecondToKilometerPerHour(windSpeedInMPHByDefault)
                    binding.textViewWindSpeed.text = windSpeedInMPHByDefault.toString() + " KPH"
                } else{
                    binding.textViewWindSpeed.text = windSpeedInMPHByDefault.toString() + " MPH"
                }




                binding.textViewPressure.text = weatherData.current.pressure.toString() + " hPa"

                binding.textViewClouds.text = weatherData.current.clouds.toString() + " %"

                if (isAdded) {
                    val geocoder = Geocoder(requireContext(), Locale.getDefault())
                    val addresses = geocoder.getFromLocation(latitudeDouble, longitudeDouble, 1)
                    if (addresses != null) {
                        if (addresses.isNotEmpty()) {
                            val address = addresses[0]
                            val addressName =
                                address.locality ?: address.subAdminArea ?: address.adminArea
                            binding.textViewCity.text = addressName
                        }
                    }
                }


                binding.textViewWeatherDescription.text = weatherData.current.weather[0].description

                Glide.with(requireContext())
                    .load(Constants.weatherConditionImageBaseURL + weatherData.current.weather[0].icon + "@2x.png")
                    .into(binding.imageViewWeatherIcon)




                var hourlyList = weatherData.hourly.take(24)


                setupHourlyAdapter(hourlyList)

                var dailyList = weatherData.daily

                setupDailyAdapter(dailyList)


            }


        }
    }


    private fun setupHourlyAdapter(hourlyList: List<Hourly>) {
        val mlayoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)

        hourlyRecyclerAdapter = HourlyAdapter()

        hourlyRecyclerView.apply {
            layoutManager = mlayoutManager
            adapter = hourlyRecyclerAdapter
        }

        hourlyRecyclerAdapter.submitList(hourlyList)


    }

    private fun setupDailyAdapter(dailyList: List<Daily>) {
        val mlayoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        dailyRecyclerView.layoutManager = mlayoutManager
        dailyRecyclerAdapter = DailyAdapter(dailyList)

        dailyRecyclerView.adapter = dailyRecyclerAdapter
    }

    private fun setLatitudeAndLongitude() {
        latitudeDouble = sharedPreferences.getString("latitude" , null)?.toDouble() ?: 0.0
        longitudeDouble = sharedPreferences.getString("longitude" , null)?.toDouble() ?: 0.0
    }


}