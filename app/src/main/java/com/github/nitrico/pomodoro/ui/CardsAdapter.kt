package com.github.nitrico.pomodoro.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.nitrico.pomodoro.R
import com.github.nitrico.pomodoro.data.TrelloCard
import com.github.nitrico.pomodoro.tool.*
import kotlinx.android.synthetic.main.item_card.view.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class CardsAdapter(
        private var list: List<TrelloCard>,
        private val todoList: Boolean) : RecyclerView.Adapter<CardsAdapter.ViewHolder>() {


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(card: TrelloCard) = with (itemView) {
            name.text = card.name
            desc.setTextOrHideView(card.desc)

            if (card.pomodoros != 0 || card.seconds != 0.toLong()) {
                data.show()
                pomodoros.text = card.pomodoros.toString()
                seconds.text = card.seconds.toTimeString()
            }
            else data.hide()

            // click listeners

            open.setOnClickListener {  context.openCard(card) }
            open.setOnLongClickListener { context.toast(R.string.open_card); true }
            edit.setOnClickListener { DialogCreator.editCard(context, card) }
            edit.setOnLongClickListener { context.toast(R.string.edit_card); true }

            if (todoList) {
                timer.show()
                timer.setOnLongClickListener { context.toast(R.string.start_timer); true }
                timer.setOnClickListener {
                    context.startActivity<TimerActivity>(TimerActivity.KEY_CARD to card)
                }
            }
            else timer.hide()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(list[position])

    override fun getItemCount() = list.size

    fun setItems(items: List<TrelloCard>) {
        list = items
        notifyDataSetChanged()
    }

    /**
     * Open the card on Trello app if installed or default browser if not
     */
    private fun Context.openCard(card: TrelloCard) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(card.url)
        startActivity(intent)
    }

}
