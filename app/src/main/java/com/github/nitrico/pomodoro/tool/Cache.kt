package com.github.nitrico.pomodoro.tool

import android.content.Context
import android.preference.PreferenceManager

/**
 * Singleton object used to save the pomodoros and total time expended on a card
 */
object Cache {

    private class CardData(
            val id: String,
            var pomodoros: Int,
            var seconds: Long)


    private const val KEY_JSON = "JSON"

    private val preferences by lazy { PreferenceManager.getDefaultSharedPreferences(context) }
    private lateinit var context: Context
    private var list = mutableListOf<CardData>()

    /**
     * Initializes the Cache object
     * This method must be called before any other of this object
     */
    fun init(context: Context) {
        this.context = context
        load()
    }

    /**
     * Increments the pomodoro counter and adds 25 minutes to the total time
     * of the card whose id is provided as an argument
     */
    fun addPomodoro(cardId: String) {
        val done = doIfContained(cardId) {
            pomodoros++
            seconds += 1500
        }
        if (!done) list.add(CardData(cardId, 1, 1500))
        save()
    }

    fun addTime(cardId: String, secondsToAdd: Long) {
        val done = doIfContained(cardId) {
            seconds += secondsToAdd
        }
        if (!done) list.add(CardData(cardId, 0, secondsToAdd))
        save()
    }

    fun getPomodoros(cardId: String): Int {
        list.forEach { if (it.id.equals(cardId)) return it.pomodoros }
        return 0
    }

    fun getTimeInSeconds(cardId: String): Long {
        list.forEach { if (it.id.equals(cardId)) return it.seconds }
        return 0
    }

    /**
     * Saves the data as a JSON string in SharedPreferences
     */
    fun save() {
        val json = Serializer.toJson(list.toTypedArray())
        preferences.edit().putString(KEY_JSON, json).commit()
    }

    /**
     * Loads the data saved in SharedPreferences
     */
    private fun load() {
        val json = preferences.getString(KEY_JSON, "[]")
        list = Serializer.fromJson(json, Array<CardData>::class.java).toMutableList()
    }

    private fun doIfContained(cardId: String, func: CardData.() -> Unit): Boolean {
        list.forEach {
            if (it.id.equals(cardId)) {
                func(it)
                return true
            }
        }
        return false
    }

}
