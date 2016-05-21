package com.github.nitrico.pomodoro.ui

import android.content.*
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
import com.github.nitrico.pomodoro.data.TrelloCard
import com.github.nitrico.pomodoro.store.TimerStore
import com.github.nitrico.pomodoro.tool.Cache
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

        val stores = listOf(TimerStore)
    }

    private val playIcon by lazy { resources.getDrawable(R.drawable.ic_play) }
    private val pauseIcon by lazy { resources.getDrawable(R.drawable.ic_pause) }

    private lateinit var card: TrelloCard
    private var pomodoroCompleted = false
    private var pomodoroSeconds: Long = 0

    override fun getStores() = stores

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        // initialize UI
        setFullScreenLayout()
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        layout.setPadding(0, 0, 0, navigationBarHeight)
        card = intent.extras.getSerializable(KEY_CARD) as TrelloCard

        text.text = App.TIME_POMODORO.toTimeString()
        name.text = card.name
        time.text = card.seconds.toTimeString()
        pomodoros.text = card.pomodoros.toString()

        fab.setOnClickListener {
            if (TimerStore.running) Pause(this)
            else {
                if (!TimerStore.paused) startTimer(App.TIME_POMODORO, false)
                else Resume(this)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.timer, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> { stop(true); true }
        R.id.stop -> { stop(false); true }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() = stop(true)

    override fun onError(error: ErrorAction) = toast("ERROR: " +error.throwable.message)

    override fun onStoreChanged(change: StoreChange) {
        when (change.store) {
            TimerStore -> when (change.action) {
                is Tick -> onTick()
                is Resume -> fab.setImageDrawable(pauseIcon)
                is Start -> fab.setImageDrawable(pauseIcon)
                is StartPomodoro -> fab.setImageDrawable(pauseIcon)
                is Pause -> fab.setImageDrawable(playIcon)
                is Stop -> fab.setImageDrawable(playIcon)
            }
        }
    }

    private fun onTick() {
        val current = TimerStore.current
        progress.setValue(current.toFloat())
        time.text = (card.seconds + current).toTimeString()
        text.text = current.toTimeString()
    }

    private fun startTimer(seconds: Long, isBreak: Boolean) {
        if (isBreak) fab.hide()
        //else Trello.moveCardToDoingList(card.id)
        val max = seconds
        progress.max = max.toFloat()
        StartPomodoro(this, card)
    }

    private fun onPomodoroCompleted() {
        pomodoroCompleted = true
        progress.setValue(progress.max)
        progress.setValue(0f)
        Cache.addPomodoro(card.id)
        text.text = "Pomodoro completed"
        fab.setImageDrawable(playIcon)
        nextAction {
            when (it) {
                ACTION_SHORT_BREAK -> startTimer(App.TIME_SHORT_BREAK, true)
                ACTION_LONG_BREAK -> startTimer(App.TIME_LONG_BREAK, true)
                ACTION_BACK -> {
                    //Trello.moveCardToTodoList(card.id)
                    finish()
                }
                ACTION_DONE -> {
                    val comment = "${card.pomodoros} pomodoros, ${card.seconds.toTimeString()} total spent"
                    AddComment(this, card.id, comment)
                    //Trello.addCommentToCard(card.id, comment)
                    //Trello.moveCardToDoneList(card.id)
                }
            }
        }
        // bottom sheet dialog
        //val f = BreakDialogFragment()
        //f.show(supportFragmentManager, resources.getString(R.string.next_action))
        //f.view?.setPadding(0, 0,  0, navigationBarHeight)
    }

    private fun onBreakCompleted() {
        progress.setValue(progress.max)
        progress.setValue(0f)
        text.text = "Break completed"
        fab.show()
        fab.setImageDrawable(playIcon)
    }

    private fun stop(andFinish: Boolean = false) {
        stopService(Intent(this, TimerService::class.java))
        progress.setValue(0f)
        //running = false
        fab.setImageDrawable(playIcon)
        text.text = App.TIME_POMODORO.toTimeString()
        if (pomodoroSeconds != 0.toLong() && !pomodoroCompleted) Cache.addTime(card.id, pomodoroSeconds)
        pomodoroSeconds = 0
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
