package com.example.android.repositories

import com.example.android.database.AppDatabase
import com.example.android.models.AudioItem
import com.example.android.network.services.AudioService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AudioRepository @Inject constructor(private val audioService: AudioService,private val database: AppDatabase) {
    var surahList: List<AudioItem> = mutableListOf()

    suspend fun loadSurahs(shouldRefresh: Boolean): Flow<List<AudioItem>> = flow {

        if (shouldRefresh) {
            surahList = audioService.loadAudio()
        } else if (surahList.isEmpty()) {
            surahList = audioService.loadAudio()
        }
        emit(surahList)
    }

    suspend fun loadFavourites(): Flow<List<AudioItem>> {
        return database.audioDao().getAll()
    }

    suspend fun insertFavourite(audioItem: AudioItem){
         database.audioDao().insert(audioItem)
    }

    suspend fun deleteFavourite(audioItem: AudioItem){
        database.audioDao().delete(audioItem)
    }

}