package com.example.android.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class AudioItem(
    @SerializedName("id") val id: Int,
    @SerializedName("url") val url: String,
    @SerializedName("title") val title: String
) : Parcelable
