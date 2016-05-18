package com.github.nitrico.pomodoro.ui

import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import com.github.nitrico.pomodoro.App
import com.github.nitrico.pomodoro.R
import com.github.nitrico.pomodoro.data.Trello
import com.github.nitrico.pomodoro.data.TrelloBoard
import com.github.nitrico.pomodoro.data.TrelloList
import com.github.nitrico.pomodoro.data.TrelloMember
import com.github.nitrico.pomodoro.tool.*
import com.thesurix.gesturerecycler.GestureAdapter
import com.thesurix.gesturerecycler.GestureManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.appbar.*
import kotlinx.android.synthetic.main.drawer_account.*
import kotlinx.android.synthetic.main.drawer_config.*

class MainActivity : AppCompatActivity(), Trello.SessionListener {

    private val listChangeListener = object : GestureAdapter.OnDataChangeListener<TrelloList> {
        override fun onItemReorder(item: TrelloList, fromPos: Int, toPos: Int) {
            Trello.setupLists(adapter.data[0].id, adapter.data[1].id, adapter.data[2].id)
        }
        override fun onItemRemoved(item: TrelloList, position: Int) { }
    }

    private lateinit var adapter: DrawerListAdapter
    private var currentBoardIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //drawer.getOverlay().add(DesignSpec.fromResource(drawer, R.raw.spec))

        // initialize Toolbar
        if (Build.VERSION.SDK_INT >= 21) window.statusBarColor = resources.getColor(R.color.statusBar)
        setFullScreenLayout()
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        appTitle.typeface = App.mocharyTypeface

        // initialize ViewPager and tabs
        pager.offscreenPageLimit = 2
        pager.adapter = TabsAdapter(supportFragmentManager)
        tabs.setupWithViewPager(pager)

        // initialize settings drawer
        boardSelector.setOnClickListener {
            DialogCreator.chooseBoard(this@MainActivity, currentBoardIndex) { setupBoard(it) }
        }
        adapter = DrawerListAdapter()
        adapter.setDataChangeListener(listChangeListener)
        drawerList.layoutManager = LinearLayoutManager(this)
        drawerList.adapter = adapter
        GestureManager.Builder(drawerList)
                .setLongPressDragEnabled(true)
                .setManualDragEnabled(true)
                .setSwipeEnabled(false)
                .build()

        // initialize Trello session
        Trello.init(this)
        if (Trello.logged) onLogIn() else onLogOut()
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
        setupConnectButton(true)
        drawerConfig.show()

        // setup board selector
        boardName.text = Trello.board?.name
        currentBoardIndex = Trello.boards.indexOf(Trello.board)

        // setup lists selector
        adapter.data = Trello.lists
        if (Trello.lists.size > 3) unusedHeader.show() else unusedHeader.hide()

        //splash.hide()
    }

    override fun onLogOut() {
        setupAccount(null)
        setupConnectButton(false)
        drawerConfig.hide()
        //splash.show()
    }

    private fun setupAccount(user: TrelloMember?) {
        if (user != null) {
            profile.show()
            if (user.avatar != null) {
                avatar.show()
                avatar.load(user.avatar!!)
            }
            fullname.text = user.fullName
            email.text = user.email
        }
        else {
            profile.hide()
            avatar.hide()
        }
    }

    private fun setupConnectButton(logged: Boolean) {
        val textRes = if (logged) R.string.logout else R.string.login
        val bgColorRes = if (logged) R.color.primary else R.color.trello
        connect.setText(textRes)
        connect.setBackgroundResource(bgColorRes)
        connect.setOnClickListener { if (logged) Trello.logOut() else Trello.logIn() }
    }

    private fun setupBoard(board: TrelloBoard) {
        Trello.setCurrentBoard(board)
        boardName.text = board.name
        currentBoardIndex = Trello.boards.indexOf(board)

        // update drawer list
        adapter.data = board.lists
        if (Trello.lists.size > 3) unusedHeader.show() else unusedHeader.hide()

        // reset tabs
        pager.adapter = TabsAdapter(supportFragmentManager)
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
