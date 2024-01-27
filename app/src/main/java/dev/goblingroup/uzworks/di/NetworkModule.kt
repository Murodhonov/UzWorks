package dev.goblingroup.uzworks.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.goblingroup.uzworks.networking.AuthService
import dev.goblingroup.uzworks.networking.DistrictService
import dev.goblingroup.uzworks.networking.JobCategoryService
import dev.goblingroup.uzworks.networking.JobService
import dev.goblingroup.uzworks.networking.RegionService
import dev.goblingroup.uzworks.networking.SecuredDistrictService
import dev.goblingroup.uzworks.networking.SecuredJobCategoryService
import dev.goblingroup.uzworks.networking.SecuredJobService
import dev.goblingroup.uzworks.networking.SecuredRegionService
import dev.goblingroup.uzworks.networking.SecuredWorkerService
import dev.goblingroup.uzworks.networking.WorkerService
import dev.goblingroup.uzworks.singleton.MySharedPreference
import dev.goblingroup.uzworks.utils.NetworkHelper
import me.sianaki.flowretrofitadapter.FlowCallAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideNetworkHelper(
        @ApplicationContext context: Context
    ): NetworkHelper {
        return NetworkHelper(context)
    }

    @Provides
    @Singleton
    fun provideBaseUrl(): String = "http://172.16.14.203:28/"

    @Provides
    @Singleton
    @Named(value = "token")
    fun provideToken(
        @ApplicationContext context: Context
    ): String {
        return MySharedPreference.getInstance(context).getToken().toString()
    }

    @Provides
    @Singleton
    fun provideInterceptor(
        @Named(value = "token") token: String
    ): Interceptor {
        return Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer ")
                .build()
            chain.proceed(request)
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        interceptor: Interceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
    }

    @Provides
    @Singleton
    @Named(value = "secured_retrofit")
    fun provideSecuredRetrofit(
        baseUrl: String,
        httpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(FlowCallAdapterFactory.create())
            .client(httpClient)
            .build()
    }

    @Provides
    @Singleton
    @Named(value = "retrofit")
    fun provideRetrofit(
        baseUrl: String
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthService(
        @Named(value = "retrofit") retrofit: Retrofit
    ): AuthService {
        return retrofit.create(AuthService::class.java)
    }

    @Provides
    @Singleton
    fun provideDistrictService(
        @Named(value = "retrofit") retrofit: Retrofit
    ): DistrictService {
        return retrofit.create(DistrictService::class.java)
    }

    @Provides
    @Singleton
    fun provideJobService(
        @Named(value = "retrofit") retrofit: Retrofit
    ): JobService {
        return retrofit.create(JobService::class.java)
    }

    @Provides
    @Singleton
    fun provideJobCategoryService(
        @Named(value = "retrofit") retrofit: Retrofit
    ): JobCategoryService {
        return retrofit.create(JobCategoryService::class.java)
    }

    @Provides
    @Singleton
    fun provideRegionService(
        @Named(value = "retrofit") retrofit: Retrofit
    ): RegionService {
        return retrofit.create(RegionService::class.java)
    }

    @Provides
    @Singleton
    fun provideWorkerService(
        @Named(value = "retrofit") retrofit: Retrofit
    ): WorkerService {
        return retrofit.create(WorkerService::class.java)
    }

    @Provides
    @Singleton
    fun provideSecuredDistrictService(
        @Named(value = "secured_retrofit") retrofit: Retrofit
    ): SecuredDistrictService {
        return retrofit.create(SecuredDistrictService::class.java)
    }

    @Provides
    @Singleton
    fun provideSecuredJobService(
        @Named(value = "secured_retrofit") retrofit: Retrofit
    ): SecuredJobService {
        return retrofit.create(SecuredJobService::class.java)
    }

    @Provides
    @Singleton
    fun provideSecuredJobCategoryService(
        @Named(value = "secured_retrofit") retrofit: Retrofit
    ): SecuredJobCategoryService {
        return retrofit.create(SecuredJobCategoryService::class.java)
    }

    @Provides
    @Singleton
    fun provideSecuredRegionService(
        @Named(value = "secured_retrofit") retrofit: Retrofit
    ): SecuredRegionService {
        return retrofit.create(SecuredRegionService::class.java)
    }

    @Provides
    @Singleton
    fun provideSecuredWorkerService(
        @Named(value = "secured_retrofit") retrofit: Retrofit
    ): SecuredWorkerService {
        return retrofit.create(SecuredWorkerService::class.java)
    }


}