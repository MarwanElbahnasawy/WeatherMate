package com.example.weathermate.presentation.home


import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weathermate.MyApp
import com.example.weathermate.R
import com.example.weathermate.databinding.FragmentHomeBinding
import com.example.weathermate.data.model.Daily
import com.example.weathermate.data.model.Hourly
import com.example.weathermate.data.model.WeatherData
import com.example.weathermate.data.remote.RetrofitState
import com.example.weathermate.util.Constants
import com.example.weathermate.util.MyConverters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private val TAG = "commonnn"

    private lateinit var binding: FragmentHomeBinding

    lateinit var hourlyRecyclerAdapter: HourlyAdapter

    lateinit var dailyRecyclerAdapter: DailyAdapter

    private lateinit var homeViewModel: HomeViewModel

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

        val viewModelFactory = HomeViewModelFactory(MyApp.getInstanceRepository())
        homeViewModel = ViewModelProvider(this, viewModelFactory)[HomeViewModel::class.java]

        initFrag()
        homeViewModel.initPreferencesManager(requireContext())

        if (homeViewModel.isPreferencesSet()) {

            homeViewModel.setLatitudeAndLongitude()
            lifecycleScope.launch {
                homeViewModel.getWeatherData()
                homeViewModel.retrofitState.collectLatest {
                    when (it) {
                        is RetrofitState.onSuccess -> {
                            withContext(Dispatchers.Main){
                                updateUi(it.weatherData)
                            }
                        }
                        is RetrofitState.onFail -> {
                            Log.i(TAG, it.errorMessage.toString())
                        }
                        else -> {}
                    }
                }
            }

        } else {
            val action =
                HomeFragmentDirections.actionNavigationHomeToPreferencesFragment()
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.mobile_navigation, true)
                .build()
            findNavController().navigate(action, navOptions)
        }
    }

    private fun initFrag() {
       // HelperObject.createSharedPreferencesInstance(requireContext())
        homeViewModel.cityName.observe(viewLifecycleOwner){
            updateCityName(it)
        }
    }

    private fun updateUi(weatherData: WeatherData) {

        updateTemperature(weatherData)
        updateDateTime(weatherData)
        binding.textViewHumidity.text = "${weatherData.current.humidity} %"
        updateWindSpeed(weatherData)
        binding.textViewPressure.text = "${weatherData.current.pressure} hPa"
        binding.textViewClouds.text = "${weatherData.current.clouds} %"
        homeViewModel.updateCityName(isAdded)
        binding.textViewWeatherDescription.text = weatherData.current.weather[0].description
        Glide.with(requireContext())
            .load("${Constants.weatherConditionImageBaseURL}${weatherData.current.weather[0].icon}@2x.png")
            .into(binding.imageViewWeatherIcon)
        setupHourlyAdapter(weatherData.hourly.take(24))
        setupDailyAdapter(weatherData.daily)

    }

    @SuppressLint("SetTextI18n")
    private fun updateTemperature(weatherData: WeatherData) {
        var tempInKelvinByDefault = weatherData.current.temp
        val tempUnit = homeViewModel.getTemperatureUnit()

        when (tempUnit) {
            "celsius" -> {
                tempInKelvinByDefault = MyConverters.kelvinToCelsius(tempInKelvinByDefault)
                binding.textViewCurrentTemp.text = "${tempInKelvinByDefault.toInt()} °C"
            }
            "fahrenheit" -> {
                tempInKelvinByDefault = MyConverters.kelvinToFahrenheit(tempInKelvinByDefault)
                binding.textViewCurrentTemp.text = "${tempInKelvinByDefault.toInt()} °F"
            }
            else -> {
                binding.textViewCurrentTemp.text = "${tempInKelvinByDefault.toInt()} °K"
            }
        }
    }

    private fun updateDateTime(weatherData: WeatherData) {
        val date = Date(weatherData.current.dt * 1000L)
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        format.timeZone = TimeZone.getTimeZone("GMT+2")
        binding.textViewCurrentDateTime.text = format.format(date)
    }

    private fun updateWindSpeed(weatherData: WeatherData) {
        var windSpeedInMPHByDefault = weatherData.current.wind_speed
        val windSpeedUnit = homeViewModel.getWindSpeedUnit()

        val windSpeedText = when (windSpeedUnit) {
            "kph" -> "${MyConverters.meterPerSecondToKilometerPerHour(windSpeedInMPHByDefault)} kph"
            else -> "$windSpeedInMPHByDefault mph"
        }

        binding.textViewWindSpeed.text = windSpeedText
    }

    private fun updateCityName(cityName: String) {
        binding.textViewCity.text = cityName
    }

    private fun setupHourlyAdapter(hourlyList: List<Hourly>) {
        val mlayoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)

        hourlyRecyclerAdapter = HourlyAdapter(homeViewModel.getStringFromSharedPreferences("temperature_unit", ""))

        binding.recyclerViewHourlyForecast.apply {
            layoutManager = mlayoutManager
            adapter = hourlyRecyclerAdapter
        }

        hourlyRecyclerAdapter.submitList(hourlyList)

    }

    private fun setupDailyAdapter(dailyList: List<Daily>) {
        val mlayoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        dailyRecyclerAdapter = DailyAdapter(homeViewModel.getStringFromSharedPreferences("temperature_unit", ""))

        binding.recyclerViewDailyForecast.apply {
            layoutManager = mlayoutManager
            adapter = dailyRecyclerAdapter
        }

        dailyRecyclerAdapter.submitList(dailyList)
    }


}

