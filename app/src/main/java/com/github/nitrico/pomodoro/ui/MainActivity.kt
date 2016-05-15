package com.github.nitrico.pomodoro.ui

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v7.widget.AppCompatEditText
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import com.afollestad.materialdialogs.MaterialDialog
import com.github.nitrico.pomodoro.R
import com.github.nitrico.pomodoro.data.Trello
import com.github.nitrico.pomodoro.data.TrelloMember
import com.github.nitrico.pomodoro.util.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.appbar.*
import kotlinx.android.synthetic.main.drawer_account.*
import kotlinx.android.synthetic.main.drawer_config.*
import org.jetbrains.anko.toast
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class MainActivity : AppCompatActivity(), Trello.SessionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // initialize UI
        setFullScreenLayout()
        setSupportActionBar(toolbar)
        appTitle.typeface = TypefaceCache.mochary
        //fab.setMargins(16.dp, 0, 16.dp, 16.dp + getNavigationBarHeight())
        initTabs()

        // initialize Trello session
        Trello.addSessionListener(this)
        if (savedInstanceState == null) Trello.init(this)
        else {
            if (Trello.logged) onLogIn()
            else {
                onLogOut()
                drawer.open()
            }
        }
    }

    private fun initTabs() {
        pager.adapter = TabsAdapter(supportFragmentManager)
        pager.offscreenPageLimit = 2
        tabs.setupWithViewPager(pager)
        //tabs.setIcons(drawablesFromArrayRes(R.array.icons))
    }

    override fun onLogIn() {
        setupAccount(Trello.user)
        with(connect) {
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
        with(connect) {
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
            //username.text = user.username
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

    private fun addCard() = MaterialDialog.Builder(this)
            .title("Add To do")
            .customView(R.layout.dialog_card, true)
            .positiveText("Add")
            .negativeText("Cancel")
            .negativeColor(R.color.black)
            .onPositive { dialog, action ->
                val name = (dialog.findViewById(R.id.name) as AppCompatEditText).text
                val desc = (dialog.findViewById(R.id.desc) as AppCompatEditText).text
                println("New card: " +name +" " +desc)
                // ADD THE CARD
            }
            .show()

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> { drawer.toggle(); true }
        //R.id.timer -> { startActivity(Intent(this, TimerActivity::class.java)); true }
        R.id.add -> { addCard(); true }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (drawer.isOpen) drawer.close()
        else super.onBackPressed()
    }

    private inner class TabsAdapter(fm: FragmentManager): FragmentStatePagerAdapter(fm) {
        private val titles: List<String> by stringsFromArrayRes(R.array.titles)
        override fun getCount() = 3
        override fun getPageTitle(position: Int) = titles[position]
        override fun getItem(position: Int)
                = ListFragment.newInstance(Trello.listIds[position]!!, position == 0)
    }

}
