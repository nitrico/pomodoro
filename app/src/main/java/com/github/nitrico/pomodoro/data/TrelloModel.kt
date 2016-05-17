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
        val due: String,
        val url: String) : TrelloItem

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
