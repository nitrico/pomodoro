package com.github.nitrico.pomodoro.util

import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout

fun consume(func: () -> Unit): Boolean {
    func()
    return true
}

inline fun DrawerLayout.consume(f: () -> Unit): Boolean {
    f()
    closeDrawer(GravityCompat.START)
    return true
}


/*
<FrameLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent" >
    <android.support.design.widget.FloatingActionButton
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_margin="16dp"
        android:layout_gravity="bottom|end"
        android:src="@drawable/ic_add" />
</FrameLayout>

class LoginActivity : BaseLoginActivity() {
    override val name = "Trello"
    override val color = "#026AA7"
    override val urlKey = KEY_LOGIN_URL
    override val javaScriptRequired = true
    override fun useUrl(url: String) {
        val verifier = Uri.parse(url).getQueryParameter("oauth_verifier")
        async() {
            provider.retrieveAccessToken(consumer, verifier)
            logIn()
        }
    }
}
*/
