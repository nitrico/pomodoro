package com.github.nitrico.flux.action

import com.github.nitrico.flux.dispatcher.Dispatcher

open class Action {

    // estaria bien implementar un toString guapante

    protected fun postAction() = Dispatcher.post(this)
    protected fun postError(throwable: Throwable) = Dispatcher.post(ErrorAction(this, throwable))

}
