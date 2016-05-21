package com.github.nitrico.pomodoro.data

import com.github.nitrico.pomodoro.BuildConfig
import oauth.signpost.basic.DefaultOAuthProvider
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import rx.Observable
import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer
import se.akerfeldt.okhttp.signpost.SigningInterceptor

interface Trello {

    companion object {
        const val VERSION = "1"
        const val CALLBACK_URL = "com.github.nitrico.pomodoro://trello-callback"

        internal val consumer = OkHttpOAuthConsumer(BuildConfig.TRELLO_KEY, BuildConfig.TRELLO_SECRET)
        internal val provider = DefaultOAuthProvider(
                "https://trello.com/1/OAuthGetRequestToken",
                "https://trello.com/1/OAuthGetAccessToken",
                "https://trello.com/1/OAuthAuthorizeToken?name=Pomodoro&scope=read,write,account")

        internal val api = Retrofit.Builder()
                .baseUrl("https://api.trello.com/")
                .client(OkHttpClient.Builder()
                        .addInterceptor(SigningInterceptor(consumer))
                        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE))
                        .build())
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
                .create(Trello::class.java)
    }

    @GET("$VERSION/members/me")
    fun getUser(): Observable<TrelloMember>

    @GET("$VERSION/members/me/boards")
    fun getBoards(
            @Query("lists") lists: String = "open",
            @Query("cards") cards: String = "all"
    ): Observable<List<TrelloBoard>>

    @GET("$VERSION/lists/{id}/cards")
    fun getListCards(
            @Path("id") listId: String,
            @Query("cards") cards: String = "all"
    ): Observable<List<TrelloCard>>

    @POST("$VERSION/lists/{listId}/cards")
    fun addCardToList(
            @Path("listId") listId: String,
            @Query("name") cardName: String,
            @Query("desc") cardDesc: String?
    ): Observable<Any>

    @POST("$VERSION/cards/{cardId}/actions/comments")
    fun addCommentToCard(
            @Path("cardId") cardId: String,
            @Query("text") text: String
    ): Observable<Any>

    @PUT("$VERSION/cards/{cardId}")
    fun moveCardToList(
            @Path("cardId") cardId: String,
            @Query("idList") idList: String
    ): Observable<Any>

    @PUT("$VERSION/cards/{cardId}/name")
    fun updateCardName(
            @Path("cardId") cardId: String,
            @Query("value") cardDescription: String
    ): Observable<Any>

    @PUT("$VERSION/cards/{cardId}/desc")
    fun updateCardDescription(
        @Path("cardId") cardId: String,
        @Query("value") cardDescription: String
    ): Observable<Any>

    @DELETE("$VERSION/cards/{cardId}")
    fun deleteCard(
            @Path("cardId") cardId: String
    ): Observable<Any>

}
