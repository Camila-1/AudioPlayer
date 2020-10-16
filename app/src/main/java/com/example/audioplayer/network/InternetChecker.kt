package com.example.audioplayer.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET

class InternetChecker(private val context: Context) {

    fun checkInternetConnection(callback: (Boolean) -> Unit) {

        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                val connected = networkCapabilities.hasCapability(NET_CAPABILITY_INTERNET)
                callback(connected)
            }

            override fun onAvailable(network: Network){
                callback(true)
                connectivityManager.unregisterNetworkCallback(this)
            }
            override fun onUnavailable() {
                callback(false)
            }

            override fun onLost(network: Network) {
                callback(false)
            }
        }
    }

    fun isConnected(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        val isOnline = capabilities?.hasCapability(NET_CAPABILITY_INTERNET) ?: false
        return isOnline
    }
}