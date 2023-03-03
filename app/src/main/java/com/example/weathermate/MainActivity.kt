package com.example.weathermate

import android.os.Bundle
import android.text.Layout.Directions
import android.util.Log
import androidx.preference.PreferenceManager
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.weathermate.home.view.HomeFragmentDirections
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    lateinit var bottom_nav_view: BottomNavigationView

    private val TAG = "commonnn"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initUI()


    }



    private fun initUI() {
        navController = findNavController(R.id.nav_host_fragment)
        bottom_nav_view = findViewById(R.id.bottom_nav_view)
        bottom_nav_view.setupWithNavController(navController)

        setBottomBarVisibility()
    }

    private fun setBottomBarVisibility() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_home, R.id.navigation_favorites, R.id.navigation_alerts , R.id.navigation_settings -> {
                    bottom_nav_view.visibility = View.VISIBLE
                }
                else -> {
                    bottom_nav_view.visibility = View.GONE
                }
            }
        }
    }




}

