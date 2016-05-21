package com.github.nitrico.pomodoro.action.trello

import com.github.nitrico.flux.action.Action
import com.github.nitrico.pomodoro.data.Trello
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class MoveCard(
        private val cardId: String,
        private val listId: String) : Action() {

    init {
        Trello.api.moveCardToList(cardId, listId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ postAction() }, { postError(it) })
    }

}
