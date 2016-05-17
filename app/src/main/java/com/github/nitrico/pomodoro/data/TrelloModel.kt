package com.github.nitrico.pomodoro.data

import java.io.Serializable

interface TrelloItem : Serializable {
    val id: String
}

class TrelloBoard(
        override val id: String,
        val name: String,
        val lists: List<TrelloList>) : TrelloItem

class TrelloList(
        override val id: String,
        val name: String,
        val cards: List<TrelloCard>?) : TrelloItem

class TrelloCard(
        override val id: String,
        val name: String,
        val desc: String,
        val url: String) : TrelloItem {

    val pomodoros: Int get() = Data.getPomodoros(id)
    val seconds: Long get() = Data.getSeconds(id)

}

class TrelloMember(
        override val id: String,
        val email: String,
        val username: String,
        val fullName: String,
        val avatarHash: String?) : TrelloItem {

    val avatar: String?
        get() {
            if (avatarHash == null) return null
            return "https://trello-avatars.s3.amazonaws.com/$avatarHash/170.png"
        }

}
