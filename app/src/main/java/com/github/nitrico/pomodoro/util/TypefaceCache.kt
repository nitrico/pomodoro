package com.github.nitrico.pomodoro.util

import android.content.Context
import android.graphics.Typeface

object TypefaceCache {

    private lateinit var context: Context

    fun init(context: Context) { this.context = context }

    val mochary by lazy { Typeface.createFromAsset(context.assets, "fonts/Mochary.ttf") }

}
