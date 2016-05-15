package com.github.nitrico.pomodoro.data

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
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
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer
import se.akerfeldt.okhttp.signpost.SigningInterceptor

object Trello {

    interface SessionListener {
        fun onLogIn()
        fun onLogOut()
    }

    const val URL_BASE = "https://api.trello.com/"
    const val URL_REQUEST_TOKEN = "https://trello.com/1/OAuthGetRequestToken"
    const val URL_ACCESS_TOKEN = "https://trello.com/1/OAuthGetAccessToken"
    const val URL_AUTHORIZE = "https://trello.com/1/OAuthAuthorizeToken?name=Pomodoro&scope=read,write,account"
    private const val URL_CALLBACK = "com.github.nitrico.pomodoro://trello-callback"
    private const val KEY_LOGIN_URL = "KEY_LOGIN_URL"
    private const val KEY_TOKEN = "KEY_TOKEN"
    private const val KEY_TOKEN_SECRET = "KEY_TOKEN_SECRET"

    private lateinit var context: Context
    private val preferences by lazy { PreferenceManager.getDefaultSharedPreferences(context) }

    private val consumer = OkHttpOAuthConsumer(BuildConfig.TRELLO_KEY, BuildConfig.TRELLO_SECRET)
    private val provider = DefaultOAuthProvider(URL_REQUEST_TOKEN, URL_ACCESS_TOKEN, URL_AUTHORIZE)

    internal val api = Retrofit.Builder()
            .baseUrl(URL_BASE)
            .client(OkHttpClient.Builder()
                    .addInterceptor(SigningInterceptor(consumer))
                    .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE))
                    .build())
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build()
            .create(TrelloApi::class.java)

    internal var tokenSecret: String? = null
        private set
    internal var token: String? = null
        private set
    var logged = false
        private set
    var user: TrelloMember? = null
        private set
    /*
    var boards: List<TrelloBoard> = emptyList()
        private set
    var lists: List<TrelloList> = emptyList()
        private set
    var todoList: List<TrelloCard> = emptyList()
        private set
    var doingList: List<TrelloCard> = emptyList()
        private set
    var doneList: List<TrelloCard> = emptyList()
        private set
    */

    var boardId: String? = "5733f00584deae6ac11ac64b"
    var todoListId: String? = "5733f00584deae6ac11ac64c"
    var doingListId: String? = "5733f00584deae6ac11ac64d"
    var doneListId: String? = "5733f00584deae6ac11ac64e"
    val listIds = listOf(todoListId, doingListId, doneListId)

    private val listeners = mutableListOf<SessionListener>()

    fun addSessionListener(listener: SessionListener) {
        if (!listeners.contains(listener)) listeners.add(listener)
    }
    fun removeSessionListener(listener: SessionListener) = listeners.remove(listener)

    private fun dispatchLogIn() = listeners.forEach { it.onLogIn() }
    private fun dispatchLogOut() = listeners.forEach { it.onLogOut() }

    fun init(context: Context) {
        this.context = context
        token = preferences.getString(KEY_TOKEN, null)
        tokenSecret = preferences.getString(KEY_TOKEN_SECRET, null)

        if (token != null && tokenSecret != null) async() {
            consumer.setTokenWithSecret(token, tokenSecret)
            uiThread { finishLogIn() }
        }
        else async() {
            val url = provider.retrieveRequestToken(consumer, URL_CALLBACK)
            uiThread { context.startActivity<LoginActivity>(KEY_LOGIN_URL to url) }
        }
    }

    private fun finishLogIn() {
        token = consumer.token
        tokenSecret = consumer.tokenSecret
        preferences.edit()
                .putString(KEY_TOKEN, token)
                .putString(KEY_TOKEN_SECRET, tokenSecret)
                .commit()
        logged = true

        api.getUser(token!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    user = it
                    if (user != null) {
                        //context.toast(user!!.username + " logged in")
                        println(user!!.email +" " +user!!.fullName)
                    }
                    dispatchLogIn() // notify listeners
                },{
                    context.toast(it.message ?: "Unknown error")
                })
    }

    fun logOut() {
        logged = false
        user = null
        token = null
        tokenSecret = null
        preferences.edit()
                .putString(KEY_TOKEN, null)
                .putString(KEY_TOKEN_SECRET, null)
                .commit()

        dispatchLogOut() // notify listeners
    }


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
                    if (url.startsWith(URL_CALLBACK)) {
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
            logOut()
            super.onBackPressed()
        }

        override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
            android.R.id.home -> {
                logOut()
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

}
