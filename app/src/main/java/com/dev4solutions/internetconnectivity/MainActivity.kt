package com.dev4solutions.internetconnectivity

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.InetAddress

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
    }

    private lateinit var connectivityManager: ConnectivityManager

    /*
    * Assuming that network is connected
    * */
    private var isConnected = true

    /*
    * Mutable Live data for listener
    * */
    private val netListener = MutableLiveData<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        /*
        * Get Connectivity manager instance
        * */
        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        /*
        * Change Listeners
        * */
        netListener.observe(this, Observer {
            tvInternetStatus.text = it
        })

        /*
        * Check Status on start
        * */
        checkInternet()
    }

    /*
    * Check Internet is working or not
    * */
    fun checkInternet() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Here you can change to your server or may be more reliable host whose uptime is 99% high.
                val ipAddress = InetAddress.getByName("www.google.com")
                if (ipAddress.hostAddress.isNullOrEmpty()) {
                    netListener.postValue("You are OFFLINE now")
                } else {
                    netListener.postValue("You are ONLINE now")
                }
            } catch (e: Exception) {
                netListener.postValue("You are OFFLINE now")
                // Exception : unknownhost
                e.printStackTrace()
            }
        }
    }


    /*
    * Connectivity Callback listening any network connectivity change
    * */
    private val connectivityCallback = object : ConnectivityManager.NetworkCallback() {

        /*
        * Callback when network is connected/available
        * */
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            checkInternet()
        }

        /*
        * Callback : when network disconnected
        * */
        override fun onLost(network: Network) {
            super.onLost(network)
            checkInternet()
        }
    }


    override fun onResume() {
        super.onResume()
        /*
        * Register Network callback for any change
        * */
        connectivityManager.registerNetworkCallback(
                NetworkRequest.Builder()
                        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                        .build(), connectivityCallback
        )
    }

    override fun onPause() {
        super.onPause()
        /*
        * Unregister Callback on Pause of Activity
        * */
        connectivityManager.unregisterNetworkCallback(connectivityCallback)
    }
}