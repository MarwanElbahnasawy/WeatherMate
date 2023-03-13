package com.example.weathermate

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
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

        changeBottomBarTabSelection()

        checkIfLayoutShouldBeArabic()

        initMainActivity()

        mainViewModel.activateAlerts(this)

        NetworkManager.setListener(this)



    }

    private fun changeBottomBarTabSelection() {
        if (mainViewModel.isLayoutChangedBySettings()){
            binding.bottomNavView.selectTabAt(3)
        }
    }

    private fun checkIfLayoutShouldBeArabic() {
        if (mainViewModel.isArabic() && !mainViewModel.isLayoutChangedBySettings()){
            val locale = Locale("ar")
            Locale.setDefault(locale)
            val configuration = Configuration()
            configuration.setLocale(locale)
            resources.updateConfiguration(configuration, resources.displayMetrics)
            setTitlesOfNavBarToArabic()
        } 
        
        setUpNavBar()
        mainViewModel.putBooleanInSharedPreferences("isLayoutChangedBySettings", false)
    }
    private fun initMainActivity() {
        navController = findNavController(R.id.nav_host_fragment)
        setBottomBarVisibility()
    }

    private fun setUpNavBar() {
        binding.bottomNavView.onTabSelected = {
            when (it) {
                binding.bottomNavView.tabs[0] -> {
                    while (navController.popBackStack()){}
                    navController.navigate(R.id.navigation_home)
                }
                binding.bottomNavView.tabs[1] -> {
                    while (navController.popBackStack()){}
                    navController.navigate(R.id.navigation_favorites)
                }
                binding.bottomNavView.tabs[2] -> {
                    while (navController.popBackStack()){}
                    navController.navigate(R.id.navigation_alerts)
                }
                binding.bottomNavView.tabs[3] -> {
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

    private fun setTitlesOfNavBarToArabic() {
        for (i in 0..3) {
            binding.bottomNavView.removeTab(binding.bottomNavView.tabs[0])
        }

        val bottomBarTab1 = binding.bottomNavView.createTab(R.drawable.ic_home_black_24dp, getString(R.string.title_home))
        val bottomBarTab2 = binding.bottomNavView.createTab(R.drawable.ic_baseline_bookmark_24, getString(R.string.title_favorites))
        val bottomBarTab3 = binding.bottomNavView.createTab(R.drawable.ic_notifications_black_24dp, getString(R.string.title_alerts))
        val bottomBarTab4 = binding.bottomNavView.createTab(R.drawable.ic_baseline_settings_24, getString(R.string.title_settings))

        binding.bottomNavView.addTab(bottomBarTab1)
        binding.bottomNavView.addTab(bottomBarTab2)
        binding.bottomNavView.addTab(bottomBarTab3)
        binding.bottomNavView.addTab(bottomBarTab4)

        binding.bottomNavView.selectTabAt(0)

        setUpNavBar()
    }

}
