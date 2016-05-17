package com.github.nitrico.pomodoro.data

import retrofit2.http.*
import rx.Observable

interface TrelloApi {

    companion object {
        const val VERSION = "1"
    }

    @GET(VERSION + "/members/me")
    fun getUser(): Observable<TrelloMember>

    @GET(VERSION + "/members/me/boards")
    fun getBoards(
            @Query("lists") lists: String = "open",
            @Query("cards") cards: String = "all"
    ): Observable<List<TrelloBoard>>

    @GET(VERSION + "/lists/{id}/cards")
    fun getListCards(
            @Path("id") listId: String,
            @Query("cards") cards: String = "all"
    ): Observable<List<TrelloCard>>

    @POST(VERSION + "/lists/{listId}/cards")
    fun addCardToList(
            @Path("listId") listId: String,
            @Query("name") cardName: String,
            @Query("desc") cardDesc: String?
    ): Observable<Any>

    @POST(VERSION + "/cards/{cardId}/actions/comments")
    fun addCommentToCard(
            @Path("cardId") cardId: String,
            @Query("text") text: String
    ): Observable<Any>

    @PUT(VERSION + "/cards/{cardId}")
    fun moveCardToList(
            @Path("cardId") cardId: String,
            @Query("idList") idList: String
    ): Observable<Any>

    @PUT(VERSION + "/cards/{cardId}/name")
    fun updateCardName(
            @Path("cardId") cardId: String,
            @Query("value") cardDescription: String
    ): Observable<Any>

    @PUT(VERSION + "/cards/{cardId}/desc")
    fun updateCardDescription(
        @Path("cardId") cardId: String,
        @Query("value") cardDescription: String
    ): Observable<Any>

    @DELETE(VERSION + "/cards/{cardId}")
    fun deleteCard(
            @Path("cardId") cardId: String
    ): Observable<Any>

}
