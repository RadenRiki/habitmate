package com.example.habitmate.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.util.Calendar

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // Show notification
        NotificationHelper.showReminderNotification(context)

        // Reschedule for tomorrow
        val prefs = context.getSharedPreferences("habit_settings", Context.MODE_PRIVATE)
        val hour = prefs.getInt("notification_hour", 9)
        val minute = prefs.getInt("notification_minute", 0)
        scheduleNotification(context, hour, minute)
    }

    companion object {
        private const val REQUEST_CODE = 12345

        fun scheduleNotification(context: Context, hour: Int, minute: Int) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val intent = Intent(context, NotificationReceiver::class.java)
            val pendingIntent =
                    PendingIntent.getBroadcast(
                            context,
                            REQUEST_CODE,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )

            // Set time for notification
            val calendar =
                    Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, hour)
                        set(Calendar.MINUTE, minute)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)

                        // If time has passed today, schedule for tomorrow
                        if (timeInMillis <= System.currentTimeMillis()) {
                            add(Calendar.DAY_OF_YEAR, 1)
                        }
                    }

            // Use setExactAndAllowWhileIdle for more reliable delivery
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
            )
        }

        fun cancelNotification(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val intent = Intent(context, NotificationReceiver::class.java)
            val pendingIntent =
                    PendingIntent.getBroadcast(
                            context,
                            REQUEST_CODE,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )

            alarmManager.cancel(pendingIntent)
        }
    }
}
