package com.github.nitrico.pomodoro.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.widget.AppCompatEditText
import android.view.LayoutInflater
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.github.nitrico.pomodoro.R
import com.github.nitrico.pomodoro.data.TrelloCard
import com.github.nitrico.pomodoro.tool.setTextOrHideView
import io.nlopez.smartadapters.views.BindableRelativeLayout
import kotlinx.android.synthetic.main.view_card.view.*
import org.jetbrains.anko.startActivity

class TrelloCardView(context: Context) : BindableRelativeLayout<TrelloCard>(context) {

    override fun getLayoutId() = R.layout.view_card

    override fun bind(item: TrelloCard) {
        name.text = item.name
        desc.setTextOrHideView(item.desc)

        // click listeners
        open.setOnClickListener { openCard(item) }
        edit.setOnClickListener { editCard(item) }
        timer.setOnClickListener {
            context.startActivity<TimerActivity>(TimerActivity.KEY_CARD to item)
        }
    }

    /**
     * Open card on Trello app, if installed, or browser otherwise
     */
    private fun openCard(card: TrelloCard) {
        Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(item.url)
            context.startActivity(this)
        }
    }

    private fun editCard(card: TrelloCard) {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_card, null, false)
        (view.findViewById(R.id.name) as AppCompatEditText).setText(item.name, TextView.BufferType.EDITABLE)
        (view.findViewById(R.id.desc) as AppCompatEditText).setText(item.desc, TextView.BufferType.EDITABLE)
        MaterialDialog.Builder(context)
                .title("Edit card")
                .customView(view, true)
                .positiveText("Save")
                .negativeText("Cancel")
                .negativeColor(R.color.black)
                .onPositive { dialog, action ->
                    val name = (dialog.findViewById(R.id.name) as AppCompatEditText).text
                    val desc = (dialog.findViewById(R.id.desc) as AppCompatEditText).text
                    // SAVE THE CARD !!
                }
                .show()
    }

    private fun moveCard() {

    }

}
