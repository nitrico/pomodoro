package com.github.nitrico.pomodoro.store

import com.github.nitrico.flux.action.Action
import com.github.nitrico.flux.store.Store
import com.github.nitrico.pomodoro.App
import com.github.nitrico.pomodoro.action.timer.*
import com.github.nitrico.pomodoro.tool.Cache

object TimerStore : Store() {

    var running: Boolean = false; private set
    var paused: Boolean = false; private set

    var isBreak: Boolean = false; private set
    var current: Long = 0; private set
    var total: Long = 0; private set
    var left: Long = 0; private set


    override fun onAction(action: Action) = when (action) {
        is Start -> onStart(action)
        is Tick -> onTick(action)
        is Stop -> onStop(action)
        is Pause -> onPause(action)
        is Resume -> onResume(action)
        is Finish -> onFinish(action)
        else -> { }
    }

    private fun onStart(action: Start) {
        isBreak = action.time != App.TIME_POMODORO
        running = true
        postChange(action)
    }

    private fun onTick(action: Tick) {
        current++
        total = action.total
        left = action.left
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

    private fun onStop(action: Stop) {
        reset(action.card.id, current)
        postChange(action)
    }

    private fun onFinish(action: Finish) {
        reset(action.card.id, action.timeToAdd)
        postChange(action)
    }

    private fun reset(cardId: String, time: Long) {
        if (!isBreak) Cache.addTime(cardId, time)
        running = false
        paused = false
        current = 0
        total = 0
        left = 0
    }

}
