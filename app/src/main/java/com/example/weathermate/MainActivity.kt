package com.example.weathermate

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.weathermate.alerts.view.AlertsManager
import com.example.weathermate.model.AlertItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var bottomNavView: BottomNavigationView

    private val TAG = "commonnn"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initUI()

        checkNotificationAndAlertPermissions()

        activateAlerts()


    }


    private fun initUI() {
        navController = findNavController(R.id.nav_host_fragment)
        bottomNavView = findViewById(R.id.bottom_nav_view)
        bottomNavView.setupWithNavController(navController)

        setBottomBarVisibility()
    }

    private fun checkNotificationAndAlertPermissions() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            val builder = androidx.appcompat.app.AlertDialog.Builder(this)

            builder.setMessage("This app needs permission to show notifications. Please enable it in the app's settings.")
                .setTitle("Permission required")

            builder.setPositiveButton("OK") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri: Uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }

            val dialog = builder.create()
            dialog.show()
        }
        if (!Settings.canDrawOverlays(this)) {

            val builder = AlertDialog.Builder(this)

            builder.setMessage("This app needs permission to display an alarm over other apps. Please enable it in the app's settings.")
                .setTitle("Permission required")

            builder.setPositiveButton("OK") { _, _ ->
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                intent.data = Uri.parse("package: $packageName")
                startActivityForResult(intent, 0)
            }

            val dialog = builder.create()
            dialog.show()
        }
    }

    private fun setBottomBarVisibility() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_home, R.id.navigation_favorites, R.id.navigation_alerts, R.id.navigation_settings -> {
                    bottomNavView.visibility = View.VISIBLE
                }
                else -> {
                    bottomNavView.visibility = View.GONE
                }
            }
        }
    }

    private fun activateAlerts() {
        val alertsManager = AlertsManager(this)
        var alerts: List<AlertItem>
        lifecycleScope.launch {
            MyApp.getInstanceRepository().getAllAlerts().observe(this@MainActivity) { it ->
                it.forEach {
                    Log.i(TAG, "activateAlerts: ")
                    alertsManager.fireAlert(it)
                }
            }
        }
    }


}

