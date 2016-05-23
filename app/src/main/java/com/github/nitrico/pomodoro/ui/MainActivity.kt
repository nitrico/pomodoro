package com.github.nitrico.pomodoro.ui

import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import com.github.nitrico.flux.action.ErrorAction
import com.github.nitrico.flux.store.StoreChange
import com.github.nitrico.pomodoro.App
import com.github.nitrico.pomodoro.R
import com.github.nitrico.pomodoro.action.trello.*
import com.github.nitrico.pomodoro.data.*
import com.github.nitrico.pomodoro.store.TrelloStore
import com.github.nitrico.pomodoro.tool.*
import com.thesurix.gesturerecycler.GestureAdapter
import com.thesurix.gesturerecycler.GestureManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_appbar.*
import kotlinx.android.synthetic.main.drawer_profile.*
import kotlinx.android.synthetic.main.drawer_config.*
import org.jetbrains.anko.toast

class MainActivity : FluxActivity() {

    private val listChangeListener = object : GestureAdapter.OnDataChangeListener<TrelloList> {
        override fun onItemRemoved(item: TrelloList, position: Int) { /* can't happen */ }
        override fun onItemReorder(item: TrelloList, fromPos: Int, toPos: Int) {
            ReorderLists(adapter.data[0].id, adapter.data[1].id, adapter.data[2].id)
        }
    }

    private val adapter = DrawerListAdapter()
    private var currentBoardIndex = 0

    override fun getStores() = listOf(TrelloStore)

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setFullScreenLayout()
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        appTitle.typeface = App.mocharyTypeface
        setTaskDescription(R.drawable.ic_main)

        // initialize ViewPager and tabs
        pager.offscreenPageLimit = 2
        pager.adapter = TabsAdapter(supportFragmentManager)
        tabs.setupWithViewPager(pager)

        // initialize settings drawer
        adapter.setDataChangeListener(listChangeListener)
        drawerList.layoutManager = LinearLayoutManager(this)
        drawerList.adapter = adapter
        GestureManager.Builder(drawerList)
                .setLongPressDragEnabled(true)
                .setManualDragEnabled(true)
                .setSwipeEnabled(false)
                .build()
        boardSelector.setOnClickListener { SelectBoard(this@MainActivity, currentBoardIndex) }
        connect.setOnClickListener { if (TrelloStore.logged) LogOut() else LogIn(this) }

        if (savedInstanceState == null) TrelloStore.init(this)
    }

    override fun onResume() {
        super.onResume()
        setupAccount()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> { drawer.toggle(); true }
        R.id.add -> { AddTodo(this); true }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (drawer.isOpen) drawer.close()
        else super.onBackPressed()
    }

    override fun onError(error: ErrorAction) {
        toast("${error.action} # ${error.throwable.message}")
    }

    override fun onStoreChanged(change: StoreChange) {
        when (change.store) {
            TrelloStore -> when (change.action) {
                is GetUser, is LogOut -> setupAccount()
                is SelectBoard -> setupBoard(TrelloStore.board!!)
            }
        }
    }

    private fun setupAccount() {
        setupProfile(TrelloStore.logged, TrelloStore.user)
        if (TrelloStore.logged) {
            splash.hide()
            drawerConfig.show()
            setupBoard(TrelloStore.board!!)
        }
        else {
            splash.show()
            drawerConfig.hide()
        }
    }

    private fun setupProfile(logged: Boolean, user: TrelloMember?) {
        if (logged && user != null) {
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
        connect.setText(if (logged) R.string.logout else R.string.login)
        connect.setBackgroundResource(if (logged) R.color.primary else R.color.trello)
    }

    private fun setupBoard(board: TrelloBoard) {
        boardName.text = board.name
        currentBoardIndex = TrelloStore.boards.indexOf(board)
        // update drawer list
        adapter.data = board.lists
        if (board.lists.size > 3) unusedHeader.show() else unusedHeader.hide()
    }


    private inner class TabsAdapter(fm: FragmentManager): FragmentStatePagerAdapter(fm) {
        private val titles = resources.getStringArray(R.array.titles)
        override fun getCount() = 3
        override fun getPageTitle(position: Int) = titles[position]
        override fun getItem(position: Int) = ListFragment.newInstance(position)
    }

}
