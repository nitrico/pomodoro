package com.github.nitrico.pomodoro.trello

import com.github.nitrico.pomodoro.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Query
import rx.Observable

interface TrelloApi {

    companion object {
        const val VERSION = "1"
    }

    @GET(VERSION + "/members/me")
    fun getUser(
            @Query("token") token: String,
            @Query("key") key: String = BuildConfig.TRELLO_KEY
    ): Observable<TrelloMember>

    @GET(VERSION + "/members/me/boards")
    fun getBoards(
            @Query("token") token: String,
            @Query("key") key: String = BuildConfig.TRELLO_KEY,
            @Query("lists") lists: String = "open"
    ): Observable<List<TrelloBoard>>

}
