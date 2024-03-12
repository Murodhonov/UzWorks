package dev.goblingroup.uzworks

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.util.Log
import dev.goblingroup.uzworks.utils.ConstValues.TAG

class ConnectivityBroadcastReceiver : BroadcastReceiver() {

    private var isConnected = true

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == ConnectivityManager.CONNECTIVITY_ACTION) {
            Log.d(TAG, "onReceive: checking network connectivity")
            val network = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = network.activeNetworkInfo
            isConnected = activeNetwork?.isConnected ?: false
            (context as MainActivity).onNetworkChange(isConnected)
        }
    }
}