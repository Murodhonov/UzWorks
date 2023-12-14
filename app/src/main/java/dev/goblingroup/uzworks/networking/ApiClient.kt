package dev.goblingroup.uzworks.networking

import me.sianaki.flowretrofitadapter.FlowCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private const val BASE_URL = "http://172.16.14.203:28/"

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(FlowCallAdapterFactory.create())
            .build()
    }

    val authService: AuthService = getRetrofit().create(AuthService::class.java)
}