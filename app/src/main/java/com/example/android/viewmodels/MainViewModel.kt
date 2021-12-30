package com.example.android.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.android.base.BaseViewModel
import com.example.android.models.User
import com.example.android.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val userRepository: UserRepository) :
    BaseViewModel() {


    fun loadFeeds(): LiveData<List<User>> {
        val response = MutableLiveData<List<User>>()
        viewModelScope.launch {

            return@launch userRepository.loadFeeds()
                .onStart {
                    _loading.postValue(true)
                }.onCompletion {
                   _loading.postValue(false)
                }
                .catch { exception ->
                    Timber.d("Error while loading feeds ${exception.message}")
                   _errorMessage.postValue(onHandleError(exception))
                }.collect {
                    response.postValue(it)
                }
        }
        return response
    }
}