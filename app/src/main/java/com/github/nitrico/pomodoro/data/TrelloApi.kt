package com.github.nitrico.pomodoro.data

import com.github.nitrico.pomodoro.BuildConfig
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import rx.Observable

interface TrelloApi {

    companion object {
        const val VERSION = "1"
    }

    @GET(VERSION + "/members/me")
    fun getUser(
            @Query("token") token: String = Trello.token!!,
            @Query("key") key: String = BuildConfig.TRELLO_KEY
    ): Observable<TrelloMember>

    @GET(VERSION + "/members/me/boards")
    fun getBoards(
            @Query("token") token: String = Trello.token!!,
            @Query("key") key: String = BuildConfig.TRELLO_KEY,
            @Query("lists") lists: String = "open"
            //@Query("cards") cards: String = "all"
    ): Observable<List<TrelloBoard>>

    @GET(VERSION + "/boards/{id}/lists")
    fun getBoardLists(
            @Path("id") boardId: String,
            @Query("token") token: String = Trello.token!!,
            @Query("key") key: String = BuildConfig.TRELLO_KEY,
            @Query("cards") cards: String = "all"
    ): Observable<List<TrelloList>>

    @GET(VERSION + "/lists/{id}/cards")
    fun getListCards(
            @Path("id") listId: String,
            @Query("token") token: String = Trello.token!!,
            @Query("key") key: String = BuildConfig.TRELLO_KEY,
            @Query("cards") cards: String = "all"
    ): Observable<List<TrelloCard>>

    @PUT(VERSION + "/cards/{cardId}/{listId}")
    fun moveCardToList(
            @Path("cardId") cardId: String,
            @Path("listId") listId: String,
            @Query("token") token: String = Trello.token!!,
            @Query("key") key: String = BuildConfig.TRELLO_KEY
    ): Observable<Void> // ??? invención mía poco fiable
    // http://stackoverflow.com/questions/33228126/how-can-i-handle-empty-response-body-with-retrofit-2

}
