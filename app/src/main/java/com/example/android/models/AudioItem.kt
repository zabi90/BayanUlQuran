package com.example.android.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class AudioItem(
    @SerializedName("link") val url: String,
    @PrimaryKey
    @SerializedName("fname") val title: String,
    val isFavourite: Boolean
) : Parcelable
