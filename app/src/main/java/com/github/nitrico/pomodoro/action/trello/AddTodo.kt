package com.github.nitrico.pomodoro.action.trello

import android.content.Context
import android.support.v7.widget.AppCompatEditText
import com.afollestad.materialdialogs.MaterialDialog
import com.github.nitrico.flux.action.Action
import com.github.nitrico.pomodoro.R
import com.github.nitrico.pomodoro.data.Trello
import com.github.nitrico.pomodoro.store.TrelloStore
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class AddTodo(private val context: Context) : Action() {

    init {
        MaterialDialog.Builder(context)
                .title(R.string.add_todo)
                .positiveText(R.string.add)
                .negativeText(R.string.cancel)
                .negativeColor(R.color.black)
                .customView(R.layout.dialog_card, true)
                .onPositive { dialog, action ->

                    val name = (dialog.findViewById(R.id.name) as AppCompatEditText).text.toString()
                    val desc = (dialog.findViewById(R.id.desc) as AppCompatEditText).text.toString()
                    val listId = TrelloStore.todoListId

                    if (listId != null) {
                        Trello.api.addCardToList(listId, name, desc)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({ postAction() },{ postError(it) })
                    }

                }
                .show()
    }

}
