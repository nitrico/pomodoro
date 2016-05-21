package com.github.nitrico.pomodoro.action.timer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.github.nitrico.flux.action.Action
import com.github.nitrico.pomodoro.data.TrelloCard
import com.github.nitrico.pomodoro.tool.TimerService

class Start(
        private val context: Context,
        private val card: TrelloCard,
        val time: Long) : Action() {

    init {
        val intent = Intent(context, TimerService::class.java)
        context.stopService(intent)
        val bundle = Bundle()
        bundle.putInt(TimerService.KEY_ACTION, TimerService.ACTION_START)
        bundle.putLong(TimerService.KEY_TIME, time)
        bundle.putSerializable(TimerService.KEY_CARD, card)
        intent.putExtras(bundle)
        context.startService(intent)
        postAction()
    }

}
