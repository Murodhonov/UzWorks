package dev.goblingroup.uzworks.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

//    @Provides
//    @Singleton
//    fun provideBaseUrl(): String = "http://172.16.14.203:28/"
//
//    @Provides
//    @Singleton
//    fun provideRetrofit(
//        baseUrl: String
//    ): Retrofit {
//
//    }


}