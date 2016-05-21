package com.github.nitrico.pomodoro.action.trello

import android.content.Context
import android.support.v7.widget.AppCompatEditText
import android.view.LayoutInflater
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.github.nitrico.flux.action.Action
import com.github.nitrico.pomodoro.R
import com.github.nitrico.pomodoro.data.Trello
import com.github.nitrico.pomodoro.data.TrelloCard
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class EditCard(
        private val context: Context,
        private val card: TrelloCard) : Action() {

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_card, null, false)
        (view.findViewById(R.id.name) as AppCompatEditText).setText(card.name, TextView.BufferType.EDITABLE)
        (view.findViewById(R.id.desc) as AppCompatEditText).setText(card.desc, TextView.BufferType.EDITABLE)

        MaterialDialog.Builder(context)
                .title(R.string.edit_card)
                .customView(view, true)
                .positiveText(R.string.update)
                .negativeText(R.string.cancel)
                .negativeColor(R.color.black)
                .onPositive { dialog, action ->

                    val name = (dialog.findViewById(R.id.name) as AppCompatEditText).text.toString()
                    val desc = (dialog.findViewById(R.id.desc) as AppCompatEditText).text.toString()

                    Observable.zip(
                            Trello.api.updateCardName(card.id, name.toString()),
                            Trello.api.updateCardDescription(card.id, desc.toString()),
                            { item1, item2 -> })
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ postAction() }, { postError(it) })

                }
                .show()
    }

}
