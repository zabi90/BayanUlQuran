package com.example.android.repositories

import com.example.android.models.AudioItem
import com.example.android.network.services.AudioService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AudioRepository @Inject constructor(private val audioService: AudioService) {

    suspend fun loadSurahs() : Flow<List<AudioItem>> = flow{
        val feeds = audioService.loadAudio()
        emit(feeds)
    }
}