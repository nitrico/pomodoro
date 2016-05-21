package com.github.nitrico.pomodoro.action.trello

import com.github.nitrico.flux.action.Action
import com.github.nitrico.pomodoro.data.Trello
import com.github.nitrico.pomodoro.data.TrelloBoard
import com.github.nitrico.pomodoro.data.TrelloMember
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class GetUser : Action() {

    lateinit var user: TrelloMember private set
    lateinit var boards: List<TrelloBoard> private set

    init {
        Observable.zip(
                Trello.api.getUser(),
                Trello.api.getBoards(),
                { user, boards -> Pair<TrelloMember, List<TrelloBoard>>(user, boards) })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    user = it.first
                    // use only boards with 3 or more lists
                    boards = it.second.filter { it.lists.size >= 3 }
                    postAction()
                },{
                    postError(it)
                })
    }

}
