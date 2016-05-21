package com.github.nitrico.pomodoro.action.timer

import com.github.nitrico.flux.action.Action
import com.github.nitrico.pomodoro.data.TrelloCard

class Finish(
        val card: TrelloCard,
        val timeToAdd: Long) : Action() {

    init {
        postAction()
    }

}
