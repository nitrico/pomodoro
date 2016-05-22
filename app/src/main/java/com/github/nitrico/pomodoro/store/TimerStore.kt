package com.github.nitrico.pomodoro.store

import com.github.nitrico.flux.action.Action
import com.github.nitrico.flux.store.Store
import com.github.nitrico.pomodoro.App
import com.github.nitrico.pomodoro.action.timer.*
import com.github.nitrico.pomodoro.tool.Cache

object TimerStore : Store() {

    var isBreak: Boolean = false; private set
    var running: Boolean = false; private set
    var paused: Boolean = false; private set

    var current: Long = 0; private set
    var total: Long = 0; private set
    var left: Long = 0; private set

    override fun onAction(action: Action) {
        when (action) {
            is Tick -> {
                current++
                //total = action.total
                //left = action.left
            }
            is Start -> {
                isBreak = action.time != App.TIME_POMODORO
                running = true
            }
            is Pause -> {
                running = false
                paused = true
            }
            is Resume -> {
                running = true
            }
            is Stop -> {
                reset(action.card.id, current)
            }
            is Finish -> {
                reset(action.card.id, action.timeToAdd)
            }
            else -> return
        }
        postChange(action)
    }

    private fun reset(cardId: String, time: Long) {
        if (!isBreak) Cache.addTime(cardId, time)
        running = false
        paused = false
        current = 0
    }

}
