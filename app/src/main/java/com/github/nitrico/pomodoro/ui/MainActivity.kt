package com.github.nitrico.pomodoro.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import com.github.nitrico.pomodoro.R
import com.github.nitrico.pomodoro.data.Trello
import com.github.nitrico.pomodoro.data.TrelloList
import com.github.nitrico.pomodoro.data.TrelloMember
import com.github.nitrico.pomodoro.tool.*
import com.thesurix.gesturerecycler.GestureAdapter
import com.thesurix.gesturerecycler.GestureManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.appbar.*
import kotlinx.android.synthetic.main.drawer.*
import kotlinx.android.synthetic.main.drawer_account.*

class MainActivity : AppCompatActivity(), Trello.SessionListener {

    private lateinit var drawerAdapter: DrawerListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // initialize UI
        setFullScreenLayout()
        setSupportActionBar(toolbar)
        appTitle.typeface = App.mocharyTypeface
        pager.adapter = TabsAdapter(supportFragmentManager)
        pager.offscreenPageLimit = 2
        tabs.setupWithViewPager(pager)
        //tabs.setIcons(drawablesFromArrayRes(R.array.icons))

        drawerList.layoutManager = LinearLayoutManager(this)
        drawerAdapter = DrawerListAdapter()
        drawerAdapter.setDataChangeListener(object : GestureAdapter.OnDataChangeListener<TrelloList> {
            override fun onItemReorder(item: TrelloList, fromPos: Int, toPos: Int) { }
            override fun onItemRemoved(item: TrelloList, position: Int) { }
        })
        drawerList.adapter = drawerAdapter
        GestureManager.Builder(drawerList).setLongPressDragEnabled(true).build()

        // initialize Trello session
        Trello.init(this)
        if (Trello.logged) onLogIn()
        else onLogOut()
    }

    override fun onResume() {
        super.onResume()
        Trello.addSessionListener(this)
    }

    override fun onPause() {
        super.onPause()
        Trello.removeSessionListener(this)
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
            setOnClickListener {
                DialogCreator.chooseBoard(this@MainActivity) {
                    boardName.text = it.name
                    Trello.boardId = it.id
                }
            }
        }
        drawerAdapter.data = Trello.lists
    }

    override fun onLogOut() {
        setupAccount(null)
        with(connect) {
            setText(R.string.login)
            setBackgroundResource(R.color.trello)
            setOnClickListener { Trello.logIn(this@MainActivity) }
        }
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> { drawer.toggle(); true }
        R.id.add -> { DialogCreator.addTodo(this); true }
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
        override fun getItem(position: Int) = ListFragment.newInstance(position)
    }

}
