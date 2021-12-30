package com.example.android.extensions

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog


fun Context.hideKeyboard(view: View) {
    (getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.apply {
        hideSoftInputFromWindow(view.windowToken, 0)
    }
}

fun Context.showKeyboard() {
    (getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.apply {
        toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }
}

fun Context.showKeyboard(view: View) {

    if (view.requestFocus()) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?

        // here is one more tricky issue
        // imm.showSoftInputMethod doesn't work well
        // and imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0) doesn't work well for all cases too
        imm?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

}

fun Context.showAlert(
    title: String?,
    message: String,
    positiveText: String,
    negativeText: String,
    positiveAction: () -> Unit,
    negativeAction: () -> Unit
) {
    val builder = AlertDialog.Builder(this)

    // Set the alert dialog title
    title.let {
        builder.setTitle(title)
    }


    // Display a message on alert dialog
    builder.setMessage(message)

    // Set a positive button and its click listener on alert dialog

    builder.setPositiveButton(positiveText) { dialog, which ->
        positiveAction()
        dialog.dismiss()
    }


    // Display a negative button on alert dialog
    builder.setNegativeButton(negativeText) { dialog, which ->
        negativeAction()
        dialog.dismiss()
    }

    // Display a neutral button on alert dialog
//    builder.setNeutralButton("Cancel"){_,_ ->
//
//    }

    // Finally, make the alert dialog using builder
    val dialog: AlertDialog = builder.create()

    // Display the alert dialog on app interface
    dialog.show()
}

fun Context.showAlert(
    title: String?,
    message: String,
    positiveText: String,
    positiveAction: () -> Unit
) {
    val builder = AlertDialog.Builder(this)

    // Set the alert dialog title
    title.let {
        builder.setTitle(title)
    }


    // Display a message on alert dialog
    builder.setMessage(message)

    // Set a positive button and its click listener on alert dialog

    builder.setPositiveButton(positiveText) { dialog, which ->
        positiveAction()
        dialog.dismiss()
    }


    // Finally, make the alert dialog using builder
    val dialog: AlertDialog = builder.create()

    // Display the alert dialog on app interface
    dialog.show()
}

