package com.github.nitrico.pomodoro.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.nitrico.pomodoro.R
import com.github.nitrico.pomodoro.data.Trello
import com.github.nitrico.pomodoro.tool.dp
import com.github.nitrico.pomodoro.tool.navigationBarHeight
import kotlinx.android.synthetic.main.fragment_list.*
import org.jetbrains.anko.support.v4.withArguments

class ListFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener,
        Trello.SessionListener, Trello.DataListener {

    companion object {
        const val KEY_LIST_TYPE = "KEY_LIST_TYPE"
        fun newInstance(listType: Int) = ListFragment().withArguments(KEY_LIST_TYPE to listType)
    }

    private lateinit var adapter: CardsAdapter
    private var listType = -1

    override fun onCreateView(li: LayoutInflater, container: ViewGroup?, savedState: Bundle?): View {
        retainInstance = true
        return li.inflate(R.layout.fragment_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val tablet = activity.resources.getBoolean(R.bool.tablet)
        val landscape = activity.resources.getBoolean(R.bool.landscape)
        listType = arguments.getInt(KEY_LIST_TYPE, -1)

        // initialize SwipeRefreshLayout
        with(layout) {
            setOnRefreshListener(this@ListFragment)
            setColorSchemeResources(android.R.color.white)
            setProgressBackgroundColorSchemeResource(R.color.accent)
        }

        // setup RecyclerView paddings
        if (tablet) {
            val dp72 = 72.dp
            list.setPadding(dp72, dp72, dp72, dp72 + activity.navigationBarHeight)
        } else {
            val dp8 = 8.dp
            list.setPadding(dp8, dp8, dp8, dp8 + activity.navigationBarHeight)
        }

        // columns
        val cols = getColumnsNumber(tablet, landscape)
        if (cols == 1) list.layoutManager = LinearLayoutManager(activity)
        else list.layoutManager = StaggeredGridLayoutManager(cols, StaggeredGridLayoutManager.VERTICAL)

        adapter = CardsAdapter(emptyList(), listType == 0)
        list.adapter = adapter

        // set items
        if (savedInstanceState == null) onRefresh()
        else {
            val items = when (listType) {
                0 -> Trello.todoCards
                1 -> Trello.doingCards
                2 -> Trello.doneCards
                else -> emptyList()
            }
            adapter.setItems(items)
        }
    }

    override fun onRefresh() {
        val id = when(listType) {
            0 -> Trello.todoListId
            1 -> Trello.doingListId
            2 -> Trello.doneListId
            else -> null
        }
        Trello.getListCards(id) {
            adapter.setItems(it)
            layout.isRefreshing = false
        }
    }

    override fun onResume() {
        super.onResume()
        Trello.addDataListener(this)
        Trello.addSessionListener(this)
        onRefresh()
    }

    override fun onPause() {
        super.onPause()
        Trello.removeDataListener(this)
        Trello.removeSessionListener(this)
    }

    override fun onDataChanged() = onRefresh()
    override fun onLogIn() = onRefresh()
    override fun onLogOut() { adapter.setItems(emptyList()) }

    private fun getColumnsNumber(tablet: Boolean, landscape: Boolean): Int {
        return if (tablet) { if (landscape) 4 else 3 }
        else { if (landscape) 2 else 1 }
    }

}
