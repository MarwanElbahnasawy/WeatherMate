package com.example.weathermate.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest

object NetworkManager {

    private lateinit var connectivityManager: ConnectivityManager
    private var isNetworkConnected = false

    private var listener  : NetworkListener? = null

    fun init(context: Context) {
        connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)

        updateNetworkStatus()
    }

    fun isInternetConnected(): Boolean {
        return isNetworkConnected
    }

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            updateNetworkStatus()
        }

        override fun onLost(network: Network) {
            updateNetworkStatus()
        }
    }

    private fun updateNetworkStatus() {

        val activeNetwork = connectivityManager.activeNetwork
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        isNetworkConnected =
            activeNetwork!= null && capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) && activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting


        notifyListeners()
    }

    fun setListener(listener: NetworkListener){
        this.listener = listener
        notifyListeners()
    }

    private fun notifyListeners() {
        listener?.onNetworkStatusChanged(isNetworkConnected)
    }

    interface NetworkListener {
        fun onNetworkStatusChanged(isConnected: Boolean)
    }
}