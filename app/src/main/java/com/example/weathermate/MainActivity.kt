package com.example.weathermate

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.weathermate.databinding.ActivityMainBinding
import com.example.weathermate.util.NetworkManager
import java.util.*


class MainActivity : AppCompatActivity() , NetworkManager.NetworkListener {

    private lateinit var navController: NavController

    private lateinit var binding: ActivityMainBinding

    private lateinit var mainViewModel: MainViewModel

    private val TAG = "commonnn"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModelFactory = MainViewModelFactory(MyApp.getInstanceRepository())
        mainViewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkIfLayoutShouldBeArabic()

        initMainActivity()

        mainViewModel.activateAlerts(this)

        NetworkManager.setListener(this)


    }

    private fun checkIfLayoutShouldBeArabic() {
        if (mainViewModel.isArabic() && !mainViewModel.isLayoutChangedBySettings()){
            val locale = Locale("ar")
            Locale.setDefault(locale)
            val configuration = Configuration()
            configuration.setLocale(locale)
            resources.updateConfiguration(configuration, resources.displayMetrics)
            fixTitlesOfBottomNavBar()
        }
        mainViewModel.putBooleanInSharedPreferences("isLayoutChangedBySettings", false)
    }

    private fun fixTitlesOfBottomNavBar() {
        binding.bottomNavView
    }

    private fun initMainActivity() {
        navController = findNavController(R.id.nav_host_fragment)
        setUpNavBar()
        setBottomBarVisibility()
    }

    private fun setUpNavBar() {
        binding.bottomNavView.onTabSelected = {
            when (it.id) {
                R.id.navigation_home -> {
                    while (navController.popBackStack()){}
                    navController.navigate(R.id.navigation_home)
                }
                R.id.navigation_favorites -> {
                    while (navController.popBackStack()){}
                    navController.navigate(R.id.navigation_favorites)
                }
                R.id.navigation_alerts -> {
                    while (navController.popBackStack()){}
                    navController.navigate(R.id.navigation_alerts)
                }
                R.id.navigation_settings -> {
                    while (navController.popBackStack()){}
                    navController.navigate(R.id.navigation_settings)
                }
                else -> {}
            }
        }
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

    override fun onNetworkStatusChanged(isConnected: Boolean) {
        runOnUiThread {
            if(isConnected){
                binding.tvNetworkIndicator.visibility = View.GONE
            } else{
                binding.tvNetworkIndicator.visibility = View.VISIBLE
            }
        }
    }
}
