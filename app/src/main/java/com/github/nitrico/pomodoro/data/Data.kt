package com.github.nitrico.pomodoro.data

import android.content.Context
import android.preference.PreferenceManager
import com.github.nitrico.pomodoro.tool.Serializer
import java.io.Serializable

object Data {

    private const val JSON = "JSON"

    class CardTime : Serializable {
        val id: String
        var pomodoros: Int
        var seconds: Long
        constructor(id: String, pomodoros: Int, seconds: Long) {
            this.id = id
            this.pomodoros = pomodoros
            this.seconds = seconds
        }
    }

    private var list = mutableListOf<CardTime>()
    private lateinit var context: Context
    private val preferences by lazy { PreferenceManager.getDefaultSharedPreferences(context) }

    fun init(context: Context) {
        this.context = context
        load()
    }

    fun addPomodoro(cardId: String) {
        val done = doIfContained(cardId) {
            pomodoros++
            seconds += 1500
        }
        if (!done) list.add(CardTime(cardId, 1, 1500))
        save()
    }

    fun addTime(cardId: String, secondsToAdd: Long) {
        val done = doIfContained(cardId) {
            seconds += secondsToAdd
        }
        if (!done) list.add(CardTime(cardId, 0, secondsToAdd))
        save()
    }

    fun getPomodoros(cardId: String): Int {
        list.forEach { if (it.id.equals(cardId)) return it.pomodoros }
        return 0
    }

    fun getSeconds(cardId: String): Long {
        list.forEach { if (it.id.equals(cardId)) return it.seconds }
        return 0
    }

    fun save() {
        val json = Serializer.toJson(list.toTypedArray())
        preferences.edit().putString(JSON, json).apply()
        println("SAVED JSON: " + json)
    }

    private fun load() {
        val json = preferences.getString(JSON, "[]")
        list = Serializer.fromJson(json, Array<CardTime>::class.java).toMutableList()
        println("SAVED LIST: " + list)
    }

    private fun doIfContained(cardId: String, func: CardTime.() -> Unit): Boolean {
        list.forEach {
            if (it.id.equals(cardId)) {
                func(it)
                return true
            }
        }
        return false
    }

}
