package com.github.nitrico.pomodoro.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.github.nitrico.pomodoro.App
import com.github.nitrico.pomodoro.R
import com.github.nitrico.pomodoro.data.Trello
import com.github.nitrico.pomodoro.data.TrelloMember
import com.github.nitrico.pomodoro.util.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.drawer_account.*
import kotlinx.android.synthetic.main.drawer_config.*

class MainActivity : AppCompatActivity(), Trello.SessionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        /*if (isPortrait && isKitkatOrHigher) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        }*/
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        abTitle.setText(R.string.app_name)
        abTitle.typeface = App.titleTypeface
        //fab.setMargins(16.dp, 0, 16.dp, 16.dp + getNavigationBarHeight())
        setupTabs()

        Trello.addSessionListener(this)
        if (savedInstanceState == null) Trello.init(this)
        else {
            if (Trello.logged) onLogIn()
            else {
                onLogOut()
                layout.open()
            }
        }
    }

    private fun setupTabs() {
        pager.adapter = TabsAdapter(supportFragmentManager)
        pager.offscreenPageLimit = 2
        pager.pageMargin = 16.dp
        tabs.setupWithViewPager(pager)
    }

    override fun onLogIn() {
        setupAccount(Trello.user)
        with(sessionButton) {
            setText(R.string.logout)
            setBackgroundResource(R.color.primary)
            setOnClickListener { Trello.logOut() }
        }
        with(board) {
            isEnabled = true
            boardHeader.isEnabled = true
            boardName.isEnabled = true
            setOnClickListener {  }
        }
        enableLists(false)
    }

    override fun onLogOut() {
        setupAccount(null)
        with(sessionButton) {
            setText(R.string.login)
            setBackgroundResource(R.color.trello)
            setOnClickListener { Trello.init(this@MainActivity) }
        }
        //account.hide()
        board.isEnabled = false
        boardHeader.isEnabled = false
        boardName.isEnabled = false
    }

    private fun setupAccount(user: TrelloMember?) {
        if (user != null) {
            profile.show()
            if (user.avatar != null) {
                avatar.show()
                avatar.load(user.avatar!!)
            }
            fullname.text = user.fullName
            username.text = user.username
            email.text = user.email
        }
        else {
            profile.hide()
            avatar.hide()
        }
    }

    private fun enableLists(enable: Boolean) {
        todoList.isEnabled = enable
        doingList.isEnabled = enable
        doneList.isEnabled = enable
        todoHeader.isEnabled = enable
        doingHeader.isEnabled = enable
        doneHeader.isEnabled = enable
        todoListName.isEnabled = enable
        doingListName.isEnabled = enable
        doneListName.isEnabled = enable
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> consume { layout.toggle() }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() = if (layout.isOpen()) layout.close() else super.onBackPressed()

    private inner class TabsAdapter(fm: FragmentManager): FragmentStatePagerAdapter(fm) {
        private val titles by stringArrayRes(R.array.titles)
        override fun getCount() = 3
        override fun getPageTitle(position: Int) = titles[position]
        override fun getItem(position: Int)
                = ListFragment.newInstance(Trello.listIds[position]!!, position == 0)
    }

}
