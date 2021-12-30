package com.example.android.viewmodels

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.android.models.User
import com.example.android.repositories.UserRepository
import com.example.android.rules.MainCoroutineRule
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.Q])
class MainViewModelTest{
    private val userRepository = mock<UserRepository>()

    lateinit var viewModel: MainViewModel

    @get:Rule
    var rule = InstantTaskExecutorRule()

    @get:Rule
    val mainTestCoroutineRule = MainCoroutineRule()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        viewModel = MainViewModel(userRepository)
    }

    @Test
    fun `load user list` () = mainTestCoroutineRule.runBlockingTest{
       val userList = ArrayList<User>()

        Mockito.`when`(userRepository.loadFeeds())
            .doReturn(flowOf(userList))

        assertEquals(0,viewModel.loadFeeds().value?.size)
    }

}