package dev.goblingroup.uzworks

import android.app.Application
import android.util.Log
import dev.goblingroup.uzworks.networking.ApiClient

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Log.d(
            "TAG",
            "onCreate: ${this::class.java.simpleName} initialized ${ApiClient::class.java.simpleName}"
        )
        ApiClient.initialize(this)
    }
}