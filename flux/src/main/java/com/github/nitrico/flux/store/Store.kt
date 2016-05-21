package com.github.nitrico.flux.store

import com.github.nitrico.flux.action.Action
import com.github.nitrico.flux.dispatcher.Dispatcher
import com.github.nitrico.flux.dispatcher.StoreDispatch

abstract class Store : StoreDispatch {

    override fun toString() = javaClass.simpleName

    protected fun postChange(action: Action) = Dispatcher.post(StoreChange(this, action))

    fun register() = Dispatcher.registerStore(this)
    fun unregister() = Dispatcher.unregisterStore(this)

}
