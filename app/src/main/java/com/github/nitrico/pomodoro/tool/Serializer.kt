package com.github.nitrico.pomodoro.tool

import com.squareup.moshi.Moshi

interface SerializerInterface {
    fun <T> fromJson(json: String, clazz: Class<T>): T
    fun toJson(obj: Any): String
}

/**
 * SerializerInterface implementation using Moshi
 */
object Serializer : SerializerInterface {
    private val moshi = Moshi.Builder().build()
    override fun <T> fromJson(json: String, clazz: Class<T>): T = moshi.adapter(clazz).fromJson(json)
    override fun toJson(obj: Any): String = moshi.adapter(obj.javaClass).toJson(obj)
}

/*
/**
 * SerializerInterface implementation using Gson
 */
class Serializer : SerializerInterface {
    private val gson = Gson()
    override fun <T> fromJson(json: String, clazz: Class<T>): T = gson.fromJson(json, clazz)
    override fun toJson(obj: Any): String = gson.toJson(obj)
}
*/
