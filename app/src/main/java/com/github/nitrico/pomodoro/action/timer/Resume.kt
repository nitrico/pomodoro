package com.github.nitrico.pomodoro.action.timer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.github.nitrico.flux.action.Action
import com.github.nitrico.pomodoro.tool.TimerService

class Resume(private val context: Context) : Action() {

    init {
        val intent = Intent(context, TimerService::class.java)
        val bundle = Bundle()
        bundle.putInt(TimerService.KEY_ACTION, TimerService.ACTION_RESUME)
        intent.putExtras(bundle)
        context.startService(intent)
        postAction()
    }

}
