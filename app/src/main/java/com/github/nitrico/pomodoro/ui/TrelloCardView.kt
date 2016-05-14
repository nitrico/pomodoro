package com.github.nitrico.pomodoro.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.github.nitrico.pomodoro.R
import com.github.nitrico.pomodoro.data.TrelloCard
import com.github.nitrico.pomodoro.util.setTextOrHideView
import io.nlopez.smartadapters.views.BindableRelativeLayout
import kotlinx.android.synthetic.main.view_item_card.view.*

class TrelloCardView(context: Context) : BindableRelativeLayout<TrelloCard>(context) {

    override fun getLayoutId() = R.layout.view_item_card

    override fun bind(item: TrelloCard) {
        name.text = item.name
        desc.setTextOrHideView(item.desc)
        due.setTextOrHideView(item.due)

        setOnClickListener {
            Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(item.url)
                context.startActivity(this)
            }
        }
    }

}
