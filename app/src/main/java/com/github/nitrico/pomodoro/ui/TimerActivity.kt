package com.github.nitrico.pomodoro.ui

import android.content.*
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.view.Menu
import android.view.MenuItem
import com.github.nitrico.pomodoro.R
import com.github.nitrico.pomodoro.data.TrelloCard
import com.github.nitrico.pomodoro.tool.*
import kotlinx.android.synthetic.main.activity_timer.*
import org.jetbrains.anko.bundleOf

class TimerActivity : AppCompatActivity() {

    companion object {
        const val KEY_CARD = "KEY_CARD"
        const val TIME_POMODORO = 25*60
        const val TIME_LONG_BREAK = 15*60
        const val TIME_SHORT_BREAK = 5*60
    }

    private var card: TrelloCard? = null
    private var seconds: Long = 0

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            seconds = intent.extras.getLong(TimerService.KEY_TIMER_CURRENT)
            progress.setValue(seconds.toFloat())
            text.text = seconds.toTimeString()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        // initialize UI
        setFullScreenLayout()
        setSupportActionBar(toolbar)
        layout.setPadding(0, 0, 0, navigationBarHeight)
        card = intent.extras?.getSerializable(KEY_CARD) as TrelloCard?
        title = card?.name

    }

    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter(TimerService.TIMER_TICK)
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.timer, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> consume { exit() }
        R.id.start25m -> consume {
            val max = 25*60 +1
            progress.max = max-0.5f
            Intent(this, TimerService::class.java).apply {
                putExtras(bundleOf(TimerService.KEY_TIMER_TOTAL to max))
                startService(this)
            }
        }
        R.id.start5m -> consume {
            val max = 21//5*60 +1
            progress.max = max-0.5f
            Intent(this, TimerService::class.java).apply {
                putExtras(bundleOf(TimerService.KEY_TIMER_TOTAL to max))
                startService(this)
            }
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() = exit()

    private fun exit() {
        stopService(Intent(this, TimerService::class.java))
        card?.let { if (seconds != 0.toLong()) Cache.addTime(it.id, seconds) }
        finish()
    }

}
