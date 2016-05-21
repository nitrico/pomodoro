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
import kotlinx.android.synthetic.main.appbar.*
import kotlinx.android.synthetic.main.drawer_account.*
import kotlinx.android.synthetic.main.drawer_config.*
import org.jetbrains.anko.toast

class MainActivity : FluxActivity() {

    companion object {
        val stores = listOf(TrelloStore)
    }

    private val listChangeListener = object : GestureAdapter.OnDataChangeListener<TrelloList> {
        override fun onItemReorder(item: TrelloList, fromPos: Int, toPos: Int) {
            ReorderLists(adapter.data[0].id, adapter.data[1].id, adapter.data[2].id)
        }
        override fun onItemRemoved(item: TrelloList, position: Int) { }
    }

    private lateinit var adapter: DrawerListAdapter
    private var currentBoardIndex = 0

    override fun getStores() = stores

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        TrelloStore.init(this)

        // initialize Toolbar
        /*if (Build.VERSION.SDK_INT >= 21) {
            window.statusBarColor = resources.getColor(R.color.statusBar)
        }*/
        setFullScreenLayout()
        //window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        appTitle.typeface = App.mocharyTypeface
        setTaskDescription(R.drawable.ic_main)

        // initialize ViewPager and tabs
        pager.offscreenPageLimit = 2
        pager.adapter = TabsAdapter(supportFragmentManager)
        tabs.setupWithViewPager(pager)

        // initialize settings drawer
        boardSelector.setOnClickListener { SelectBoard(this@MainActivity, currentBoardIndex) }
        adapter = DrawerListAdapter()
        adapter.setDataChangeListener(listChangeListener)
        drawerList.layoutManager = LinearLayoutManager(this)
        drawerList.adapter = adapter
        GestureManager.Builder(drawerList)
                .setLongPressDragEnabled(true)
                .setManualDragEnabled(true)
                .setSwipeEnabled(false)
                .build()
        connect.setOnClickListener { if (TrelloStore.logged) LogOut() else LogIn(this) }
        setupConnectButton(false)

        // initialize Trello session
        if (savedInstanceState == null && TrelloStore.user == null) LogIn(this)
        else setupAccount()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> { drawer.toggle(); true }
        R.id.add -> {
            AddTodo(this); true }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (drawer.isOpen) drawer.close()
        else super.onBackPressed()
    }

    override fun onError(error: ErrorAction) {
        toast(error.throwable.message ?: "Unknown error in action: " +error.action)
    }

    override fun onStoreChanged(change: StoreChange) {
        when (change.store) {
            TrelloStore -> when (change.action) {
                is GetUser -> setupAccount()
                is SelectBoard -> setupBoard(TrelloStore.board!!)
                is LogOut -> onLogOut()
            }
        }
    }

    private fun setupAccount() {
        setupProfile(TrelloStore.user)
        setupConnectButton(true)
        drawerConfig.show()

        // setup board selector
        boardName.text = TrelloStore.board?.name
        currentBoardIndex = TrelloStore.boards.indexOf(TrelloStore.board)

        // setup lists selector
        adapter.data = TrelloStore.lists
        if (TrelloStore.lists.size > 3) unusedHeader.show() else unusedHeader.hide()

        //splash.hide()
    }

    private fun onLogOut() {
        setupProfile(null)
        setupConnectButton(false)
        drawerConfig.hide()
        //splash.show()
    }

    private fun setupProfile(user: TrelloMember?) {
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
    }

    private fun setupBoard(board: TrelloBoard) {
        boardName.text = board.name
        currentBoardIndex = TrelloStore.boards.indexOf(board)

        // update drawer list
        adapter.data = board.lists
        if (TrelloStore.lists.size > 3) unusedHeader.show() else unusedHeader.hide()

        // reset tabs
        pager.adapter = TabsAdapter(supportFragmentManager)
    }


    private inner class TabsAdapter(fm: FragmentManager): FragmentStatePagerAdapter(fm) {
        private val titles = resources.getStringArray(R.array.titles)
        override fun getCount() = 3
        override fun getPageTitle(position: Int) = titles[position]
        override fun getItem(position: Int) = ListFragment.newInstance(position)
    }

}
