package com.github.nitrico.flux.dispatcher

import com.github.nitrico.flux.action.Action

interface StoreDispatch {

    fun onAction(action: Action)

}
