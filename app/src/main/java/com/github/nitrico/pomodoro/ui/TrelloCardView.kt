package com.github.nitrico.pomodoro.ui

import android.content.Context
import com.github.nitrico.pomodoro.R
import com.github.nitrico.pomodoro.data.TrelloCard
import io.nlopez.smartadapters.views.BindableRelativeLayout
import kotlinx.android.synthetic.main.view_item_card.view.*

class TrelloCardView(context: Context) : BindableRelativeLayout<TrelloCard>(context) {

    override fun getLayoutId() = R.layout.view_item_card

    override fun bind(item: TrelloCard) {
        text.text = item.name
    }

}
