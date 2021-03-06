package com.github.nitrico.pomodoro.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.github.nitrico.pomodoro.R
import com.github.nitrico.pomodoro.action.trello.EditCard
import com.github.nitrico.pomodoro.data.TrelloCard
import com.github.nitrico.pomodoro.tool.*
import kotlinx.android.synthetic.main.item_card.view.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class CardsAdapter(
        private var list: List<TrelloCard>,
        private val isTodoList: Boolean) : RecyclerView.Adapter<CardsAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(card: TrelloCard) = with (itemView) {
            name.text = card.name

            // show the card description only if it is not empty
            desc.setTextOrHideView(card.desc)

            // show the add time button only for the To do list
            timer.showIfAndHideIfNot(isTodoList)

            // show the time and pomodoros count only if they are not zero
            if (card.pomodoros != 0 || card.seconds != 0.toLong()) {
                data.show()
                pomodoros.text = card.pomodoros.toString()
                time.text = card.seconds.toTimeString()
            }
            else data.hide()

            // click listeners
            open.setOnClickListener {  context.openCard(card) }
            open.setOnLongClickListener { context.toast(R.string.open_card); true }
            edit.setOnClickListener { EditCard(context, card) }
            edit.setOnLongClickListener { context.toast(R.string.edit_card); true }
            timer.setOnLongClickListener { context.toast(R.string.start_timer); true }
            timer.setOnClickListener {
                context.startActivity<TimerActivity>(TimerActivity.KEY_CARD to card)
            }
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.item_card))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(list[position])

    override fun getItemCount() = list.size

    fun setItems(items: List<TrelloCard>) {
        list = items
        notifyDataSetChanged()
    }

}
