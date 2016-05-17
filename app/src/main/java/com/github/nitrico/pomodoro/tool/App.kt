package com.github.nitrico.pomodoro.tool

import android.app.Application
import android.graphics.Typeface
import com.github.nitrico.pomodoro.tool.Cache

class App : Application() {

    companion object {
        lateinit var mocharyTypeface: Typeface
    }

    override fun onCreate() {
        super.onCreate()
        mocharyTypeface = Typeface.createFromAsset(assets, "fonts/Mochary.ttf")
        Cache.init(this)
    }

}
