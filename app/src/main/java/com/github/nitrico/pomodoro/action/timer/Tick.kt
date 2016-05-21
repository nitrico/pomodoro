package com.github.nitrico.pomodoro.action.timer

import com.github.nitrico.flux.action.Action

class Tick(val total: Long, val left: Long) : Action() {

    init {
        postAction()
    }

}
