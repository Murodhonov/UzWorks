package com.goblindevs.uzworks

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager

class ConnectivityBroadcastReceiver : BroadcastReceiver() {

    private var isConnected = true

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == ConnectivityManager.CONNECTIVITY_ACTION) {
            val network = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = network.activeNetworkInfo
            isConnected = activeNetwork?.isConnected ?: false
            (context as MainActivity).onNetworkChange(isConnected)
        }
    }
}