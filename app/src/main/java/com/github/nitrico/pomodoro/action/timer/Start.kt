package com.github.nitrico.pomodoro.action.timer

import com.github.nitrico.flux.action.Action

class Start(
        /*private val context: Context,
        val card: TrelloCard,*/
        val isBreak: Boolean) : Action() {

    init {
        /*
        val intent = Intent(context, TimerService::class.java)
        context.stopService(intent)
        val bundle = Bundle()
        bundle.putInt(TimerService.KEY_ACTION, TimerService.ACTION_START)
        bundle.putLong(TimerService.KEY_TIME, App.TIME_POMODORO)
        bundle.putSerializable(TimerService.KEY_CARD, card)
        intent.putExtras(bundle)
        context.startService(intent)
        */
        postAction()
    }

}
