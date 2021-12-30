package com.example.android.base


import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.android.AndroidApp
import com.example.android.R
import com.example.android.extensions.showSnackBar
import com.example.android.extensions.toast
import com.example.android.receivers.NetworkConnectionReceiver


abstract class BaseActivity : AppCompatActivity() {


    val Activity.app: AndroidApp
        get() = application as AndroidApp


    private var baseViewModel: BaseViewModel? = null

    private val loadingFragment: LoadingFragment = LoadingFragment()

    abstract fun setListeners()

    abstract fun setViewModel(): BaseViewModel?


    override fun onStart() {
        super.onStart()
        setListeners()
        baseViewModel = setViewModel()
        baseViewModel?.let {
            it.isLoading.observe(this, Observer { isLoading ->
                if (isLoading) {
                    showLoading()
                } else {
                    hideLoading()
                }
            })

            it.errorMessage.observe(this, Observer {error->
                toast(error.description)
            })
        }
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            listener,
            IntentFilter(NetworkConnectionReceiver.CONNECTION_RECEIVER_ACTION)
        )
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(listener)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    fun setupActionBar(title: String, showBack: Boolean) {

        if (showBack) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        supportActionBar?.title = title

    }

    fun showLoading() {
        if (!loadingFragment.isAdded) {
            loadingFragment.show(supportFragmentManager, "loading")
            loadingFragment.isCancelable = false
        }
    }

    fun hideLoading() {
        if (loadingFragment.isAdded) {
            loadingFragment.dismiss()
        }
    }



    private val listener = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                val status = it.getBooleanExtra(NetworkConnectionReceiver.STATUS, false)
                if (status) {
                    showSnackBar(
                        R.string.internet_connection_back,
                        ContextCompat.getColor(this@BaseActivity, R.color.seed)
                    )
                } else {
                    showSnackBar(
                        R.string.internet_connection_lost,
                        ContextCompat.getColor(this@BaseActivity, R.color.error)
                    )
                }
            }
        }
    }
}
