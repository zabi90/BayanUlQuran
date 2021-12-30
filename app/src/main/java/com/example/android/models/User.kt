package com.example.android.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    @SerializedName("id") var id: String,
    @SerializedName("user_name") var userName: String,
    @SerializedName("full_name") var fullName: String,
    @SerializedName("age") var age: String,
    @SerializedName("feed_image") var feedImage: String,
    @SerializedName("image") var image: String,
    @SerializedName("info") var info: String
): Parcelable