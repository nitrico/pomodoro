package com.github.nitrico.pomodoro.tool

import android.app.Service
import android.content.Intent
import android.os.IBinder
import org.jetbrains.anko.toast

class TimerService : Service() {

    override fun onBind(intent: Intent): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        //toast("TimerService onCreate")
    }

    override fun onDestroy() {
        super.onDestroy()
        //toast("TimerService onDestroy")
    }

}
