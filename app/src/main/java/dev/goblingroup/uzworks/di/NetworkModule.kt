package dev.goblingroup.uzworks.di

import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.goblingroup.uzworks.networking.AuthService
import dev.goblingroup.uzworks.networking.DistrictService
import dev.goblingroup.uzworks.networking.ExperienceService
import dev.goblingroup.uzworks.networking.JobCategoryService
import dev.goblingroup.uzworks.networking.JobService
import dev.goblingroup.uzworks.networking.RegionService
import dev.goblingroup.uzworks.networking.SecuredJobService
import dev.goblingroup.uzworks.networking.SecuredUserService
import dev.goblingroup.uzworks.networking.SecuredWorkerService
import dev.goblingroup.uzworks.networking.UserService
import dev.goblingroup.uzworks.networking.WorkerService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideBaseUrl(): String = "https://accepted-first-pug.ngrok-free.app/"

    @Provides
    @Singleton
    @Named(value = "token")
    fun provideToken(
        sharedPreferences: SharedPreferences
    ): String {
        return sharedPreferences.getString("token", null).toString()
    }

    @Provides
    @Singleton
    fun provideInterceptor(
        @Named(value = "token") token: String
    ): Interceptor {
        return Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
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
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideConverterFactory(): GsonConverterFactory {
        return GsonConverterFactory.create()
    }

    @Provides
    @Singleton
    @Named(value = "secured_retrofit")
    fun provideSecuredRetrofit(
        baseUrl: String,
        httpClient: OkHttpClient,
        converterFactory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(converterFactory)
            .client(httpClient)
            .build()
    }

    @Provides
    @Singleton
    @Named(value = "retrofit")
    fun provideRetrofit(
        baseUrl: String,
        converterFactory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(converterFactory)
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
    fun provideSecuredJobService(
        @Named(value = "secured_retrofit") retrofit: Retrofit
    ): SecuredJobService {
        return retrofit.create(SecuredJobService::class.java)
    }

    @Provides
    @Singleton
    fun provideSecuredWorkerService(
        @Named(value = "secured_retrofit") retrofit: Retrofit
    ): SecuredWorkerService {
        return retrofit.create(SecuredWorkerService::class.java)
    }

    @Provides
    @Singleton
    fun provideUserService(
        @Named(value = "retrofit") retrofit: Retrofit
    ): UserService {
        return retrofit.create(UserService::class.java)
    }

    @Provides
    @Singleton
    fun provideSecuredUserService(
        @Named(value = "secured_retrofit") retrofit: Retrofit
    ): SecuredUserService {
        return retrofit.create(SecuredUserService::class.java)
    }

    @Provides
    @Singleton
    fun provideExperience(
        @Named(value = "secured_retrofit") retrofit: Retrofit
    ): ExperienceService {
        return retrofit.create(ExperienceService::class.java)
    }

}