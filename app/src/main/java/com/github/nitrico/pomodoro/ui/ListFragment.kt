package com.github.nitrico.pomodoro.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.nitrico.pomodoro.R
import com.github.nitrico.pomodoro.data.Trello
import com.github.nitrico.pomodoro.data.TrelloCard
import com.github.nitrico.pomodoro.util.dp
import com.github.nitrico.pomodoro.util.navigationBarHeight
import io.nlopez.smartadapters.SmartAdapter
import kotlinx.android.synthetic.main.fragment_list.*
import org.jetbrains.anko.support.v4.withArguments

class ListFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    companion object {
        const val KEY_LIST_ID = "KEY_LIST_ID"
        const val KEY_IS_TODO = "KEY_IS_TODO"
        fun newInstance(listId: String, isTodoList: Boolean = false)
            = ListFragment().withArguments(KEY_LIST_ID to listId, KEY_IS_TODO to isTodoList)
    }

    private lateinit var listId: String
    private var isTodoList: Boolean = false
    private var items: List<TrelloCard> = emptyList()

    private val adapter by lazy {
        SmartAdapter.empty().map(TrelloCard::class.java, TrelloCardView::class.java).into(list)
    }

    override fun onCreateView(li: LayoutInflater, container: ViewGroup?, savedState: Bundle?): View {
        listId = arguments.getString(KEY_LIST_ID, null)
        isTodoList = arguments.getBoolean(KEY_IS_TODO, false)
        return li.inflate(R.layout.fragment_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        with(layout) {
            setOnRefreshListener(this@ListFragment)
            setColorSchemeResources(android.R.color.white)
            setProgressBackgroundColorSchemeResource(R.color.accent)
        }
        with(list) {
            setPadding(8.dp, 8.dp, 8.dp, 8.dp+activity.navigationBarHeight)
            layoutManager = LinearLayoutManager(activity)
        }
        if (savedInstanceState == null) onRefresh()
        else adapter.setItems(Trello.todoCards)
    }

    override fun onRefresh() {
        Trello.getListCards(listId) {
            items = it
            adapter.clearItems()
            adapter.addItems(items)
            layout.isRefreshing = false
        }
    }

}
