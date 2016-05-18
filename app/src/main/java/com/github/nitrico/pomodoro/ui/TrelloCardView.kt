package com.github.nitrico.pomodoro.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.github.nitrico.pomodoro.R
import com.github.nitrico.pomodoro.data.TrelloCard
import com.github.nitrico.pomodoro.tool.DialogCreator
import com.github.nitrico.pomodoro.tool.hide
import com.github.nitrico.pomodoro.tool.setTextOrHideView
import com.github.nitrico.pomodoro.tool.show
import io.nlopez.smartadapters.views.BindableRelativeLayout
import kotlinx.android.synthetic.main.view_card.view.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class TrelloCardView(context: Context) : BindableRelativeLayout<TrelloCard>(context) {

    override fun getLayoutId() = R.layout.view_card

    override fun bind(card: TrelloCard) {
        name.text = card.name
        desc.setTextOrHideView(card.desc)

        if (card.pomodoros != 0 || card.seconds != 0.toLong()) {
            data.show()
            pomodoros.text = card.pomodoros.toString()
            seconds.text = card.seconds.toString() +"s"
        }
        else data.hide()

        open.setOnClickListener { openCard(card) }
        open.setOnLongClickListener { context.toast(R.string.open_card); true }
        edit.setOnClickListener { DialogCreator.editCard(context, card) }
        edit.setOnLongClickListener { context.toast(R.string.edit_card); true }
        timer.setOnClickListener { context.startActivity<TimerActivity>(TimerActivity.KEY_CARD to card) }
        timer.setOnLongClickListener { context.toast(R.string.start_timer); true }
    }

    /**
     * Opens the card on Trello app if installed or default browser if not
     */
    private fun openCard(card: TrelloCard) = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(card.url)
        context.startActivity(this)
    }

}
