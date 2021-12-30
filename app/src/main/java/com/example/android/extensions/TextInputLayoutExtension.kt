package com.example.android.extensions
import android.widget.TextView
import com.google.android.material.textfield.TextInputLayout

fun TextInputLayout.getText(): String {
    return editText?.text.toString()
}

fun TextInputLayout.setText(text: String) {
    editText?.setText(text, TextView.BufferType.EDITABLE)
}

fun TextInputLayout.clear(){
    editText?.setText("", TextView.BufferType.EDITABLE)
}