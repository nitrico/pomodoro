package com.github.nitrico.pomodoro.tool

import android.annotation.TargetApi
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.os.Build
import android.support.annotation.*
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.github.nitrico.pomodoro.R

/**
 * Call the function received as a parameter and return true
 */
fun consume(f: () -> Unit): Boolean {
    f()
    return true
}

fun Long.toTwoDigitsString() = String.format("%02d", this)

fun Long.toTimeString(): String {
    val MINUTE: Long = 60
    val HOUR: Long = 3600
    if (this < MINUTE) return "${this}s"
    else if (this < HOUR) {
        val minutes: Long = this / MINUTE
        val seconds: Long = this - (minutes * MINUTE)
        return "${minutes.toTwoDigitsString()}m:${seconds.toTwoDigitsString()}s"
    }
    else {
        val hours: Long = this / HOUR
        val minutes: Long = (this - hours*HOUR) / MINUTE
        val seconds: Long = this - hours*HOUR - minutes*MINUTE
        return "${hours}h:${minutes.toTwoDigitsString()}m:${seconds.toTwoDigitsString()}s"
    }
}

val Int.dp: Int // dip to px conversion
    get() = (this * Resources.getSystem().displayMetrics.density + 0.5).toInt()

val Context.isPortrait: Boolean
    get() = resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT

val Context.primaryColor: Int
    @ColorInt get() {
        val typedValue = TypedValue()
        theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
        return typedValue.data
    }

fun Activity.setFullScreenLayout() {
    window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
}

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
fun Activity.setTaskDescription(@DrawableRes iconRes: Int = R.mipmap.ic_launcher,
                                @ColorInt color: Int = primaryColor) {
    if (Build.VERSION.SDK_INT < 21) return
    val bitmap = BitmapFactory.decodeResource(resources, iconRes)
    setTaskDescription(ActivityManager.TaskDescription(null, bitmap, color))
    bitmap.recycle()
}


///// VIEW /////

fun View.show() { visibility = View.VISIBLE }
fun View.hide() { visibility = View.GONE }

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

fun View.setMargins(l: Int, t: Int, r: Int, b: Int) {
    if (layoutParams is ViewGroup.MarginLayoutParams) {
        val params = layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(l, t, r, b)
        requestLayout()
    }
}

fun TextView.setTextOrHideView(string: String?) {
    if (string.isNullOrEmpty()) hide()
    else {
        show()
        text = string
    }
}

fun ImageView.load(url: String, circular: Boolean = false) = ImageLoader.load(this, url, circular)



///// DRAWER LAYOUT /////

private const val DEFAULT_DRAWER_GRAVITY = GravityCompat.START
val DrawerLayout.isOpen: Boolean get() = isDrawerOpen(DEFAULT_DRAWER_GRAVITY)
fun DrawerLayout.open() = openDrawer(DEFAULT_DRAWER_GRAVITY)
fun DrawerLayout.close() = closeDrawer(DEFAULT_DRAWER_GRAVITY)
fun DrawerLayout.toggle() = if (isOpen) close() else open()



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

val Context.statusBarHeight: Int
    get() {
        if (Build.VERSION.SDK_INT < 19) return 0
        val id = resources.getIdentifier("status_bar_height", "dimen", "android")
        return resources.getDimensionPixelSize(id)
    }

val Context.hasNavigationBar: Boolean
    get() {
        if (Build.VERSION.SDK_INT < 19) return false
        return !ViewConfiguration.get(this).hasPermanentMenuKey()
    }

val Context.isNavigationBarHorizontal: Boolean
    get() {
        if (!hasNavigationBar) return false
        val dm = resources.displayMetrics
        return !canNavigationBarChangeItsPosition || dm.widthPixels < dm.heightPixels
    }

private val Context.canNavigationBarChangeItsPosition: Boolean // Only phone between 0-599dp can
    get() {
        val dm = resources.displayMetrics
        return dm.widthPixels != dm.heightPixels && resources.configuration.smallestScreenWidthDp < 600
    }

val Context.navigationBarHeight: Int
    get() {
        if (!hasNavigationBar) return 0
        if (canNavigationBarChangeItsPosition && !isPortrait) return 0
        val idString = if (isPortrait) "navigation_bar_height" else "navigation_bar_height_landscape"
        val id = resources.getIdentifier(idString, "dimen", "android")
        if (id > 0) return resources.getDimensionPixelSize(id)
        return 0
    }

val Context.navigationBarWidth: Int
    get() {
        if (!hasNavigationBar) return 0
        if (canNavigationBarChangeItsPosition && !isPortrait) {
            val id = resources.getIdentifier("navigation_bar_width", "dimen", "android")
            if (id > 0) return resources.getDimensionPixelSize(id)
        }
        return 0
    }
