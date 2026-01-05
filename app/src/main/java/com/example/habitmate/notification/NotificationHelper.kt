package com.example.habitmate.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.habitmate.MainActivity
import com.example.habitmate.R

object NotificationHelper {

    private const val CHANNEL_ID = "habit_reminder_channel"
    private const val CHANNEL_NAME = "Habit Reminders"
    private const val NOTIFICATION_ID = 1001

    // Generic motivational messages
    private val messages =
            listOf(
                    "Time to check your habits! 🎯",
                    "Don't forget to complete your habits today! 💪",
                    "Your habits are waiting for you! ✨",
                    "Stay consistent - check your habits! 🔥",
                    "Build your streak today! 🚀"
            )

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel =
                    NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                        description = "Daily reminders to complete your habits"
                    }

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showReminderNotification(context: Context) {
        // Check permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        // Create intent to open app
        val intent =
                Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }

        val pendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

        // Pick random message
        val message = messages.random()

        val notification =
                NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("HabitMate")
                        .setContentText(message)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
    }
}
