package com.github.nitrico.pomodoro.util

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.support.annotation.ArrayRes
import android.support.annotation.ColorInt
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView


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

fun TextView.setTextOrHideView(string: String?) {
    if (string.isNullOrEmpty()) hide()
    else {
        show()
        text = string
    }
}

fun ImageView.load(url: String, circular: Boolean = false) = ImageLoader.load(this, url, circular)



///// VIEW /////

fun View.show() { visibility = View.VISIBLE }
fun View.hide() { visibility = View.GONE }

fun View.setMargins(l: Int, t: Int, r: Int, b: Int) {
    if (layoutParams is ViewGroup.MarginLayoutParams) {
        val params = layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(l, t, r, b)
        requestLayout()
    }
}



///// DRAWER LAYOUT /////

fun DrawerLayout.isOpen() = isDrawerOpen(GravityCompat.START)
fun DrawerLayout.open() = openDrawer(GravityCompat.START)
fun DrawerLayout.close() = closeDrawer(GravityCompat.START)
fun DrawerLayout.toggle() = if (isOpen()) close() else open()

inline fun DrawerLayout.consume(f: () -> Unit): Boolean {
    f()
    close()
    return true
}



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



///// SYSTEM BARS /////


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
