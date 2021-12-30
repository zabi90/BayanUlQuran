package com.example.android.extensions

import android.app.Activity
import android.widget.Toast
import androidx.fragment.app.Fragment



fun Activity.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Activity.toast(msgId: Int) {
    Toast.makeText(this, getString(msgId), Toast.LENGTH_SHORT).show()
}

fun Fragment.toast(message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun Fragment.toast(msgId: Int) {
    Toast.makeText(context, getString(msgId), Toast.LENGTH_SHORT).show()
}