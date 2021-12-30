package com.example.android.injection.modules

import com.example.android.network.services.AudioService
import com.example.android.network.services.UserService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    fun getFeedsService(@Named(RetrofitModule.PUBLIC_CLIENT) retrofit: Retrofit): UserService {
        return retrofit.create(UserService::class.java)
    }

    @Provides
    fun getAudioService(@Named(RetrofitModule.PUBLIC_CLIENT) retrofit: Retrofit): AudioService {
        return retrofit.create(AudioService::class.java)
    }

}