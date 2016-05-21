package com.github.nitrico.pomodoro.action.trello

import android.content.Context
import com.afollestad.materialdialogs.MaterialDialog
import com.github.nitrico.flux.action.Action
import com.github.nitrico.pomodoro.R
import com.github.nitrico.pomodoro.data.Trello
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class DeleteCard(
        private val context: Context,
        private val cardId: String) : Action() {

    init {
        MaterialDialog.Builder(context)
                .title(R.string.delete_card)
                .content(R.string.sure)
                .positiveText(R.string.delete)
                .negativeText(R.string.cancel)
                .negativeColor(R.color.black)
                .onPositive { materialDialog, dialogAction ->

                    Trello.api.deleteCard(cardId)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ postAction() }, { postError(it) })

                }
                .show()
    }

}
