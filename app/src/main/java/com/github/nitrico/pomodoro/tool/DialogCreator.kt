package com.github.nitrico.pomodoro.tool

import android.content.Context
import android.support.v7.widget.AppCompatEditText
import com.afollestad.materialdialogs.MaterialDialog
import com.github.nitrico.pomodoro.R
import com.github.nitrico.pomodoro.data.Trello
import com.github.nitrico.pomodoro.data.TrelloBoard
import com.github.nitrico.pomodoro.data.TrelloList

object DialogCreator {

    fun addTodo(context: Context, callback: (() -> Unit) ? = null) {
        MaterialDialog.Builder(context)
                .title("Add To do")
                .customView(R.layout.dialog_card, true)
                .positiveText("Add")
                .negativeText("Cancel")
                .negativeColor(R.color.black)
                .onPositive { dialog, action ->
                    val name = (dialog.findViewById(R.id.name) as AppCompatEditText).text.toString()
                    val desc = (dialog.findViewById(R.id.desc) as AppCompatEditText).text.toString()
                    Trello.addTodoCard(name, desc, callback)
                }
                .show()
    }

    fun chooseBoard(context: Context, callback: ((TrelloBoard) -> Unit) ? = null) {
        MaterialDialog.Builder(context)
                .title("Choose board")
                .items(Trello.boardNames)
                .itemsCallbackSingleChoice(-1, { dialog, itemView, which, text ->
                    val board = Trello.boards[which]
                    callback?.invoke(board)
                    true
                })
                .positiveText("Ok")
                .show()
    }

    fun chooseTodoList(context: Context, callback: ((TrelloList) -> Unit) ? = null) {
        MaterialDialog.Builder(context)
                .title("Choose to od list")
                .items(Trello.boardListNames)
                .itemsCallbackSingleChoice(-1, { dialog, itemView, which, text ->
                    val list = Trello.lists[which]
                    callback?.invoke(list)
                    true
                })
                .positiveText("Ok")
                .show()
    }

}
