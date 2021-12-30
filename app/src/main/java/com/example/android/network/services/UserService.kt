package com.example.android.network.services

import com.example.android.models.User
import retrofit2.http.GET

interface UserService {
    @GET("feeds")
    suspend fun loadUsers():List<User>
}