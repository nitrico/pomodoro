package com.github.nitrico.pomodoro.util

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.ArrayRes
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.github.nitrico.pomodoro.R


val Int.dp: Int // dip to px conversion
    get() = (this * Resources.getSystem().displayMetrics.density + 0.5).toInt()

val Context.isPortrait: Boolean
    get() = resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT

fun Activity.setFullScreenLayout() {
    window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
}



operator fun TabLayout.get(position: Int): TabLayout.Tab = getTabAt(position)!!

fun TabLayout.forEach(func: (TabLayout.Tab) -> Unit) {
    for (i in 0..tabCount-1) func(get(i))
}

fun TabLayout.setIcons(icons: List<Drawable>) {
    for (i in 0..tabCount-1) get(i).icon = icons[i]
    tint()
}

var Drawable.tint: Int
    @ColorInt get() = tint
    @ColorInt set(value) {
        if (Build.VERSION.SDK_INT >= 21) setTint(value)
        else DrawableCompat.setTint(DrawableCompat.wrap(this), value)
    }

fun TabLayout.tint(selectedPosition: Int = 0,
                   selectedColor: Int = context.colorRes(R.color.white),
                   @ColorRes defaultColor: Int = R.color.white_trans) {
    forEach { it.icon?.tint = context.colorRes(defaultColor) }
    get(selectedPosition).icon?.tint = selectedColor
}

@ColorInt fun Context.colorRes(@ColorRes id: Int): Int {
    if (Build.VERSION.SDK_INT >= 23) getColor(id)
    @Suppress("deprecation") return resources.getColor(id)
}


///// RESOURCES /////

fun Context.stringsFromArrayRes(@ArrayRes arrayResId: Int): Lazy<List<String>> = lazy {
    resources.getStringArray(arrayResId).toList()
}

fun Context.drawablesFromArrayRes(@ArrayRes id: Int): List<Drawable> {
    val array = resources.obtainTypedArray(id)
    val list = mutableListOf<Drawable>()
    for (i in 0..array.length()-1) list.add(array.getDrawable(i))
    array.recycle()
    return list
}


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

fun TextView.setTextOrHideView(string: String?) {
    if (string.isNullOrEmpty()) hide()
    else {
        show()
        text = string
    }
}

fun ImageView.load(url: String, circular: Boolean = false) = ImageLoader.load(this, url, circular)



///// DRAWER LAYOUT /////

val DrawerLayout.isOpen: Boolean get() = isDrawerOpen(GravityCompat.START)
fun DrawerLayout.open() = openDrawer(GravityCompat.START)
fun DrawerLayout.close() = closeDrawer(GravityCompat.START)
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
