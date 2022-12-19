package com.ravi.fimoviesdemo.util

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

enum class NetworkResult {
    CONNECTED,
    DISCONNECTED,
    DISCONNECTING
}

class NetworkCallback : ConnectivityManager.NetworkCallback(){

    val result = MutableLiveData<NetworkResult>()

    override fun onLost(network: Network) {
        result.postValue(NetworkResult.DISCONNECTED)
    }

    override fun onLosing(network: Network, maxMsToLive: Int) {
        result.postValue(NetworkResult.DISCONNECTING)
    }

    override fun onAvailable(network: Network) {
        result.postValue(NetworkResult.CONNECTED)
    }
}


class ConnectivityFactory {

    fun internetRequest() : NetworkRequest{
        return NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED)
            .build()
    }
}


class NetworkConnectionManager(context: Context){

    private val factory : ConnectivityFactory = ConnectivityFactory()
    private val callback : NetworkCallback = NetworkCallback()

    private val connectivityManager = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

    val result : LiveData<NetworkResult>  get() = callback.result

    fun registerCallback(){
        val request = factory.internetRequest()
        connectivityManager.registerNetworkCallback(request, callback)
    }

    fun unregisterCallback(){
        connectivityManager.unregisterNetworkCallback(callback)
    }

}