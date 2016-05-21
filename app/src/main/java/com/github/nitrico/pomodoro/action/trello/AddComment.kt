package com.github.nitrico.pomodoro.action.trello

import android.content.Context
import android.view.LayoutInflater
import com.afollestad.materialdialogs.MaterialDialog
import com.github.nitrico.flux.action.Action
import com.github.nitrico.pomodoro.R
import com.github.nitrico.pomodoro.data.Trello
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class AddComment(
        private val context: Context,
        private val cardId: String,
        private val comment: String) : Action() {

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_comment, null, false)
        MaterialDialog.Builder(context)
                .title(R.string.add_comment)
                .customView(view, true)
                .positiveText(R.string.add)
                .negativeText(R.string.cancel)
                .negativeColor(R.color.black)
                .onPositive { dialog, action ->

                    Trello.api.addCommentToCard(cardId, comment)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ postAction() }, { postError(it) })

                }
                .show()
    }

}
