package dev.goblingroup.uzworks

import android.app.Application
import dev.goblingroup.uzworks.networking.ApiClient

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ApiClient.initialize(this)
    }
}