package com.github.nitrico.pomodoro.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.view.Menu
import android.view.MenuItem
import com.github.nitrico.pomodoro.R
import com.github.nitrico.pomodoro.trello.Trello
import com.github.nitrico.pomodoro.util.consume
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val fragments = listOf(TodoListFragment(), DoingListFragment(), DoneListFragment())
    private val titles = listOf("To do", "Doing", "Done")
    private val tabsAdapter: TabsAdapter by lazy { TabsAdapter(supportFragmentManager) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        with(pager) {
            adapter = tabsAdapter
            offscreenPageLimit = 2
            addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
                //override fun onPageSelected(position: Int) = tabs.tint(position)
            })
        }
        tabs.setupWithViewPager(pager)

        Trello.init(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.login -> consume { Trello.init(this) }
        R.id.logout -> consume { Trello.logOut() }
        else -> super.onOptionsItemSelected(item)
    }

    private inner class TabsAdapter(fm: FragmentManager): FragmentStatePagerAdapter(fm) {
        override fun getCount() = 3
        override fun getItem(position: Int) = fragments[position]
        override fun getPageTitle(position: Int) = titles[position]
    }

}
