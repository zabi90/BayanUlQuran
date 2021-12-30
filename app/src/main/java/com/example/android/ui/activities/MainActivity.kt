package com.example.android.ui.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.android.R
import com.example.android.base.BaseActivity
import com.example.android.base.BaseViewModel
import com.example.android.databinding.ActivityMainBinding
import com.example.android.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : BaseActivity() {


    private lateinit var binding: ActivityMainBinding
    private val viewModel:MainViewModel by viewModels()
    //region Activity overrides methods

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView3) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigation.setupWithNavController(navController)

        //viewModel.loadFeeds().observe(this, {
            //binding.titleTextView.text = it.toString()
       // })
    }
    // endregion

    //region Base class and interface override methods
    override fun setListeners() {

    }

    override fun setViewModel(): BaseViewModel {
       return viewModel
    }
    //endregion
}
