package com.github.nitrico.pomodoro.tool

import android.app.Service
import android.content.Intent
import com.github.nitrico.pomodoro.action.timer.*
import com.github.nitrico.pomodoro.data.TrelloCard
import org.jetbrains.anko.toast

class TimerService : Service() {

    companion object {
        const val KEY_CARD = "KEY_CARD"
        const val KEY_TIME = "KEY_TIME"
        const val KEY_ACTION = "KEY_ACTION"
        const val ACTION_START = 0
        const val ACTION_STOP = 1
        const val ACTION_PAUSE = 2
        const val ACTION_RESUME = 3
        const val ACTION_RESET = 4 // ??
    }

    private lateinit var timer: Timer
    private lateinit var card: TrelloCard
    private var time: Long = 0

    override fun onBind(intent: Intent?) = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) {
            stopSelf()
            return super.onStartCommand(intent, flags, startId)
        }
        when (intent.extras.getInt(KEY_ACTION)) {
            ACTION_START  -> start(intent)
            ACTION_STOP   -> stop()
            ACTION_PAUSE  -> pause()
            ACTION_RESUME -> resume()
            ACTION_RESET  -> reset()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start(intent: Intent) {
        card = intent.extras.getSerializable(KEY_CARD) as TrelloCard
        time = intent.extras.getLong(KEY_TIME)

        timer = object : Timer(time * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                Tick(time, millisUntilFinished)
            }
            override fun onFinish() {
                toast("onFinish")
            }
        }
        timer.start()
        toast("Start")
    }

    private fun stop() {
        toast("Stop")
    }

    private fun pause() {
        timer.pause()
        toast("Pause")
    }

    private fun resume() {
        timer.resume()
        toast("Resume")
    }

    private fun reset() {
        Reset()
        toast("Reset")
    }

}
