package com.github.nitrico.pomodoro.tool

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.os.*
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.NotificationCompat
import com.github.nitrico.pomodoro.R
import com.github.nitrico.pomodoro.ui.TimerActivity
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.toast

class TimerService : Service() {

    companion object {
        const val KEY_CARD_NAME = "KEY_CARD_NAME"
        const val KEY_TIMER_TOTAL = "KEY_TIMER_TOTAL"
        const val KEY_TIMER_CURRENT = "KEY_TIMER_CURRENT"
        const val TIMER_TICK = "com.github.nitrico.pomodoro.TIMER_TICK"
        const val NOTIFICATION_ID = 1990574
    }

    private val nm by lazy { getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }
    private var seconds: Long = 0
    private lateinit var timer: CountDownTimer

    override fun onBind(intent: Intent) = null

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        toast("service onCreate")
        val total = 20.toLong() // cogerlo del intent de llamada de creación
        timer = object : CountDownTimer(total*1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                val timeString = secondsLeft.toTimeString()
                seconds = total - secondsLeft
                createNotification("Título", timeString, total.toInt(), seconds.toInt())
                sendLocalBroadcast(seconds)
            }
            override fun onFinish() {
                nm.cancel(NOTIFICATION_ID)
            }
        }
        timer.start()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun sendLocalBroadcast(current: Long) {
        val intent = Intent(TIMER_TICK)
        intent.putExtras(bundleOf(KEY_TIMER_CURRENT to current))
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    override fun onDestroy() {
        toast("service onDestroy")
        timer.cancel()
        nm.cancel(NOTIFICATION_ID)
        super.onDestroy()
    }

    private fun createNotification(title: String, text: String, max: Int, progress: Int) {
        val builder = NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_timer)
                .setContentTitle(title)
                .setContentText(text)
                .setProgress(max, progress, false)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        val resultIntent = Intent(this, TimerActivity::class.java)
        val stackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addNextIntent(resultIntent)
        val resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setContentIntent(resultPendingIntent)
        nm.notify(NOTIFICATION_ID, builder.build())
    }

}
