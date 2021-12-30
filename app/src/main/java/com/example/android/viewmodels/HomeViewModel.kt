package com.example.android.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.android.base.BaseViewModel
import com.example.android.models.AudioItem
import com.example.android.models.User
import com.example.android.repositories.AudioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val audioRepository: AudioRepository) :
    BaseViewModel() {


    private val _audioItems = MutableLiveData<List<AudioItem>>()

    val audioItems: LiveData<List<AudioItem>> = _audioItems

    fun loadSurahList(shouldRefresh : Boolean) {

        viewModelScope.launch {
            return@launch audioRepository.loadSurahs(shouldRefresh)
                .onStart {
                    _loading.postValue(true)
                }.onCompletion {
                    _loading.postValue(false)
                }.catch { exception ->
                    Timber.d("Error while loading feeds ${exception.message}")
                    _errorMessage.postValue(onHandleError(exception))
                }
                .collect {
                    _audioItems.postValue(it)
                }
        }

    }
}