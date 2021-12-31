package com.example.android.injection.modules

import android.content.Context
import androidx.room.Room
import com.example.android.database.AppDatabase
import com.example.android.models.Surah
import com.example.android.utils.getJsonDataFromAsset
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    @Singleton
    fun getDataBase(@ApplicationContext applicationContext: Context): AppDatabase {

        return Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "banyan-ul-quran"
        ).build()

    }


    @Provides
    @Singleton
    fun getSurahList(@ApplicationContext applicationContext: Context): List<Surah> {

        val jsonFileString = getJsonDataFromAsset(applicationContext, "tafheem.json")

        val gson = Gson()

        val listSurahType = object : TypeToken<List<Surah>>() {}.type

        return gson.fromJson(jsonFileString, listSurahType)
    }

}