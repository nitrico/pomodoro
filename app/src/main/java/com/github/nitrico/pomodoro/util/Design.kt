package com.github.nitrico.pomodoro.util

import android.support.annotation.ColorInt
import android.support.design.widget.Snackbar
import android.view.View


///// SNACK BAR /////

fun View.snack(message: String, length: Int = Snackbar.LENGTH_LONG, f: (Snackbar.() -> Unit)? = null) {
    val snack = Snackbar.make(this, message, length)
    if (f != null) snack.f()
    snack.show()
}

fun Snackbar.action(action: String, @ColorInt color: Int? = null, listener: (View) -> Unit) {
    setAction(action, listener)
    color?.let { setActionTextColor(color) }
}
