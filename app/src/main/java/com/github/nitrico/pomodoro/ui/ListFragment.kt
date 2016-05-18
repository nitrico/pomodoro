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
import com.github.nitrico.pomodoro.data.TrelloCard
import com.github.nitrico.pomodoro.tool.dp
import com.github.nitrico.pomodoro.tool.isPortrait
import com.github.nitrico.pomodoro.tool.navigationBarHeight
import io.nlopez.smartadapters.SmartAdapter
import kotlinx.android.synthetic.main.fragment_list.*
import org.jetbrains.anko.support.v4.withArguments

class ListFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener,
        Trello.SessionListener, Trello.DataListener {

    companion object {
        const val KEY_LIST_TYPE = "KEY_LIST_TYPE"
        fun newInstance(listType: Int) = ListFragment().withArguments(KEY_LIST_TYPE to listType)
    }

    private var listType = -1

    private val adapter by lazy {
        SmartAdapter.empty().map(TrelloCard::class.java, TrelloCardView::class.java).into(list)
    }

    override fun onCreateView(li: LayoutInflater, container: ViewGroup?, savedState: Bundle?): View {
        return li.inflate(R.layout.fragment_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        listType = arguments.getInt(KEY_LIST_TYPE, -1)

        // initialize UI
        with(list) {
            val dp8 = 8.dp
            setPadding(dp8, dp8, dp8, dp8 + activity.navigationBarHeight)
            if (activity.isPortrait) layoutManager = LinearLayoutManager(activity)
            else layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
        with(layout) {
            setOnRefreshListener(this@ListFragment)
            setColorSchemeResources(android.R.color.white)
            setProgressBackgroundColorSchemeResource(R.color.accent)
        }

        // set items
        if (savedInstanceState == null) onRefresh()
        else {
            val cards = when (listType) {
                0 -> Trello.todoCards
                1 -> Trello.doingCards
                2 -> Trello.doneCards
                else -> emptyList()
            }
            adapter.setItems(cards)
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
    override fun onLogOut() = adapter.setItems(emptyList<TrelloCard>())

}
