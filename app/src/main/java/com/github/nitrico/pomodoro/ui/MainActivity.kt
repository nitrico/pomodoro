package com.github.nitrico.pomodoro.ui

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import com.github.nitrico.pomodoro.App
import com.github.nitrico.pomodoro.R
import com.github.nitrico.pomodoro.data.Trello
import com.github.nitrico.pomodoro.util.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), Trello.SessionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        if (isPortrait) window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        abTitle.setText(R.string.app_name)
        abTitle.typeface = App.titleTypeface
        fab.setMargins(16.dp, 0, 16.dp, 16.dp + getNavigationBarHeight())
        setupTabs()

        Trello.addSessionListener(this)
        if (savedInstanceState == null) Trello.init(this)
    }

    private fun setupTabs() {
        pager.adapter = TabsAdapter(supportFragmentManager)
        pager.offscreenPageLimit = 2
        pager.pageMargin = 16.dp
        tabs.setupWithViewPager(pager)
    }

    override fun onLogIn() {
        layout.snack(" logged in")
    }

    override fun onLogOut() {
        layout.snack("Logged out") {
            action("Log in") { Trello.init(this@MainActivity) }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.login -> consume { Trello.init(this) }
        R.id.logout -> consume { Trello.logOut() }
        R.id.settings -> consume { startActivity(Intent(this, SettingsActivity::class.java)) }
        R.id.splash -> consume { startActivity(Intent(this, SplashActivity::class.java)) }
        else -> super.onOptionsItemSelected(item)
    }

    private inner class TabsAdapter(fm: FragmentManager): FragmentStatePagerAdapter(fm) {
        private val titles by stringArrayRes(R.array.titles)
        override fun getCount() = 3
        override fun getPageTitle(position: Int) = titles[position]
        override fun getItem(position: Int) = ListFragment
                .newInstance(Trello.listIds[position]!!, position == 0)
    }

}
