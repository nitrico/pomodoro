package com.github.nitrico.pomodoro

import android.app.Application
import android.graphics.Typeface
import com.github.nitrico.flux.Flux
import com.github.nitrico.pomodoro.tool.Cache

class App : Application() {

    companion object {
        const val TIME_POMODORO: Long = 25//*60
        const val TIME_LONG_BREAK: Long = 15//*60
        const val TIME_SHORT_BREAK: Long = 5//*60

        lateinit var mocharyTypeface: Typeface
    }

    override fun onCreate() {
        super.onCreate()
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO)
        mocharyTypeface = Typeface.createFromAsset(assets, "fonts/Mochary.ttf")

        Flux.init(this)
        Cache.init(this)
    }

}
