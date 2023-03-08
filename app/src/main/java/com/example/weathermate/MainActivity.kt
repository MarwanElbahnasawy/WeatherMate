package com.example.weathermate

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.lifecycle.ViewModelProvider
import com.example.weathermate.databinding.ActivityMainBinding
import com.example.weathermate.util.HelperObject
import com.example.weathermate.util.HelperSingleton


class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    private lateinit var binding: ActivityMainBinding

    private lateinit var mainViewModel: MainViewModel

    private val TAG = "commonnn"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initMainActivity()

        val viewModelFactory = MainViewModelFactory(MyApp.getInstanceRepository())
        mainViewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]

        mainViewModel.activateAlerts(this)
    }


    private fun initMainActivity() {
        navController = findNavController(R.id.nav_host_fragment)
        binding.bottomNavView.setupWithNavController(navController)
        setBottomBarVisibility()
    }


    private fun setBottomBarVisibility() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_home, R.id.navigation_favorites, R.id.navigation_alerts, R.id.navigation_settings -> {
                    binding.bottomNavView.visibility = View.VISIBLE
                }
                else -> {
                    binding.bottomNavView.visibility = View.GONE
                }
            }
        }
    }


}
