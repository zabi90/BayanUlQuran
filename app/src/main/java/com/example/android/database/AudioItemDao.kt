package com.example.android.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.android.models.AudioItem
import kotlinx.coroutines.flow.Flow

@Dao
interface AudioItemDao {

    @Query("SELECT * FROM AudioItem")
    fun getAll(): Flow<List<AudioItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(audioItem: AudioItem)

    @Delete
    suspend fun delete(audioItem: AudioItem): Int

    @Query("SELECT * FROM AudioItem WHERE title = :title ")
    suspend fun isExist(title: String) :  AudioItem
}