package com.example.weathermate.presentation.home


import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.example.weathermate.MyApp
import com.example.weathermate.R
import com.example.weathermate.data.model.Daily
import com.example.weathermate.data.model.Hourly
import com.example.weathermate.data.model.WeatherData
import com.example.weathermate.data.remote.RetrofitStateWeather
import com.example.weathermate.databinding.FragmentHomeBinding
import com.example.weathermate.util.MyHelper
import com.example.weathermate.util.MyConverters
import com.example.weathermate.util.NetworkManager
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
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

        checkInternet()


        binding.swipeRefreshLayout.setOnRefreshListener {
            findNavController().navigate(R.id.navigation_home)
        }



    }

    private fun checkInternet() {
        if(NetworkManager.isInternetConnected()){
            homeViewModel.initPreferencesManager(requireContext())
            homeViewModel.setLatitudeAndLongitude()
            getWeatherData()
            animateImages()
        } else{
            lifecycleScope.launch(Dispatchers.Main) {
                val savedWeatherData = homeViewModel.getWeatherDataFromDatabase()
                if (savedWeatherData != null){
                    homeViewModel.initPreferencesManager(requireContext())
                    homeViewModel.setLatitudeAndLongitude()
                    updateUi(savedWeatherData)
                    animateImages()
                }
            }

        }
    }

    private fun getWeatherData() {
        lifecycleScope.launch {
            homeViewModel.getWeatherDataOnline()
            homeViewModel.retrofitStateWeather.collectLatest {
                when (it) {
                    is RetrofitStateWeather.Loading -> {
                        binding.imgLoading.visibility = View.VISIBLE
                        startLottieAnimation(binding.imgLoading, "loading.json")
                        binding.scrollViewIDHome.visibility = View.GONE
                    }
                    is RetrofitStateWeather.OnSuccess -> {
                        updateUi(it.weatherData)
                        binding.scrollViewIDHome.visibility = View.VISIBLE
                        binding.imgLoading.visibility = View.GONE
                        binding.imgLoading.pauseAnimation()

                        homeViewModel.insertOrUpdateWeatherData(it.weatherData)

                    }
                    is RetrofitStateWeather.OnFail -> {
                        Log.i(TAG, it.errorMessage.toString())
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateUi(weatherData: WeatherData) {
        homeViewModel.cityName.observe(viewLifecycleOwner) {
            updateCityName(it)
        }
        updateTemperature(weatherData)
        binding.tvHumidity.text =
            "${MyConverters.convertHumidtyOrPressureOrTemperature(weatherData.current.humidity)} %"
        updateWindSpeed(weatherData)
        updateDateTime(weatherData)
        binding.tvPressure.text =
            "${MyConverters.convertHumidtyOrPressureOrTemperature(weatherData.current.pressure)} ${
                getString(R.string.hPa)
            }"
        binding.tvCloudiness.text =
            "${MyConverters.convertHumidtyOrPressureOrTemperature(weatherData.current.clouds)} %"
        homeViewModel.updateCityName(isAdded)
        binding.textViewWeatherDescription.text =
            weatherData.current.weather[0].description.capitalize()

        startLottieAnimation(
            binding.imageViewWeatherIcon,
            MyHelper.getWeatherLottie(weatherData.current.weather[0].icon)
        )

        setupHourlyAdapter(weatherData.hourly.take(24))
        setupDailyAdapter(weatherData.daily.take(7))

    }

    @SuppressLint("SetTextI18n")
    private fun updateTemperature(weatherData: WeatherData) {
        var tempInKelvinByDefault = weatherData.current.temp
        binding.textViewCurrentTemp.text =
            MyConverters.convertTemperature(tempInKelvinByDefault, requireContext())
    }

    private fun updateWindSpeed(weatherData: WeatherData) {
        var windSpeedInMPSByDefault = weatherData.current.wind_speed
        binding.tvWind.text = MyConverters.convertWind(windSpeedInMPSByDefault, requireContext())
    }

    private fun updateDateTime(weatherData: WeatherData) {
        val date = Date(weatherData.current.dt * 1000L)
        val formatDate = SimpleDateFormat("MMM dd, yyyy - EEE", Locale.getDefault())
        formatDate.timeZone = TimeZone.getTimeZone("GMT+2")
        binding.textViewDate.text = formatDate.format(date)

        val formatTime = SimpleDateFormat("hh:mm a", Locale.getDefault())
        formatTime.timeZone = TimeZone.getTimeZone("GMT+2")
        binding.textViewTime.text = formatTime.format(date)
    }

    private fun updateCityName(cityName: String) {
        binding.textViewCity.text = cityName
    }

    private fun setupHourlyAdapter(hourlyList: List<Hourly>) {
        val mlayoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)

        var highestTemp = -1000.0
        var lowestTemp = 1000.0
        hourlyList.forEach {
            if (it.temp > highestTemp)
                highestTemp = it.temp
            if (it.temp < lowestTemp)
                lowestTemp = it.temp
        }
        hourlyRecyclerAdapter = HourlyAdapter(
            homeViewModel.getStringFromSharedPreferences("temperature_unit", ""),
            highestTemp,
            lowestTemp
        )

        binding.recyclerViewHourlyForecast.apply {
            layoutManager = mlayoutManager
            adapter = hourlyRecyclerAdapter
        }



        hourlyRecyclerAdapter.submitList(hourlyList)

    }

    private fun setupDailyAdapter(dailyList: List<Daily>) {
        val mlayoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        var highestTemp = -1000.0
        var highestLowestTemp = -1000.0
        var lowestTempMin = 1000.0
        var lowestTempMax = 1000.0
        dailyList.forEach {
            if (it.temp.max > highestTemp)
                highestTemp = it.temp.max
            if (it.temp.min > highestLowestTemp)
                highestLowestTemp = it.temp.min
            if (it.temp.min < lowestTempMin)
                lowestTempMin = it.temp.min
            if (it.temp.max < lowestTempMax)
                lowestTempMax = it.temp.max
        }

        dailyRecyclerAdapter = DailyAdapter(
            homeViewModel.getStringFromSharedPreferences("temperature_unit", ""),
            highestLowestTemp,
            highestTemp,
            lowestTempMin,
            lowestTempMax
        )

        binding.recyclerViewDailyForecast.apply {
            layoutManager = mlayoutManager
            adapter = dailyRecyclerAdapter
        }

        dailyRecyclerAdapter.submitList(dailyList)
    }

    private fun startLottieAnimation(animationView: LottieAnimationView, animationName: String) {
        animationView.setAnimation(animationName)
        animationView.repeatCount = LottieDrawable.INFINITE
        animationView.repeatMode = LottieDrawable.RESTART
        animationView.playAnimation()
    }

    private fun animateImages() {
        animateWeatherImage(binding.imgCloudiness)
        animateWeatherImage(binding.imgPressure)
        animateWeatherImage(binding.imgHumidity)
        animateWeatherImage(binding.imgWind)
    }

    private fun animateWeatherImage(imageViewWeatherIcon: ImageView) {

        var displacement = 5f
        if (imageViewWeatherIcon == binding.imageViewWeatherIcon){
            displacement = 20f
        }

        val randomNumber1 = (0 until 360).random()
        val randomNumber2 = 360 - randomNumber1
        val circularAnimator =
            ValueAnimator.ofFloat(randomNumber1.toFloat(), randomNumber2.toFloat()).apply {
                duration = 3000
                repeatCount = ValueAnimator.INFINITE
                repeatMode = ValueAnimator.REVERSE
                interpolator = LinearInterpolator()
            }

        circularAnimator.addUpdateListener {
            val angle = it.animatedValue as Float
            val radius = displacement
            val x = radius * Math.cos(Math.toRadians(angle.toDouble())).toFloat()
            val y = radius * Math.sin(Math.toRadians(angle.toDouble())).toFloat()
            imageViewWeatherIcon.translationX = x
            imageViewWeatherIcon.translationY = y
        }
        circularAnimator.start()
    }

}



