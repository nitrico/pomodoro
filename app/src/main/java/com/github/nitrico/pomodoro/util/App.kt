package com.github.nitrico.pomodoro.util

import android.app.Application

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        TypefaceCache.init(this)
    }

}
