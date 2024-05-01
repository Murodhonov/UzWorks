package dev.goblingroup.uzworks.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.goblingroup.uzworks.networking.AuthService
import dev.goblingroup.uzworks.networking.DistrictService
import dev.goblingroup.uzworks.networking.ExperienceService
import dev.goblingroup.uzworks.networking.JobCategoryService
import dev.goblingroup.uzworks.networking.JobService
import dev.goblingroup.uzworks.networking.RegionService
import dev.goblingroup.uzworks.networking.UserService
import dev.goblingroup.uzworks.networking.WorkerService
import dev.goblingroup.uzworks.utils.ConstValues.BASE_URL
import dev.goblingroup.uzworks.utils.NetworkHelper
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideNetworkHelper(@ApplicationContext context: Context) = NetworkHelper(context)

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        httpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthService(
        retrofit: Retrofit
    ): AuthService {
        return retrofit.create(AuthService::class.java)
    }

    @Provides
    @Singleton
    fun provideDistrictService(
        retrofit: Retrofit
    ): DistrictService {
        return retrofit.create(DistrictService::class.java)
    }

    @Provides
    @Singleton
    fun provideJobService(
        retrofit: Retrofit
    ): JobService {
        return retrofit.create(JobService::class.java)
    }

    @Provides
    @Singleton
    fun provideJobCategoryService(
        retrofit: Retrofit
    ): JobCategoryService {
        return retrofit.create(JobCategoryService::class.java)
    }

    @Provides
    @Singleton
    fun provideRegionService(
        retrofit: Retrofit
    ): RegionService {
        return retrofit.create(RegionService::class.java)
    }

    @Provides
    @Singleton
    fun provideWorkerService(
        retrofit: Retrofit
    ): WorkerService {
        return retrofit.create(WorkerService::class.java)
    }

    @Provides
    @Singleton
    fun provideSecuredUserService(
        retrofit: Retrofit
    ): UserService {
        return retrofit.create(UserService::class.java)
    }

    @Provides
    @Singleton
    fun provideExperience(
        retrofit: Retrofit
    ): ExperienceService {
        return retrofit.create(ExperienceService::class.java)
    }

}