package com.github.nitrico.flux.action

class ErrorAction(val action: Action, val throwable: Throwable) : Action()
