package com.example.android.repositories

import com.example.android.database.AppDatabase
import com.example.android.models.AudioItem
import com.example.android.models.Surah
import com.example.android.models.User
import com.example.android.network.services.AudioService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AudioRepository @Inject constructor(
    private val audioService: AudioService,
    private val database: AppDatabase,
    private val surahList : List<Surah>
) {
   // var surahList: List<AudioItem> = mutableListOf()

    suspend fun loadSurahs(shouldRefresh: Boolean): Flow<List<Surah>> = flow {

//        if (shouldRefresh) {
//            surahList = audioService.loadAudio()
//        } else if (surahList.isEmpty()) {
//            surahList = audioService.loadAudio()
//        }
        emit(surahList)
    }

    suspend fun loadFavourites(): Flow<List<AudioItem>> {
        return database.audioDao().getAll()
    }

    suspend fun insertFavourite(audioItem: AudioItem) {
        database.audioDao().insert(audioItem)
    }

    suspend fun deleteFavourite(audioItem: AudioItem) {
        database.audioDao().delete(audioItem)
    }

    suspend fun isExist(audioItem: AudioItem): Flow<AudioItem?> = flow {
        emit(database.audioDao().isExist(audioItem.title))
    }


}