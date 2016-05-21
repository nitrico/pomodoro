package com.github.nitrico.pomodoro.action.timer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.github.nitrico.flux.action.Action
import com.github.nitrico.pomodoro.data.TrelloCard
import com.github.nitrico.pomodoro.tool.TimerService

class Stop(
        private val context: Context,
        val card: TrelloCard) : Action() {

    init {
        val intent = Intent(context, TimerService::class.java)
        val bundle = Bundle()
        bundle.putInt(TimerService.KEY_ACTION, TimerService.ACTION_STOP)
        intent.putExtras(bundle)
        context.startService(intent)
        postAction()
    }

}
