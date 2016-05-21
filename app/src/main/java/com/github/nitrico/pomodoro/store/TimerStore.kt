package com.github.nitrico.pomodoro.store

import android.content.Context
import android.preference.PreferenceManager
import com.github.nitrico.flux.action.Action
import com.github.nitrico.flux.store.Store
import com.github.nitrico.pomodoro.action.timer.*

object TimerStore : Store() {

    private lateinit var context: Context
    private val preferences by lazy { PreferenceManager.getDefaultSharedPreferences(context) }

    var running: Boolean = false; private set
    var paused: Boolean = false; private set

    var isBreak: Boolean = false; private set
    var current: Long = 0; private set
    var total: Long = 0; private set
    var left: Long = 0; private set

    fun init(context: Context) {
        this.context = context
    }

    override fun onAction(action: Action) = when (action) {
        is StartPomodoro -> onStartPomodoro(action)
        is Tick -> onTick(action)
        is Stop -> onStop(action)
        is Pause -> onPause(action)
        is Resume -> onResume(action)
        is Reset -> onReset(action)
        else -> { }
    }

    private fun onStartPomodoro(action: StartPomodoro) {
        isBreak = false
        running = true
        postChange(action)
    }

    private fun onTick(action: Tick) {
        current++
        total = action.total
        left = action.left
        println("millis until finish: " +action.left)
        postChange(action)
    }

    private fun onStop(action: Stop) {
        // guardar los datos de current?
        current = 0
        total = 0
        left = 0
        postChange(action)
    }

    private fun onPause(action: Pause) {
        running = false
        paused = true
        postChange(action)
    }

    private fun onResume(action: Resume) {
        running = true
        postChange(action)
    }

    private fun onReset(action: Reset) {
        postChange(action)
    }

}
