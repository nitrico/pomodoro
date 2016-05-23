package com.github.nitrico.pomodoro.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import com.github.nitrico.flux.dispatcher.ViewDispatch

/**
 * Base Fragment to use the Flux architecture
 */
abstract class FluxFragment : Fragment(), ViewDispatch {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        register()
        getStores().forEach { it.register() }
    }

    override fun onPause() {
        super.onPause()
        getPauseableStores().forEach { it.unregister() }
    }

    override fun onResume() {
        super.onResume()
        getPauseableStores().forEach { it.register() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unregister()
        getStores().forEach { it.unregister() }
    }

}
