package com.github.nitrico.pomodoro.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.github.nitrico.pomodoro.R
import com.github.nitrico.pomodoro.data.Trello
import com.github.nitrico.pomodoro.data.TrelloBoard
import com.github.nitrico.pomodoro.data.TrelloList
import kotlinx.android.synthetic.main.activity_settings.*
import org.jetbrains.anko.toast
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class SettingsActivity : AppCompatActivity() {

    private val boards: MutableList<TrelloBoard> = mutableListOf()
    private val boardTitles: MutableList<String> = mutableListOf()

    private val lists: MutableList<TrelloList> = mutableListOf()
    private val listTitles: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        Trello.api.getBoards(Trello.token!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { onBoardsLoaded(it) }

        boardSpinner.isEnabled = false
        todoSpinner.isEnabled = false
        doingSpinner.isEnabled = false
        doneSpinner.isEnabled = false
    }

    private fun onBoardsLoaded(list: List<TrelloBoard>) {
        boardSpinner.isEnabled = true
        boards.clear()
        boardTitles.clear()
        boards.addAll(list)
        boards.forEach { boardTitles.add(it.name) }
        val boardAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, boardTitles)
        boardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        boardSpinner.adapter = boardAdapter
        boardSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(spinner: AdapterView<*>, view: View, position: Int, id: Long) {
                onBoardSelected(boards[position])
            }
            override fun onNothingSelected(spinner: AdapterView<*>) { }
        }
    }

    private fun onBoardSelected(board: TrelloBoard) {
        todoSpinner.isEnabled = true
        toast(board.name + " selected")
        lists.clear()
        listTitles.clear()
        lists.addAll(board.lists!!)
        lists.forEach { listTitles.add(it.name) }
        val todoAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listTitles)
        todoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        todoSpinner.adapter = todoAdapter
    }

    private fun onTodoListSelected(list: TrelloList) {

    }

}
