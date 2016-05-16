package com.github.nitrico.pomodoro.data

import com.github.nitrico.pomodoro.BuildConfig
import retrofit2.http.*
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
            @Query("lists") lists: String = "open",
            @Query("cards") cards: String = "all"
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

    @POST(VERSION + "/lists/{listId}/cards")
    fun addCardToList(
            @Path("listId") listId: String,
            @Query("name") cardName: String,
            @Query("desc") cardDesc: String?,
            @Query("token") token: String = Trello.token!!,
            @Query("key") key: String = BuildConfig.TRELLO_KEY
    ): Observable<Any>

    @PUT(VERSION + "/cards/{cardId}")
    fun moveCardToList(
            @Path("cardId") cardId: String,
            @Query("idList") idList: String,
            @Query("token") token: String = Trello.token!!,
            @Query("key") key: String = BuildConfig.TRELLO_KEY
    ): Observable<Any>

}
