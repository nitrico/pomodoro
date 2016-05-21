package com.github.nitrico.flux

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.github.nitrico.flux.dispatcher.Dispatcher
import com.github.nitrico.flux.dispatcher.ViewDispatch

object Flux : Application.ActivityLifecycleCallbacks {

    private var activityCounter = 0

    var log = false
        private set

    fun init(application: Application, log: Boolean = false) {
        this.log = log
        application.registerActivityLifecycleCallbacks(this)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        activityCounter++
        if (activity is ViewDispatch) {
            activity.getStores().forEach { it.register() }
        }
    }

    override fun onActivityDestroyed(activity: Activity) {
        activityCounter--
        if (activityCounter == 0) Dispatcher.unregisterAll()
    }

    override fun onActivityResumed(activity: Activity) {
        if (activity is ViewDispatch) {
            Dispatcher.registerView(activity)
            activity.getPauseableStores().forEach { it.register() }
        }
    }

    override fun onActivityPaused(activity: Activity) {
        if (activity is ViewDispatch) {
            activity.getPauseableStores().forEach { it.unregister() }
            Dispatcher.unregisterView(activity)
        }
    }

    override fun onActivityStarted(activity: Activity) { }

    override fun onActivityStopped(activity: Activity) { }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle?) {  }

}
