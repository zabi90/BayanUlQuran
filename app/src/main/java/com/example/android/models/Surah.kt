package com.example.android.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Surah(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val title: String,
    @SerializedName("audios") val audios: List<AudioItem>
) : Parcelable
