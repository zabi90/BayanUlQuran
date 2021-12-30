package com.example.android.repositories

import com.example.android.models.User
import com.example.android.network.services.UserService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class UserRepository @Inject constructor(private val userService: UserService) {

    suspend fun loadFeeds() : Flow<List<User>> = flow{
        val feeds = userService.loadUsers()
        emit(feeds)
    }
}