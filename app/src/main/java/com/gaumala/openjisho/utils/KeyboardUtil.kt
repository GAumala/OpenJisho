package com.gaumala.openjisho.utils

import android.widget.EditText
import android.content.Context.INPUT_METHOD_SERVICE
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT


fun EditText.openKeyboard() {
    this.requestFocus()
    val manager = this.context
        .getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    manager.showSoftInput(this, SHOW_IMPLICIT)
}

fun View.hideKeyboard() {
    this.requestFocus()
    val manager = this.context
        .getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    manager.hideSoftInputFromWindow(windowToken, 0)
}
