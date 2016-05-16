package com.github.nitrico.pomodoro.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Menu
import android.view.MenuItem
import com.github.nitrico.pomodoro.R
import com.github.nitrico.pomodoro.tool.navigationBarHeight
import com.github.nitrico.pomodoro.tool.navigationBarWidth
import kotlinx.android.synthetic.main.activity_timer.*

class TimerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)
        setSupportActionBar(toolbar)
        layout.setPadding(0, 0, navigationBarWidth, navigationBarHeight)
    }

    private fun startTimer(seconds: Long) {
        progress.max = seconds - 0.5f
        progress.setValue(0f)
        val timer = object : CountDownTimer(seconds * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                progress.setValue((seconds - secondsLeft).toFloat())
                text.text = secondsLeft.toTimeString()
            }
            override fun onFinish() {
                progress.setValue(progress.max)
                text.text = "finished!"
            }
        }
        timer.cancel()
        timer.start()
    }

    fun Long.toTimeString(): String {
        if (this < 60) return "$this s"
        else {
            val minutes: Long = (this / 60)
            val seconds: Long = this - (minutes * 60)
            return "$minutes:${seconds.toTwoDigitsString()}"
        }
    }

    fun Long.toTwoDigitsString() = String.format("%02d", this)

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.timer, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.start25m -> { startTimer(25*60 +1); true }
        R.id.start5m -> { startTimer(5*60 +1); true }
        else -> super.onOptionsItemSelected(item)
    }

}
