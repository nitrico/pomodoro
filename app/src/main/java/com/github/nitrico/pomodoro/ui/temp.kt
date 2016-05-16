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

