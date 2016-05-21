package com.github.nitrico.pomodoro.action.trello

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.webkit.WebView
import android.webkit.WebViewClient
import com.github.nitrico.flux.action.Action
import com.github.nitrico.pomodoro.R
import com.github.nitrico.pomodoro.data.Trello
import com.github.nitrico.pomodoro.tool.setTaskDescription
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.async
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.uiThread

class LogIn(private val context: Context) : Action() {

    companion object {
        private const val KEY_LOGIN_URL = "KEY_LOGIN_URL"
    }

    init {
        async() {
            val url = Trello.provider.retrieveRequestToken(Trello.consumer, Trello.CALLBACK_URL)
            uiThread { context.startActivity<LoginActivity>(KEY_LOGIN_URL to url) }
        }
    }

    class Success() : Action() {
        init {
            postAction()
        }
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
            setTaskDescription(R.drawable.ic_main)

            // JavaScript is needed to handle buttons clicks on Trello login page
            webview.settings.javaScriptEnabled = true

            // custom WebViewClient to handle callback url and its params
            webview.setWebViewClient(object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    if (url.startsWith(Trello.CALLBACK_URL)) {
                        val verifier = Uri.parse(url).getQueryParameter("oauth_verifier")
                        async() {
                            Trello.provider.retrieveAccessToken(Trello.consumer, verifier)
                            uiThread { Success() }
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

        override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
            android.R.id.home -> { finish(); true }
            else -> super.onOptionsItemSelected(item)
        }

    }

}
