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

    fun chooseBoard(context: Context, current: Int, callback: ((TrelloBoard) -> Unit)? = null) {
        MaterialDialog.Builder(context)
                .title(R.string.select_board)
                .positiveText(R.string.ok)
                .items(Trello.boardNames)
                .itemsCallbackSingleChoice(current, { dialog, itemView, which, text ->
                    val board = Trello.boards[which]
                    callback?.invoke(board)
                    true
                })
                .show()
    }

    fun addTodo(context: Context, callback: (() -> Unit) ? = null) {
        MaterialDialog.Builder(context)
                .title(R.string.add_todo)
                .positiveText(R.string.add)
                .negativeText(R.string.cancel)
                .negativeColor(R.color.black)
                .customView(R.layout.dialog_card, true)
                .onPositive { dialog, action ->
                    val name = (dialog.findViewById(R.id.name) as AppCompatEditText).text.toString()
                    val desc = (dialog.findViewById(R.id.desc) as AppCompatEditText).text.toString()
                    Trello.addTodoCard(name, desc, callback)
                }
                .show()
    }

    fun editCard(context: Context, card: TrelloCard, callback: (() -> Unit)? = null) {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_card, null, false)
        (view.findViewById(R.id.name) as AppCompatEditText).setText(card.name, TextView.BufferType.EDITABLE)
        (view.findViewById(R.id.desc) as AppCompatEditText).setText(card.desc, TextView.BufferType.EDITABLE)
        MaterialDialog.Builder(context)
                .title(R.string.edit_card)
                .customView(view, true)
                .positiveText(R.string.save)
                .negativeText(R.string.cancel)
                .negativeColor(R.color.black)
                .onPositive { dialog, action ->
                    val name = (dialog.findViewById(R.id.name) as AppCompatEditText).text
                    val desc = (dialog.findViewById(R.id.desc) as AppCompatEditText).text
                    Trello.updateCard(card.id, name.toString(), desc.toString(), callback)
                }
                .show()
    }

    fun addComment(context: Context, card: TrelloCard, callback: (() -> Unit)? = null) {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_comment, null, false)
        MaterialDialog.Builder(context)
                .title(R.string.add_comment)
                .customView(view, true)
                .positiveText(R.string.add)
                .negativeText(R.string.cancel)
                .negativeColor(R.color.black)
                .onPositive { dialog, action ->
                    val comment = (dialog.findViewById(R.id.comment) as AppCompatEditText).text
                    Trello.addCommentToCard(card.id, comment.toString(), callback)
                }
                .show()
    }

    fun deleteCard(context: Context, card: TrelloCard, callback: (() -> Unit)? = null) {
        MaterialDialog.Builder(context)
                .title(R.string.delete_card)
                .content(R.string.sure)
                .positiveText(R.string.delete)
                .negativeText(R.string.cancel)
                .negativeColor(R.color.black)
                .onPositive { materialDialog, dialogAction ->
                    Trello.deleteCard(card.id, callback)
                }
                .show()
    }

}
