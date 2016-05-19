package com.github.nitrico.pomodoro.tool

import android.app.*
import android.content.Intent
import android.os.*
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.content.LocalBroadcastManager
import com.github.nitrico.pomodoro.R
import com.github.nitrico.pomodoro.data.TrelloCard
import com.github.nitrico.pomodoro.ui.TimerActivity
import org.jetbrains.anko.bundleOf

class TimerService : Service() {

    companion object {
        const val KEY_ACTION = "KEY_ACTION"
        const val KEY_CARD = "KEY_CARD"
        const val KEY_BREAK = "KEY_BREAK"
        const val KEY_TIMER_TEXT = "KEY_TIMER_TEXT"
        const val KEY_TIMER_TOTAL = "KEY_TIMER_TOTAL"
        const val KEY_TIMER_CURRENT = "KEY_TIMER_CURRENT"

        const val ACTIONS_FROM_TIMER = "com.github.nitrico.pomodoro.ACTIONS_FROM_TIMER"
        const val ACTION_TICK = 0
        const val ACTION_POMODORO_COMPLETED = 1
        const val ACTION_BREAK_COMPLETED = 2

        const val ID_NOTIFICATION_PROGRESS = 0
        const val ID_NOTIFICATION_COMPLETED = 1
    }

    private lateinit var timer: CountDownTimer
    private lateinit var card: TrelloCard
    private var seconds: Long = 0
    private var isBreak = false

    override fun onBind(intent: Intent) = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) {
            stopSelf()
            return super.onStartCommand(intent, flags, startId)
        }
        val total = intent.extras.getLong(KEY_TIMER_TOTAL)
        isBreak = intent.extras.getBoolean(KEY_BREAK)
        card = intent.extras.getSerializable(KEY_CARD) as TrelloCard

        timer = object : com.github.nitrico.pomodoro.tool.CountDownTimer(total*1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                val timeString = secondsLeft.toTimeString()
                seconds = total - secondsLeft
                createProgressNotification(card.name, timeString, total.toInt(), seconds.toInt())
                sendTick(seconds, timeString, isBreak)
            }
            override fun onFinish() {
                NotificationManagerCompat.from(this@TimerService).cancel(ID_NOTIFICATION_PROGRESS)
                if (isBreak) {
                    createNotification("Break completed!", card.name)
                    sendBreakCompleted()
                } else {
                    createNotification("Pomodoro completed!", card.name)
                    sendPomodoroCompleted()
                }
            }
        }
        timer.start()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun sendTick(current: Long, text: String, isBreak: Boolean) {
        val bundle = Bundle()
        bundle.putInt(KEY_ACTION, ACTION_TICK)
        bundle.putLong(KEY_TIMER_CURRENT, current)
        bundle.putString(KEY_TIMER_TEXT, text)
        bundle.putBoolean(KEY_BREAK, isBreak)
        sendBroadcast(bundle)
    }

    private fun sendPomodoroCompleted() {
        val bundle = Bundle()
        bundle.putInt(KEY_ACTION, ACTION_POMODORO_COMPLETED)
        sendBroadcast(bundle, true)
    }

    private fun sendBreakCompleted() {
        val bundle = Bundle()
        bundle.putInt(KEY_ACTION, ACTION_BREAK_COMPLETED)
        sendBroadcast(bundle, true)
    }

    private fun sendBroadcast(bundle: Bundle, andStop: Boolean = false) {
        val intent = Intent(ACTIONS_FROM_TIMER)
        intent.putExtras(bundle)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        if (andStop) stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
        NotificationManagerCompat.from(this).cancel(ID_NOTIFICATION_PROGRESS)
    }

    private fun createNotification(title: String, text: String) {
        val builder = NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_pomodoro)
                .setContentTitle(title)
                .setContentText(text)
                .setTicker(title)
                .setSubText(text)
                .setColor(resources.getColor(R.color.primary))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true) // disappear when clicked
        notify(ID_NOTIFICATION_COMPLETED, builder)
    }

    private fun createProgressNotification(title: String, text: String, max: Int, progress: Int) {
        val builder = NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_pomodoro)
                .setContentTitle(title)
                .setContentText(text)
                .setSubText(if (isBreak) "Break running" else "Pomodoro running")
                .setColor(resources.getColor(R.color.primary))
                .setProgress(max, progress, false)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setCategory(NotificationCompat.CATEGORY_PROGRESS)
                .setOngoing(true) // to avoid swipe to dismiss
                //.addAction(R.drawable.ic_pause, "Pause", null)
                //.addAction(R.drawable.ic_stop, "Stop", null)
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
