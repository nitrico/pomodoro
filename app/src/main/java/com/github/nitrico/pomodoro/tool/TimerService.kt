package com.github.nitrico.pomodoro.tool

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.app.TaskStackBuilder
import android.support.v4.content.ContextCompat
import com.github.nitrico.pomodoro.App
import com.github.nitrico.pomodoro.R
import com.github.nitrico.pomodoro.action.timer.*
import com.github.nitrico.pomodoro.data.TrelloCard
import com.github.nitrico.pomodoro.ui.TimerActivity
import org.jetbrains.anko.bundleOf

class TimerService : Service() {

    companion object {
        const val KEY_CARD = "KEY_CARD"
        const val KEY_TIME = "KEY_TIME"
        const val KEY_ACTION = "KEY_ACTION"
        const val ACTION_START = 0
        const val ACTION_STOP = 1
        const val ACTION_PAUSE = 2
        const val ACTION_RESUME = 3
        const val ID_NOTIFICATION_PROGRESS = 0
        const val ID_NOTIFICATION_FINISHED = 1
    }

    private lateinit var card: TrelloCard
    private var timer: Timer? = null
    private var time: Long = 0
    private var isBreak: Boolean = false

    override fun onBind(intent: Intent?) = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) {
            stopSelf()
            return super.onStartCommand(intent, flags, startId)
        }
        when (intent.extras.getInt(KEY_ACTION)) {
            ACTION_START  -> start(intent)
            ACTION_PAUSE  -> timer?.pause()
            ACTION_RESUME -> timer?.resume()
            ACTION_STOP   -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start(intent: Intent) {
        card = intent.extras.getSerializable(KEY_CARD) as TrelloCard
        time = intent.extras.getLong(KEY_TIME)
        isBreak = time != App.TIME_POMODORO

        timer = object : Timer(time * 1000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                val left = millisUntilFinished/1000
                Tick(left)
                val progress = time-left
                notifyProgress(card.name, left.toTimeString(), time.toInt(), progress.toInt())
            }

            override fun onFinish() {
                notifyProgress(card.name, 0L.toTimeString(), time.toInt(), time.toInt())
                val timeToAdd = if (isBreak) 0 else time
                Finish(card, timeToAdd)
                NotificationManagerCompat.from(this@TimerService).cancel(ID_NOTIFICATION_PROGRESS)
                val title = getString(if (isBreak) R.string.break_completed else R.string.pomodoro_completed)
                notifyFinished(title, card.name)
                stopSelf()
            }

        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        NotificationManagerCompat.from(this).cancel(ID_NOTIFICATION_PROGRESS)
    }

    private fun stop() {
        timer?.cancel()
        stopSelf()
    }

    private fun notifyFinished(title: String, text: String) {
        val builder = NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_pomodoro)
                .setContentTitle(title)
                .setContentText(text)
                .setTicker(title)
                .setColor(ContextCompat.getColor(this, R.color.primary))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true) // disappear when clicked
        notify(ID_NOTIFICATION_FINISHED, builder)
    }

    private fun notifyProgress(title: String, text: String, max: Int, progress: Int) {
        val subText = getString(if (isBreak) R.string.break_time else R.string.pomodoro_time)
        val builder = NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_pomodoro)
                .setContentTitle(title)
                .setContentText(text)
                .setSubText(subText)
                .setColor(ContextCompat.getColor(this, R.color.primary))
                .setProgress(max, progress, false)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setCategory(NotificationCompat.CATEGORY_PROGRESS)
                .setOngoing(true) // to avoid swipe to dismiss
        notify(ID_NOTIFICATION_PROGRESS, builder)
    }

    private fun notify(notificationId: Int, builder: NotificationCompat.Builder) {
        val intent = Intent(this, TimerActivity::class.java)
        intent.putExtras(bundleOf(TimerActivity.KEY_CARD to card))
        val stackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addNextIntent(intent)
        val pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setContentIntent(pendingIntent)
        NotificationManagerCompat.from(this).notify(notificationId, builder.build())
    }

}
