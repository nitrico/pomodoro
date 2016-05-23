package com.github.nitrico.pomodoro.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.afollestad.materialdialogs.MaterialDialog
import com.github.nitrico.flux.action.ErrorAction
import com.github.nitrico.flux.store.StoreChange
import com.github.nitrico.pomodoro.App
import com.github.nitrico.pomodoro.R
import com.github.nitrico.pomodoro.action.timer.*
import com.github.nitrico.pomodoro.action.trello.AddComment
import com.github.nitrico.pomodoro.action.trello.MoveCard
import com.github.nitrico.pomodoro.data.TrelloCard
import com.github.nitrico.pomodoro.store.TimerStore
import com.github.nitrico.pomodoro.store.TrelloStore
import com.github.nitrico.pomodoro.tool.*
import kotlinx.android.synthetic.main.activity_timer.*
import org.jetbrains.anko.toast

class TimerActivity : FluxActivity() {

    companion object {
        const val KEY_CARD = "KEY_CARD"
        const val ACTION_BACK = -2
        const val ACTION_DONE = -1
        const val ACTION_SHORT_BREAK = 0
        const val ACTION_LONG_BREAK = 1
    }

    private val playIcon by lazy { resources.getDrawable(R.drawable.ic_play) }
    private val pauseIcon by lazy { resources.getDrawable(R.drawable.ic_pause) }

    private lateinit var stopButton: MenuItem
    private lateinit var card: TrelloCard

    override fun getStores() = listOf(TimerStore)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        setFullScreenLayout()
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        setTaskDescription(R.drawable.ic_main)

        layout.setPadding(0, 0, 0, navigationBarHeight)
        card = intent.extras.getSerializable(KEY_CARD) as TrelloCard

        text.text = App.TIME_POMODORO.toTimeString()
        name.text = card.name
        time.text = card.seconds.toTimeString()
        pomodoros.text = card.pomodoros.toString()

        fab.setOnClickListener {
            if (TimerStore.isRunning) Pause(this)
            else {
                if (!TimerStore.isPaused) startTimer(App.TIME_POMODORO, false)
                else Resume(this)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.timer, menu)
        stopButton = menu.findItem(R.id.stop)
        stopButton.isVisible = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> { stopTimer(true); true }
        R.id.stop -> { stopTimer(); true }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() = stopTimer(true)

    override fun onError(error: ErrorAction) = toast("${error.action} # ${error.throwable.message}")

    override fun onStoreChanged(change: StoreChange) {
        when (change.store) {
            TimerStore -> when (change.action) {

                is Resume -> fab.setImageDrawable(pauseIcon)

                is Start -> fab.setImageDrawable(pauseIcon)

                is Pause -> fab.setImageDrawable(playIcon)

                is Tick -> {
                    val current = TimerStore.current
                    val left = TimerStore.total - current
                    progress.max = TimerStore.total.toFloat()
                    progress.setValue(current.toFloat())
                    text.text = left.toTimeString()
                    if (!TimerStore.isBreak) time.text = (card.seconds + current).toTimeString()
                }

                is Finish -> {
                    progress.setValue(0f)
                    fab.show()
                    fab.setImageDrawable(playIcon)
                    text.text = App.TIME_POMODORO.toTimeString()
                    stopButton.isVisible = false
                    pomodoros.text = card.pomodoros.toString()

                    if (!TimerStore.isBreak) nextAction {
                        when (it) {
                            ACTION_SHORT_BREAK -> startTimer(App.TIME_SHORT_BREAK, true)
                            ACTION_LONG_BREAK -> startTimer(App.TIME_LONG_BREAK, true)
                            ACTION_BACK -> {
                                //MoveCard(card.id, TrelloStore.todoListId!!)
                                finish()
                            }
                            ACTION_DONE -> {
                                val comment = "${card.pomodoros} pomodoros, " +
                                        "${card.seconds.toTimeString()} total spent"
                                AddComment(card.id, comment)
                                MoveCard(card.id, TrelloStore.doneListId!!)
                                finish()
                            }
                        }
                    }
                }

                is Stop -> {
                    stopButton.isVisible = TimerStore.current != 0.toLong() && !TimerStore.isBreak
                    fab.setImageDrawable(playIcon)
                    progress.setValue(0f)
                    text.text = App.TIME_POMODORO.toTimeString()
                }

            }
        }
    }

    private fun startTimer(time: Long, isBreak: Boolean) {
        stopButton.isVisible = true
        if (isBreak) fab.hide()
        //else MoveCard(card.id, TrelloStore.doingListId!!)

        progress.max = time.toFloat()
        progress.setValue(0f)
        Start(this, card,  time)
    }

    private fun stopTimer(andFinish: Boolean = false) {
        if (TimerStore.isBreak) fab.show()
        Stop(this, card)
        if (andFinish) finish()
    }

    private fun nextAction(callback: ((Int) -> Unit)? = null) = MaterialDialog.Builder(this)
            .items(R.array.actions)
            .title(R.string.next_action)
            .positiveText(R.string.card_done)
            .negativeText(R.string.back)
            .negativeColor(R.color.black)
            .onPositive { dialog, dialogAction -> callback?.invoke(ACTION_DONE) }
            .onNegative { dialog, dialogAction -> callback?.invoke(ACTION_BACK) }
            .itemsCallback { dialog, itemView, which, text -> callback?.invoke(which) }
            .show()

}
