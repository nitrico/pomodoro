package com.github.nitrico.pomodoro.tool

import android.app.Service
import android.content.Intent
import com.github.nitrico.pomodoro.App
import com.github.nitrico.pomodoro.action.timer.*
import com.github.nitrico.pomodoro.data.TrelloCard

class TimerService : Service() {

    companion object {
        const val KEY_CARD = "KEY_CARD"
        const val KEY_TIME = "KEY_TIME"
        const val KEY_ACTION = "KEY_ACTION"
        const val ACTION_START = 0
        const val ACTION_STOP = 1
        const val ACTION_PAUSE = 2
        const val ACTION_RESUME = 3
    }

    private lateinit var card: TrelloCard
    private var timer: Timer? = null
    private var time: Long = 0
    private var isBreak: Boolean = false

    override fun onBind(intent: Intent?) = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) {
            stopSelf()
            return super.onStartCommand(intent, flags, startId)
        }
        when (intent.extras.getInt(KEY_ACTION)) {
            ACTION_START  -> start(intent)
            ACTION_PAUSE  -> timer?.pause()
            ACTION_RESUME -> timer?.resume()
            ACTION_STOP   -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start(intent: Intent) {
        card = intent.extras.getSerializable(KEY_CARD) as TrelloCard
        time = intent.extras.getLong(KEY_TIME)
        if (time != App.TIME_POMODORO) isBreak = true

        timer = object : Timer(time * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                Tick(time, millisUntilFinished)
            }
            override fun onFinish() {
                val timeToAdd = if (isBreak) 0 else time
                Finish(card, timeToAdd)
                stopSelf()
            }
        }
        timer?.start()
    }

    private fun stop() {
        timer?.cancel()
        stopSelf()
    }

}
