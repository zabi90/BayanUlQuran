package com.example.android.extensions

import android.app.Activity
import android.graphics.Color
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar



fun Activity.showSnackBar(restId: Int, color: Int = Color.BLACK) {
    val snackBar = Snackbar.make(window.decorView.rootView, restId, Snackbar.LENGTH_LONG)
    snackBar.view.setBackgroundColor(color)
    snackBar.show()
}

fun Fragment.showSnackBar(restId: Int, color: Int = Color.BLACK) {
    activity?.let {
        val snackBar = Snackbar.make(it.window.decorView.rootView, restId, Snackbar.LENGTH_LONG)
        snackBar.view.setBackgroundColor(color)
        snackBar.show()
    }
}

fun Activity.showSnackBar(message: String, color: Int = Color.BLACK) {
    val snackBar = Snackbar.make(window.decorView.rootView, message, Snackbar.LENGTH_LONG)
    snackBar.view.setBackgroundColor(color)
    snackBar.show()
}

fun Fragment.showSnackBar(message: String, color: Int = Color.BLACK) {
    activity?.let {
        val snackBar = Snackbar.make(it.window.decorView.rootView, message, Snackbar.LENGTH_LONG)
        snackBar.view.setBackgroundColor(color)
        snackBar.show()
    }
}