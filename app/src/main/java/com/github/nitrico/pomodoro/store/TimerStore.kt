package com.github.nitrico.pomodoro.store

import com.github.nitrico.flux.action.Action
import com.github.nitrico.flux.store.Store
import com.github.nitrico.pomodoro.App
import com.github.nitrico.pomodoro.action.timer.*
import com.github.nitrico.pomodoro.tool.Cache

/**
 * Singleton object used to keep the current timer status
 */
object TimerStore : Store() {

    var isBreak: Boolean = false; private set
    var isPaused: Boolean = false; private set
    var isRunning: Boolean = false; private set

    var current: Long = 0; private set
    var total: Long = 0; private set

    override fun onAction(action: Action) {
        when (action) {
            is Tick -> @Synchronized {
                current++
            }
            is Start -> @Synchronized {
                total = action.time
                isBreak = action.time != App.TIME_POMODORO
                isRunning = true
            }
            is Pause -> @Synchronized {
                isRunning = false
                isPaused = true
            }
            is Resume -> @Synchronized {
                isRunning = true
            }
            is Stop -> @Synchronized {
                reset(action.card.id, current)
            }
            is Finish -> @Synchronized {
                reset(action.card.id, action.timeToAdd)
            }
            else -> return
        }
        postChange(action)
    }

    private fun reset(cardId: String, time: Long) {
        if (!isBreak) Cache.addTime(cardId, time)
        isBreak = false
        isRunning = false
        isPaused = false
        current = 0
    }

}
