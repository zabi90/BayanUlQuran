package com.example.android.injection.modules

import com.example.android.database.AppDatabase
import com.example.android.models.Surah
import com.example.android.network.services.AudioService
import com.example.android.network.services.UserService
import com.example.android.repositories.AudioRepository
import com.example.android.repositories.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    fun getFeedRepository(userService: UserService): UserRepository {
        return UserRepository(userService)
    }

    @Provides
    fun getAudioRepository(audioService: AudioService, database: AppDatabase, surahList: List<Surah>): AudioRepository {
        return AudioRepository(audioService,database,surahList)
    }
}