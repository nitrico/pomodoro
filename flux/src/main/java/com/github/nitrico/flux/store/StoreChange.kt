package com.github.nitrico.flux.store

import com.github.nitrico.flux.action.Action

class StoreChange(val store: Store, val action: Action) {

    override fun toString() = "${store.javaClass.simpleName} # $action"

}
