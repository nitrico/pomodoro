package com.github.nitrico.pomodoro.ui

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v7.app.NotificationCompat
import android.view.Menu
import android.view.MenuItem
import com.github.nitrico.pomodoro.R
import com.github.nitrico.pomodoro.data.Data
import com.github.nitrico.pomodoro.data.TrelloCard
import com.github.nitrico.pomodoro.tool.TimerService
import com.github.nitrico.pomodoro.tool.consume
import com.github.nitrico.pomodoro.tool.navigationBarHeight
import com.github.nitrico.pomodoro.tool.setFullScreenLayout
import kotlinx.android.synthetic.main.activity_timer.*

class TimerActivity : AppCompatActivity() {

    companion object {
        const val KEY_CARD = "KEY_CARD"
        const val NOTIFICATION_ID = 1990574
    }

    private var card: TrelloCard? = null
    private var currentSeconds: Long = 0

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

    private fun startTimer(seconds: Long) {
        progress.max = seconds - 0.5f
        progress.setValue(0f)
        val timer = object : CountDownTimer(seconds * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                currentSeconds = seconds - secondsLeft
                val value = currentSeconds.toFloat()
                val timeString = secondsLeft.toTimeString()
                progress.setValue(value)
                text.text = timeString
                createNotification(timeString, progress.max.toInt(), value.toInt())
            }
            override fun onFinish() {
                progress.setValue(progress.max)
                text.text = "finished!"
                // cancel the notification
                val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                nm.cancel(NOTIFICATION_ID)
                // stopService(...) ??
                // lanzar una notificación nueva con sonido ??
            }
        }
        timer.cancel()
        timer.start()
        startService(Intent(this, TimerService::class.java))
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
        android.R.id.home -> consume { exit() }
        R.id.start25m -> consume { startTimer(25*60 +1) }
        R.id.start5m -> consume { startTimer(5*60 +1) }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        exit()
        super.onBackPressed()
    }

    private fun exit() = card?.let {
        println("add $currentSeconds to card ${it.id}")
        if (currentSeconds != 0.toLong()) Data.addTime(it.id, currentSeconds)
        finish()
    }


    private fun createNotification(text: String, max: Int, progress: Int) {
        val builder = NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_timer)
                .setContentTitle("My notification")
                .setContentText(text)
                .setProgress(max, progress, false)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                //.addAction(R.drawable.ic_pause, "Pause", null/*pausePendingIntent*/)
                //.addAction(R.drawable.ic_stop, "Stop", null/*stopPendingIntent*/)
                //.setStyle(NotificationCompat.MediaStyle())
                //.setLargeIcon(albumArtBitmap)
        // Creates an explicit intent for an Activity in your app
        val resultIntent = Intent(this, TimerActivity::class.java)

        // The stack builder object will contain an artificial back stack for the started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        val stackBuilder = TaskStackBuilder.create(this)
        // Adds the back stack for the Intent (but not the Intent itself)
        //stackBuilder.addParentStack(ResultActivity.class)
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent)
        val resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setContentIntent(resultPendingIntent)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // notificationId allows you to update the notification later on.
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

}
