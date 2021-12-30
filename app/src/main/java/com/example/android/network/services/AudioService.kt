package com.example.android.network.services

import com.example.android.models.AudioItem
import retrofit2.http.GET

interface AudioService {
    @GET("surah")
    suspend fun loadAudio(): List<AudioItem>
}