package com.github.nitrico.pomodoro.tool

import android.content.Context
import android.preference.PreferenceManager
import com.github.nitrico.pomodoro.App

/**
 * Singleton object used to save the pomodoros and total time expended for each card
 */
object Cache {

    private class CardData(
            val id: String,
            var pomodoros: Int,
            var seconds: Long)


    private const val KEY_JSON = "JSON"
    private lateinit var context: Context
    private val preferences by lazy { PreferenceManager.getDefaultSharedPreferences(context) }
    private var data = mutableListOf<CardData>()

    /**
     * Initialize the Cache object
     * This method must be called before any other of this object
     */
    fun init(context: Context) {
        this.context = context
        load()
    }

    /**
     * Increment the time expended on a card
     * It also increment the pomodoros count if the time to add is a Pomodoro
     */
    fun addTime(cardId: String, secondsToAdd: Long) {
        val pomodoro = secondsToAdd == App.TIME_POMODORO
        val done = doIfContained(cardId) {
            seconds += secondsToAdd
            if (pomodoro) pomodoros++
        }
        if (!done) data.add(CardData(cardId, if (pomodoro) 1 else 0, secondsToAdd))
        save()
    }

    /**
     * Return the pomodoros amount expended for the card whose id is passed as an argument
     */
    fun getPomodoros(cardId: String): Int {
        data.forEach { if (it.id.equals(cardId)) return it.pomodoros }
        return 0
    }

    /**
     * Return the time in seconds expended for the card whose id is passes as an argument
     */
    fun getTimeInSeconds(cardId: String): Long {
        data.forEach { if (it.id.equals(cardId)) return it.seconds }
        return 0
    }

    /**
     * Save the data as a JSON string in SharedPreferences
     */
    fun save() {
        val json = Serializer.toJson(data.toTypedArray())
        preferences.edit().putString(KEY_JSON, json).commit()
    }

    /**
     * Load the data saved in SharedPreferences
     */
    private fun load() {
        val json = preferences.getString(KEY_JSON, "[]")
        data = Serializer.fromJson(json, Array<CardData>::class.java).toMutableList()
    }

    /**
     * Execute action if the card is contained in the saved data
     * @return true if the action was done (the card was on the data) or false otherwise
     */
    private fun doIfContained(cardId: String, action: CardData.() -> Unit): Boolean {
        data.forEach {
            if (it.id.equals(cardId)) {
                it.action()
                return true
            }
        }
        return false
    }

}
