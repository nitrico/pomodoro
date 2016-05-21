package com.github.nitrico.pomodoro.action.trello

import com.github.nitrico.flux.action.Action
import com.github.nitrico.pomodoro.data.Trello
import com.github.nitrico.pomodoro.data.TrelloCard
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class ReorderLists(
        val todoId: String,
        val doingId: String,
        val doneId: String) : Action() {

    lateinit var todoCards: List<TrelloCard> private set
    lateinit var doingCards: List<TrelloCard> private set
    lateinit var doneCards: List<TrelloCard> private set

    init {
        Observable.zip(
                Trello.api.getListCards(todoId),
                Trello.api.getListCards(doingId),
                Trello.api.getListCards(doneId),
                { l1, l2, l3 -> Triple<List<TrelloCard>, List<TrelloCard>, List<TrelloCard>>(l1, l2, l3) })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    todoCards = it.first
                    doingCards = it.second
                    doneCards = it.third
                    postAction()
                },{
                    postError(it)
                })
    }

}
