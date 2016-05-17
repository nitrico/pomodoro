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

        // click listeners
        open.setOnClickListener { openCard(card) }
        edit.setOnClickListener { DialogCreator.editCard(context, card) }
        //delete.setOnClickListener { DialogCreator.deleteCard(context, card) }
        //comment.setOnClickListener { DialogCreator.addComment(context, card) }
        timer.setOnClickListener {
            context.startActivity<TimerActivity>(TimerActivity.KEY_CARD to card)
        }

        open.setOnLongClickListener { context.toast(R.string.open_card); true }
        edit.setOnLongClickListener { context.toast(R.string.edit_card); true }
        //delete.setOnLongClickListener { context.toast(R.string.delete_card); true }
        //comment.setOnLongClickListener { context.toast(R.string.add_comment); true }
        // timer.setOnLongClickListener { context.toast(""); true }
    }

    /**
     * Open card on Trello app, if installed, or browser otherwise
     */
    private fun openCard(card: TrelloCard) {
        Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(card.url)
            context.startActivity(this)
        }
    }

}
