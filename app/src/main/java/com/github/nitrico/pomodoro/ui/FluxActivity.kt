package com.github.nitrico.pomodoro.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.github.nitrico.flux.dispatcher.ViewDispatch

abstract class FluxActivity : AppCompatActivity(), ViewDispatch {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getStores().forEach { register() }
    }

    override fun onResume() {
        super.onResume()
        register()
        getPauseableStores().forEach { it.register() }
    }

    override fun onPause() {
        super.onPause()
        unregister()
        getPauseableStores().forEach { it.unregister() }
    }

}
