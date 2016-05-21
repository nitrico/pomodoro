package com.github.nitrico.pomodoro.action.trello

import com.github.nitrico.flux.action.Action
import com.github.nitrico.pomodoro.data.Trello
import com.github.nitrico.pomodoro.data.TrelloCard
import com.github.nitrico.pomodoro.store.TrelloStore
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class GetCards : Action() {

    lateinit var todoCards: List<TrelloCard> private set
    lateinit var doingCards: List<TrelloCard> private set
    lateinit var doneCards: List<TrelloCard> private set

    init {
        val id1 = TrelloStore.todoListId
        val id2 = TrelloStore.doingListId
        val id3 = TrelloStore.doneListId

        if (id1 != null && id2 != null && id3 != null) {
            Observable.zip(
                    Trello.api.getListCards(id1),
                    Trello.api.getListCards(id2),
                    Trello.api.getListCards(id3),
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

}
