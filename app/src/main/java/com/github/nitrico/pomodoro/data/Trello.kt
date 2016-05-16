package com.github.nitrico.pomodoro.data

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.webkit.WebView
import android.webkit.WebViewClient
import com.github.nitrico.pomodoro.BuildConfig
import com.github.nitrico.pomodoro.R
import kotlinx.android.synthetic.main.activity_login.*
import oauth.signpost.basic.DefaultOAuthProvider
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.jetbrains.anko.async
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer
import se.akerfeldt.okhttp.signpost.SigningInterceptor

object Trello {


    ///// SESSION LISTENER /////

    interface SessionListener {
        // empty implementations provided to override only desired method
        fun onLogIn() { }
        fun onLogOut() { }
    }

    private val sessionListeners = mutableListOf<SessionListener>()

    private fun dispatchLogIn() = sessionListeners.forEach {
        if ((it !is Activity || !it.isFinishing) && (it !is Fragment || !it.isAdded)) it.onLogIn()
    }
    private fun dispatchLogOut() = sessionListeners.forEach {
        if ((it !is Activity || !it.isFinishing) && (it !is Fragment || !it.isAdded)) it.onLogOut()
    }
    fun addSessionListener(listener: SessionListener) {
        if (!sessionListeners.contains(listener)) sessionListeners.add(listener)
    }
    fun removeSessionListener(listener: SessionListener) = sessionListeners.remove(listener)


    ///// DATA LISTENER /////

    interface DataListener {
        fun onDataChanged()
    }

    private val dataListeners = mutableListOf<DataListener>()

    private fun dispatchDataChange() = dataListeners.forEach {
        if ((it !is Activity || !it.isFinishing) && (it !is Fragment || !it.isAdded)) it.onDataChanged()
    }
    fun addDataListener(listener: DataListener) {
        if (!dataListeners.contains(listener)) dataListeners.add(listener)
    }
    fun removeDataListener(listener: DataListener) = dataListeners.remove(listener)



    private const val CALLBACK_URL = "com.github.nitrico.pomodoro://trello-callback"
    private const val KEY_LOGIN_URL = "KEY_LOGIN_URL"
    private const val KEY_SECRET = "KEY_SECRET"
    private const val KEY_TOKEN = "KEY_TOKEN"

    private val consumer = OkHttpOAuthConsumer(BuildConfig.TRELLO_KEY, BuildConfig.TRELLO_SECRET)
    private val provider = DefaultOAuthProvider(
            "https://trello.com/1/OAuthGetRequestToken",
            "https://trello.com/1/OAuthGetAccessToken",
            "https://trello.com/1/OAuthAuthorizeToken?name=Pomodoro&scope=read,write,account")

    private val api = Retrofit.Builder()
            .baseUrl("https://api.trello.com/")
            .client(OkHttpClient.Builder()
                    .addInterceptor(SigningInterceptor(consumer))
                    .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE))
                    .build())
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build()
            .create(TrelloApi::class.java)

    private lateinit var context: Context
    private val preferences by lazy { PreferenceManager.getDefaultSharedPreferences(context) }

    private var secret: String? = null

    var token: String? = null
        private set
    var logged = false
        private set
    var user: TrelloMember? = null
        private set
    var boards: List<TrelloBoard> = emptyList()
        private set
    var lists: List<TrelloList> = emptyList()
        private set
    var todoList: TrelloList? = null
        private set
    var doingList: TrelloList? = null
        private set
    var doneList: TrelloList? = null
        private set
    var todoCards: List<TrelloCard> = emptyList()
        private set
    var doingCards: List<TrelloCard> = emptyList()
        private set
    var doneCards: List<TrelloCard> = emptyList()
        private set
    var boardId: String? = "5733f00584deae6ac11ac64b" // 5733f00584deae6ac11ac64b
    var todoListId: String? = "5733f00584deae6ac11ac64c"
    var doingListId: String? = "5733f00584deae6ac11ac64d"
    var doneListId: String? = "5733f00584deae6ac11ac64e"
    var listIds = listOf(todoListId, doingListId, doneListId)
        private set

    val boardNames: List<String>
        get() {
            val list = mutableListOf<String>()
            boards.forEach { list.add(it.name) }
            return list
        }
    val boardListNames: List<String>
        get() {
            val list = mutableListOf<String>()
            lists.forEach { list.add(it.name) }
            return list
        }

    fun init(context: Context) {
        this.context = context
        token = preferences.getString(KEY_TOKEN, null)
        secret = preferences.getString(KEY_SECRET, null)

        if (token != null && secret != null) async() {
            consumer.setTokenWithSecret(token, secret)
            uiThread { finishLogIn() }
        }
    }

    fun logIn(context: Context) {
        this.context = context
        async() {
            val url = Trello.provider.retrieveRequestToken(Trello.consumer, Trello.CALLBACK_URL)
            uiThread { context.startActivity<LoginActivity>(Trello.KEY_LOGIN_URL to url) }
        }
    }

    private fun finishLogIn() {
        token = consumer.token
        secret = consumer.tokenSecret
        preferences.edit()
                .putString(KEY_TOKEN, token)
                .putString(KEY_SECRET, secret)
                .commit()
        logged = true

        Observable.zip(api.getUser(), api.getBoards(),
                { user, boards -> Pair<TrelloMember, List<TrelloBoard>>(user, boards) })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    user = it.first
                    boards = it.second
                    lists = boards[0].lists!!
                    dispatchLogIn() // notify listeners
                },{
                    context.toast(it.message ?: "Unknown error")
                })
    }

    fun logOut() {
        logged = false
        user = null
        token = null
        secret = null
        todoCards = emptyList()
        doingCards = emptyList()
        doneCards = emptyList()
        preferences.edit()
                .putString(KEY_TOKEN, null)
                .putString(KEY_SECRET, null)
                .commit()

        dispatchLogOut() // notify listeners
    }

    fun addTodoCard(name: String, desc: String?, callback: (() -> Unit) ? = null) {
        // CHECK ALL REQUERIMENTS
        // CHECK logged
        // CHECK todoListId != null
        api.addCardToList(todoListId!!, name, desc)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    dispatchDataChange()
                    callback?.invoke()
                },{
                    context.toast(it.message ?: "Unknown error when adding a to do")
                })
    }

    fun moveCardToList(cardId: String, listId: String, callback: (() -> Unit) ? = null) {
        // CHECKS
        Trello.api.moveCardToList(cardId, listId) // ESTE NO ESTÁ PROBADO
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    context.toast("card moved to Done list")
                    callback?.invoke()
                },{
                    context.toast(it.message.toString())
                })
    }

    fun getListCards(listId: String?, callback: ((List<TrelloCard>) -> Unit) ? = null) {
        // CHECKS
        if (!logged || token == null || listId == null) return
        api.getListCards(listId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    when (listId) {
                        todoListId -> todoCards = it
                        doingListId -> doingCards = it
                        doneListId -> doneCards = it
                    }
                    callback?.invoke(it)
                },{
                    context.toast(it.message ?: "Unknown error getting list cards")
                })
    }

    /*
    fun getTodoCards(callback: ((List<TrelloCard>) -> Unit)?) = getListCards(todoListId, callback)
    fun getDoingCards(callback: ((List<TrelloCard>) -> Unit)?) = getListCards(doingListId, callback)
    fun getDoneCards(callback: ((List<TrelloCard>) -> Unit)?) = getListCards(doneListId, callback)
    */

    fun updateCard(cardId: String, callback: (() -> Unit) ? = null) { }
    fun removeCard(cardId: String, callback: (() -> Unit) ? = null) { }



    /**
     * Activity consisting on a WebView used to perform the user login
     */
    class LoginActivity : AppCompatActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_login)
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            // JavaScript is needed to handle buttons clicks on Trello login page
            webview.settings.javaScriptEnabled = true

            // custom WebViewClient to handle callback url and its params
            webview.setWebViewClient(object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    if (url.startsWith(CALLBACK_URL)) {
                        val verifier = Uri.parse(url).getQueryParameter("oauth_verifier")
                        async() {
                            provider.retrieveAccessToken(consumer, verifier)
                            uiThread { finishLogIn() }
                        }
                        finish()
                        return true
                    }
                    else return super.shouldOverrideUrlLoading(view, url)
                }
            })

            // open Trello login url
            val url = intent.getStringExtra(KEY_LOGIN_URL)
            webview.loadUrl(url)
        }

        override fun onBackPressed() {
            if (!logged) logOut()
            super.onBackPressed()
        }

        override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
            android.R.id.home -> { if (!logged) logOut(); finish(); true }
            else -> super.onOptionsItemSelected(item)
        }

    }

}
