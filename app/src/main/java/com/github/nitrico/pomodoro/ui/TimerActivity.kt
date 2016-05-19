package com.github.nitrico.pomodoro.ui

import android.content.*
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.view.Menu
import android.view.MenuItem
import com.github.nitrico.pomodoro.App
import com.github.nitrico.pomodoro.R
import com.github.nitrico.pomodoro.data.Trello
import com.github.nitrico.pomodoro.data.TrelloCard
import com.github.nitrico.pomodoro.tool.*
import kotlinx.android.synthetic.main.activity_timer.*

class TimerActivity : AppCompatActivity() {

    companion object {
        const val KEY_CARD = "KEY_CARD"
        const val ACTION_BACK = -2
        const val ACTION_DONE = -1
        const val ACTION_SHORT_BREAK = 0
        const val ACTION_LONG_BREAK = 1
    }

    /**
     * Receive messages from TimerService
     */
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.extras.getInt(TimerService.KEY_ACTION)
            when (action) {
                TimerService.ACTION_TICK -> onTick(intent)
                TimerService.ACTION_BREAK_COMPLETED -> onBreakCompleted()
                TimerService.ACTION_POMODORO_COMPLETED -> onPomodoroCompleted()
            }
        }
    }

    private val playIcon by lazy { resources.getDrawable(R.drawable.ic_play) }
    private val pauseIcon by lazy { resources.getDrawable(R.drawable.ic_pause) }

    private lateinit var card: TrelloCard
    private var pomodoroCompleted = false
    private var pomodoroSeconds: Long = 0
    private var seconds: Long = 0
    private var running = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        // initialize UI
        setFullScreenLayout()
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        layout.setPadding(0, 0, 0, navigationBarHeight)
        card = intent.extras.getSerializable(KEY_CARD) as TrelloCard
        //title = card.name

        text.text = App.TIME_POMODORO.toTimeString()
        name.text = card.name
        time.text = card.seconds.toTimeString()
        pomodoros.text = card.pomodoros.toString()

        fab.setOnClickListener {
            if (!running) {
                startTimer(App.TIME_POMODORO, false)
                fab.setImageDrawable(pauseIcon)
            } else {
                fab.setImageDrawable(playIcon)
            }
            running = !running
        }
    }

    override fun onResume() {
        super.onResume()
        // register receiver to receive messages only from TimerService
        val intentFilter = IntentFilter(TimerService.ACTIONS_FROM_TIMER)
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        // unregister receiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.timer, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> { stop(true); true }
        /*
        R.id.pomodoro -> { startTimer(App.TIME_POMODORO, false); true }
        R.id.longBreak -> { startTimer(App.TIME_LONG_BREAK, true); true }
        R.id.shortBreak -> { startTimer(App.TIME_SHORT_BREAK, true); true }
        R.id.pause -> { true }
        R.id.resume -> { true }
        */
        R.id.stop -> { stop(false); true }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() = stop(true)

    private fun startTimer(seconds: Long, isBreak: Boolean) {
        if (isBreak) fab.hide()
        else Trello.moveCardToDoingList(card.id)

        val max = seconds
        progress.max = max.toFloat()
        val intent = Intent(this, TimerService::class.java)
        stopService(intent)
        val bundle = Bundle()
        bundle.putSerializable(TimerService.KEY_CARD, card)
        bundle.putBoolean(TimerService.KEY_BREAK, isBreak)
        bundle.putLong(TimerService.KEY_TIMER_TOTAL, max)
        intent.putExtras(bundle)
        startService(intent)
    }

    private fun onTick(intent: Intent) {
        seconds = intent.extras.getLong(TimerService.KEY_TIMER_CURRENT)
        text.text = intent.extras.getString(TimerService.KEY_TIMER_TEXT)
        val isBreak = intent.extras.getBoolean(TimerService.KEY_BREAK)
        if (!isBreak) pomodoroSeconds = seconds
        progress.setValue(seconds.toFloat())
        time.text = (card.seconds+seconds).toTimeString()
    }

    private fun onPomodoroCompleted() {
        pomodoroCompleted = true
        progress.setValue(progress.max)
        Cache.addPomodoro(card.id)
        text.text = "Pomodoro completed"
        fab.setImageDrawable(playIcon)
        DialogCreator.nextAction(this) {
            when (it) {
                ACTION_SHORT_BREAK -> startTimer(App.TIME_SHORT_BREAK, true)
                ACTION_LONG_BREAK -> startTimer(App.TIME_LONG_BREAK, true)
                ACTION_BACK -> {
                    Trello.moveCardToTodoList(card.id)
                    finish()
                }
                ACTION_DONE -> {
                    val comment = "${card.pomodoros} pomodoros, ${card.seconds.toTimeString()} total spent"
                    Trello.addCommentToCard(card.id, comment)
                    Trello.moveCardToDoneList(card.id)
                }
            }
        }
    }

    private fun onBreakCompleted() {
        progress.setValue(progress.max)
        text.text = "Break completed"
        fab.show()
        fab.setImageDrawable(playIcon)
    }

    private fun stop(andFinish: Boolean = false) {
        stopService(Intent(this, TimerService::class.java))
        progress.setValue(0f)
        running = false
        fab.setImageDrawable(playIcon)
        text.text = App.TIME_POMODORO.toTimeString()
        if (pomodoroSeconds != 0.toLong() && !pomodoroCompleted) Cache.addTime(card.id, pomodoroSeconds)
        pomodoroSeconds = 0
        if (andFinish) finish()
    }

}
