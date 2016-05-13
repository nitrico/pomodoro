package com.github.nitrico.pomodoro

import android.app.Application
import android.graphics.Typeface

class App : Application() {

    companion object {
        lateinit var titleTypeface: Typeface
    }

    override fun onCreate() {
        super.onCreate()
        titleTypeface = Typeface.createFromAsset(assets, "fonts/Mochary.ttf")
    }

}
