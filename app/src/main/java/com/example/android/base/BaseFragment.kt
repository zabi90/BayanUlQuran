package com.example.android.base

import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.android.AndroidApp
import com.example.android.extensions.toast


abstract class BaseFragment : Fragment() {

    val Fragment.app: AndroidApp
        get() = activity?.application as AndroidApp

    private var baseViewModel: BaseViewModel? = null
    private val loadingFragment: LoadingFragment = LoadingFragment()

    abstract fun setListeners()

    abstract fun setViewModel(): BaseViewModel?


    override fun onStart() {
        super.onStart()
        setListeners()
        baseViewModel = setViewModel()
        baseViewModel?.let {
//            it.isLoading.observe(this, Observer { isLoading ->
//                if (isLoading) {
//                    showLoading()
//                } else {
//                    hideLoading()
//                }
//            })

            it.errorMessage.observe(this, Observer { error ->
                toast(error.description)
            })
        }
    }


    fun showLoading() {

        if (!loadingFragment.isAdded) {
            childFragmentManager.let {
                loadingFragment.show(it, "loading")
                loadingFragment.isCancelable = false
            }
        }
    }

    fun hideLoading() {
        if (loadingFragment.isAdded) {
            loadingFragment.dismiss()
        }
    }

}