package com.example.android.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.android.models.AudioItem


@Database(entities = [AudioItem::class], version = 1)
abstract  class AppDatabase : RoomDatabase() {
    abstract fun audioDao(): AudioItemDao
}