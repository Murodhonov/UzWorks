package dev.goblingroup.uzworks.networking

import android.content.Context
import android.util.Log
import dev.goblingroup.uzworks.singleton.MySharedPreference
import me.sianaki.flowretrofitadapter.FlowCallAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private const val BASE_URL = "http://172.16.14.203:28/"
    private var authToken: String? = null

    fun initialize(context: Context) {
        if (authToken == null) {
            authToken = MySharedPreference.getInstance(context).getToken()
            Log.d(
                "TAG",
                "initialize: ${this::class.java.simpleName} ${authToken} got token from shared preference"
            )
        }
    }

    private fun getRetrofit(): Retrofit {
        Log.d("TAG", "getRetrofit: ${this::class.java.simpleName} is working")
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(FlowCallAdapterFactory.create())
            .build()
    }

    private fun getSecuredRetrofit(): Retrofit {
        val interceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $authToken")
                .build()
            chain.proceed(request)
        }

        val httpClient = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        Log.d(
            "TAG",
            "getSecuredRetrofit: ${this::class.java.simpleName} $authToken got token and added"
        )

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(FlowCallAdapterFactory.create())
            .client(httpClient)
            .build()
    }

    val authService: AuthService =
        getRetrofit().create(AuthService::class.java)
    val districtService: DistrictService =
        getRetrofit().create(DistrictService::class.java)
    val jobService: JobService =
        getRetrofit().create(JobService::class.java)
    val jobCategoryService: JobCategoryService =
        getRetrofit().create(JobCategoryService::class.java)
    val regionService: RegionService =
        getRetrofit().create(RegionService::class.java)
    val workerService: WorkerService =
        getRetrofit().create(WorkerService::class.java)

    val securedDistrictService: SecuredDistrictService =
        getSecuredRetrofit().create(SecuredDistrictService::class.java)
    val securedJobService: SecuredJobService =
        getSecuredRetrofit().create(SecuredJobService::class.java)
    val securedJobCategoryService: SecuredJobCategoryService =
        getSecuredRetrofit().create(SecuredJobCategoryService::class.java)
    val securedRegionService: SecuredRegionService =
        getSecuredRetrofit().create(SecuredRegionService::class.java)
    val securedWorkerService: SecuredWorkerService =
        getSecuredRetrofit().create(SecuredWorkerService::class.java)
}