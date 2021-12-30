package com.example.android.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.android.models.AudioItem
import kotlinx.coroutines.flow.Flow

@Dao
interface AudioItemDao {

    @Query("SELECT * FROM AudioItem")
    fun getAll(): Flow<List<AudioItem>>

    @Insert
    suspend fun insert(audioItem: AudioItem)

    @Delete
    suspend fun delete(audioItem: AudioItem) : Int
}