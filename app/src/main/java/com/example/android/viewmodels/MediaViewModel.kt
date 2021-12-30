package com.example.android.viewmodels

import androidx.lifecycle.viewModelScope
import com.example.android.base.BaseViewModel
import com.example.android.models.AudioItem
import com.example.android.repositories.AudioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MediaViewModel  @Inject constructor(private val audioRepository: AudioRepository) : BaseViewModel() {

    fun insertFavouriteSurahList(audioItem: AudioItem) {

        viewModelScope.launch {
            try {
                audioRepository.insertFavourite(audioItem)
            } catch (exception: Exception) {
                Timber.d("Error while inserting audio ${exception.message}")
                _errorMessage.postValue(onHandleError(exception))
            }
        }
    }
}