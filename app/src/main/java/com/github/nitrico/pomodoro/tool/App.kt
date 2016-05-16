package com.github.nitrico.pomodoro.tool

import android.app.Application
import android.graphics.Typeface

class App : Application() {

    companion object {
        lateinit var mocharyTypeface: Typeface
    }

    override fun onCreate() {
        super.onCreate()
        mocharyTypeface = Typeface.createFromAsset(assets, "fonts/Mochary.ttf")
    }

}
