package com.example.android.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.android.base.BaseViewModel
import com.example.android.models.AudioItem
import com.example.android.repositories.AudioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MediaViewModel @Inject constructor(private val audioRepository: AudioRepository) :
    BaseViewModel() {
    private val _isFavourite = MutableLiveData<Boolean>()

    val isFavourite: LiveData<Boolean> = _isFavourite

    fun insertFavouriteSurahList(audioItem: AudioItem) {

        viewModelScope.launch {
            try {
                if(_isFavourite.value != null && _isFavourite.value!!){
                    audioRepository.deleteFavourite(audioItem)
                }else{
                    audioRepository.insertFavourite(audioItem)
                }

            } catch (exception: Exception) {
                Timber.d("Error while inserting audio ${exception.message}")
                _errorMessage.postValue(onHandleError(exception))
            }
        }
    }


//    fun isAudioItemExist(audioItem: AudioItem) {
//        viewModelScope.launch {
//
//            return@launch audioRepository.isExist(audioItem)
//                .onStart {
//                    _loading.postValue(true)
//                }.onCompletion {
//                    _loading.postValue(false)
//                }
//                .catch { exception ->
//                    Timber.d("Error while loading feeds ${exception.message}")
//                    _errorMessage.postValue(onHandleError(exception))
//                }.collect {
//                    if (it != null)
//                        _isFavourite.postValue(true)
//                    else
//                        _isFavourite.postValue(false)
//                }
//        }
//    }
}