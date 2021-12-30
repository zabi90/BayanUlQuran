package com.example.android.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class AudioItem(
    @PrimaryKey
    @SerializedName("id") val id: Int,
    @SerializedName("url") val url: String,
    @SerializedName("title") val title: String,
    val isFavourite: Boolean
) : Parcelable
