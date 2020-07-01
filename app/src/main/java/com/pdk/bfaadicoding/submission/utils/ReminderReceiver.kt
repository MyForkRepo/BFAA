package com.pdk.bfaadicoding.submission.utils

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.pdk.bfaadicoding.submission.R
import com.pdk.bfaadicoding.submission.ui.activities.MainActivity
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ReminderReceiver : BroadcastReceiver() {

    companion object {
        private const val REMINDER_CODE = 100
    }

    override fun onReceive(context: Context, intent: Intent) {
        showNotification(context)
    }

    fun isReminder(context: Context): Boolean {
        val intent = Intent(context, ReminderReceiver::class.java)
        return PendingIntent.getBroadcast(
            context,
            REMINDER_CODE,
            intent,
            PendingIntent.FLAG_NO_CREATE
        ) != null
    }

    fun setReminder(context: Context) {

        if (isReminder(context)) {
            cancelReminder(context)
        } else {
            activatedReminder(context)
        }
    }

    private fun cancelReminder(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, REMINDER_CODE, intent, 0)
        pendingIntent.cancel()
        alarmManager.cancel(pendingIntent)
        Toast.makeText(
            context,
            context.resources.getString(R.string.reminder_off),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun activatedReminder(context: Context) {
        val time = context.resources.getString(R.string.reminder_time)
        if (isDateInvalid(time)) return
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)
        val timeArray = time.split(":".toRegex())
            .dropLastWhile { it.isEmpty() }.toTypedArray()
        val calendar = Calendar.getInstance()
        calendar.apply {
            set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0]))
            set(Calendar.MINUTE, Integer.parseInt(timeArray[1]))
            set(Calendar.SECOND, 0)
        }
        val pendingIntent = PendingIntent.getBroadcast(context, REMINDER_CODE, intent, 0)
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
        Toast.makeText(context, R.string.reminder_on, Toast.LENGTH_SHORT).show()
    }

    private fun isDateInvalid(date: String): Boolean {
        return try {
            val df = SimpleDateFormat("HH:mm", Locale.getDefault())
            df.isLenient = false
            df.parse(date)
            false
        } catch (e: ParseException) {
            true
        }
    }

    private fun showNotification(context: Context) {
        val channelId = "reminder_chanel"
        val channelName = "Reminder Github User"
        val notificationManagerCompat =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationIntent = Intent(context, MainActivity::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_SINGLE_TOP
        val intent =
            PendingIntent.getActivity(context, 0, notificationIntent, 0)
        val builder = NotificationCompat.Builder(context, channelId)
            .setContentIntent(intent)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle(channelName)
            .setContentText(context.resources.getString(R.string.reminder_msg))
            .setColor(ContextCompat.getColor(context, android.R.color.transparent))
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            .setSound(alarmSound)
            .setAutoCancel(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(1000, 1000, 1000, 1000, 1000)
            builder.setChannelId(channelId)
            notificationManagerCompat.createNotificationChannel(channel)
        }
        val notification = builder.build()
        notificationManagerCompat.notify(REMINDER_CODE, notification)
    }
}