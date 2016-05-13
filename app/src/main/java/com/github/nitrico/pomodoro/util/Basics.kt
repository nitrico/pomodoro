package com.github.nitrico.pomodoro.util

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.support.annotation.ArrayRes
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup

val Context.isPortrait: Boolean
    get() = resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT

val Context.dm: DisplayMetrics
    get() = resources.displayMetrics

val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density + 0.5).toInt()


fun consume(func: () -> Unit): Boolean {
    func()
    return true
}

fun Context.stringArrayRes(@ArrayRes arrayResId: Int): Lazy<List<String>> = lazy {
    resources.getStringArray(arrayResId).toList()
}

fun View.setMargins(l: Int, t: Int, r: Int, b: Int) {
    if (layoutParams is ViewGroup.MarginLayoutParams) {
        val params = layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(l, t, r, b)
        requestLayout()
    }
}


///// SYSTEM BARS /////

fun View.setLightStatusBar() {
    systemUiVisibility = systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
}


fun Context.hasNavigationBar(): Boolean {
    return if (Build.VERSION.SDK_INT < 19) false else !ViewConfiguration.get(this).hasPermanentMenuKey()
}

fun Context.getNavigationBarHeight(): Int {
    fun getIdString() = if (isPortrait) "navigation_bar_height" else "navigation_bar_height_landscape"
    if (!hasNavigationBar()) return 0
    if (navigationBarCanChangeItsPosition() && !isPortrait) return 0
    val id = resources.getIdentifier(getIdString(), "dimen", "android")
    if (id > 0) return resources.getDimensionPixelSize(id)
    return 0
}

fun Context.getNavigationBarWidth(): Int {
    if (!hasNavigationBar()) return 0
    if (navigationBarCanChangeItsPosition() && !isPortrait) {
        val id = resources.getIdentifier("navigation_bar_width", "dimen", "android")
        if (id > 0) return resources.getDimensionPixelSize(id)
    }
    return 0
}

private fun Context.navigationBarCanChangeItsPosition(): Boolean { // Only phone between 0-599dp can
    return dm.widthPixels != dm.heightPixels && resources.configuration.smallestScreenWidthDp < 600
}

fun Context.isNavigationBarHorizontal(): Boolean {
    if (!hasNavigationBar()) return false
    return !navigationBarCanChangeItsPosition() || dm.widthPixels < dm.heightPixels
}
