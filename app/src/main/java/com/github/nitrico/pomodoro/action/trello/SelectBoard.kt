package com.github.nitrico.pomodoro.action.trello

import android.content.Context
import com.afollestad.materialdialogs.MaterialDialog
import com.github.nitrico.flux.action.Action
import com.github.nitrico.pomodoro.R
import com.github.nitrico.pomodoro.data.TrelloBoard
import com.github.nitrico.pomodoro.store.TrelloStore

class SelectBoard(
        private val context: Context,
        private val current: Int) : Action() {

    lateinit var board: TrelloBoard private set

    init {
        MaterialDialog.Builder(context)
                .title(R.string.select_board)
                .positiveText(R.string.select)
                .items(TrelloStore.boardNames)
                .itemsCallbackSingleChoice(current, { dialog, itemView, which, text ->

                    board = TrelloStore.boards[which]
                    postAction()
                    true

                })
                .show()
    }

}
