package com.github.nitrico.pomodoro.tool

import android.content.Context
import android.support.v7.widget.AppCompatEditText
import android.view.LayoutInflater
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.github.nitrico.pomodoro.R
import com.github.nitrico.pomodoro.data.Trello
import com.github.nitrico.pomodoro.data.TrelloBoard
import com.github.nitrico.pomodoro.data.TrelloCard

object DialogCreator {

    fun chooseBoard(context: Context, current: Int, callback: ((TrelloBoard) -> Unit) ? = null) {
        MaterialDialog.Builder(context)
                .title("Choose board")
                .items(Trello.boardNames)
                .itemsCallbackSingleChoice(current, { dialog, itemView, which, text ->
                    val board = Trello.boards[which]
                    callback?.invoke(board)
                    true
                })
                .positiveText("Ok")
                .show()
    }

    fun addTodo(context: Context, callback: (() -> Unit) ? = null) {
        MaterialDialog.Builder(context)
                .title("Add To do")
                .positiveText("Add")
                .negativeText("Cancel")
                .negativeColor(R.color.black)
                .customView(R.layout.dialog_card, true)
                .onPositive { dialog, action ->
                    val name = (dialog.findViewById(R.id.name) as AppCompatEditText).text.toString()
                    val desc = (dialog.findViewById(R.id.desc) as AppCompatEditText).text.toString()
                    Trello.addTodoCard(name, desc, callback)
                }
                .show()
    }

    fun editCard(context: Context, card: TrelloCard, callback: (() -> Unit) ? = null) {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_card, null, false)
        (view.findViewById(R.id.name) as AppCompatEditText).setText(card.name, TextView.BufferType.EDITABLE)
        (view.findViewById(R.id.desc) as AppCompatEditText).setText(card.desc, TextView.BufferType.EDITABLE)
        MaterialDialog.Builder(context)
                .title("Edit card")
                .customView(view, true)
                .positiveText("Save")
                .negativeText("Cancel")
                .negativeColor(R.color.black)
                .onPositive { dialog, action ->
                    val name = (dialog.findViewById(R.id.name) as AppCompatEditText).text
                    val desc = (dialog.findViewById(R.id.desc) as AppCompatEditText).text
                    Trello.updateCard(card.id, name.toString(), desc.toString(), callback)
                }
                .show()
    }

}
