package com.github.nitrico.pomodoro.ui

/*
        if (boardId != null) api.getBoardLists(boardId!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    lists = it
                },{
                    context.toast(it.message ?: "Unknown error")
                })


    fun chooseTodoList(context: Context, callback: ((TrelloList) -> Unit) ? = null) {
        MaterialDialog.Builder(context)
                .title("Choose to do list")
                .items(Trello.boardListNames)
                .itemsCallbackSingleChoice(-1, { dialog, itemView, which, text ->
                    val list = Trello.lists[which]
                    callback?.invoke(list)
                    true
                })
                .positiveText("Ok")
                .show()
    }



api.getUser(token!!)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .flatMap { it -> api.getBoards() }
        .subscribe({
            user = it
            if (user != null) {
                //context.toast(user!!.username + " logged in")
            }
            dispatchLogIn() // notify listeners
        },{
            context.toast(it.message ?: "Unknown error")
        })
api.getBoards()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
            boards = it
            println(boards)
            //it.forEach { println(it.lists) }
        },{
            context.toast(it.message ?: "Unknown error")
        })
        */

