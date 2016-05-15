package com.github.nitrico.pomodoro.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.widget.AppCompatEditText
import android.view.LayoutInflater
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.github.nitrico.pomodoro.R
import com.github.nitrico.pomodoro.data.Trello
import com.github.nitrico.pomodoro.data.TrelloCard
import com.github.nitrico.pomodoro.util.setTextOrHideView
import io.nlopez.smartadapters.views.BindableRelativeLayout
import kotlinx.android.synthetic.main.view_card.view.*
import org.jetbrains.anko.toast
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class TrelloCardView(context: Context) : BindableRelativeLayout<TrelloCard>(context) {

    override fun getLayoutId() = R.layout.view_card

    override fun bind(item: TrelloCard) {
        name.text = item.name
        desc.setTextOrHideView(item.desc)
        //due.setTextOrHideView(item.due)


        open.setOnClickListener {
            // open card on Trello app (if installed) or browser
            Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(item.url)
                context.startActivity(this)
            }
        }

        timer.setOnClickListener { context.startActivity(Intent(context, TimerActivity::class.java)) }

        edit.setOnClickListener {
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
                        println("Card: " +name +" " +desc)
                        // SAVE THE CARD
                    }
                    .show()
        }

        /*
        setOnLongClickListener {
            Trello.api.moveCardToList(item.id, Trello.doneListId!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        context.toast("card moved to Done list")
                    },{
                        context.toast(it.message.toString())
                    })

            true
        }
        */
    }

}
