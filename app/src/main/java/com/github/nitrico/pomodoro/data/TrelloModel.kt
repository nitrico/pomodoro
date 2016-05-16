package com.github.nitrico.pomodoro.data

import java.io.Serializable

data class TrelloBoard(
        val id: String,
        val name: String,
        val lists: List<TrelloList>?) : Serializable

data class TrelloList(
        val id: String,
        val name: String,
        val cards: List<TrelloCard>?) : Serializable

data class TrelloCard(
        val id: String,
        val name: String,
        val desc: String,
        val due: String,
        val url: String) : Serializable

data class TrelloMember(
        val id: String,
        val email: String,
        val username: String,
        val fullName: String,
        val avatarHash: String?) : Serializable {

    val avatar: String?
        get() {
            if (avatarHash == null) return null
            return "https://trello-avatars.s3.amazonaws.com/$avatarHash/170.png"
        }

}
