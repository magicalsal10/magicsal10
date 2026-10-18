package com.example.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.example.data.NotificationPreferences
import com.example.data.entity.FastingSession
import com.example.model.FastingProtocol

object FastingNotificationManager {

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NotificationConstants.CHANNEL_ID,
                NotificationConstants.CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = NotificationConstants.CHANNEL_DESCRIPTION
                enableLights(true)
                enableVibration(true)
                setShowBadge(true)
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Schedules all relevant alarms for an active fasting session.
     */
    fun scheduleFastingReminders(context: Context, session: FastingSession) {
        val prefs = NotificationPreferences(context)
        if (!prefs.notificationsEnabled.value) return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val now = System.currentTimeMillis()

        // 1. Target Fasting Goal Completion Reminder
        if (session.targetEndTimeMillis > now) {
            val intent = Intent(context, FastingAlarmReceiver::class.java).apply {
                action = NotificationConstants.ACTION_FASTING_REMINDER
                putExtra(NotificationConstants.EXTRA_REMINDER_TYPE, NotificationConstants.TYPE_FAST_ENDED)
                putExtra(NotificationConstants.EXTRA_PROTOCOL_NAME, session.protocolName)
                putExtra(NotificationConstants.EXTRA_TARGET_HOURS, session.targetFastingHours)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                NotificationConstants.REQUEST_CODE_FAST_ENDED,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            setAlarm(alarmManager, session.targetEndTimeMillis, pendingIntent)
        }

        // 2. Autophagy 16h Milestone Alert (if session is 16h or longer)
        if (prefs.autophagyAlertEnabled.value && session.targetFastingHours >= 16f) {
            val autophagyTime = session.startTimeMillis + (16L * 3600 * 1000)
            if (autophagyTime > now) {
                val intent = Intent(context, FastingAlarmReceiver::class.java).apply {
                    action = NotificationConstants.ACTION_FASTING_REMINDER
                    putExtra(NotificationConstants.EXTRA_REMINDER_TYPE, NotificationConstants.TYPE_AUTOPHAGY_REACHED)
                    putExtra(NotificationConstants.EXTRA_PROTOCOL_NAME, session.protocolName)
                    putExtra(NotificationConstants.EXTRA_TARGET_HOURS, session.targetFastingHours)
                }

                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    NotificationConstants.REQUEST_CODE_AUTOPHAGY,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                setAlarm(alarmManager, autophagyTime, pendingIntent)
            }
        }
    }

    /**
     * Schedules a reminder when the user's eating window is ending and next fast begins.
     */
    fun scheduleEatingWindowEndReminder(
        context: Context,
        eatingEndTimestampMillis: Long,
        protocol: FastingProtocol
    ) {
        val prefs = NotificationPreferences(context)
        if (!prefs.notificationsEnabled.value || !prefs.eatingWindowAlertEnabled.value) return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val now = System.currentTimeMillis()

        if (eatingEndTimestampMillis > now) {
            val intent = Intent(context, FastingAlarmReceiver::class.java).apply {
                action = NotificationConstants.ACTION_FASTING_REMINDER
                putExtra(NotificationConstants.EXTRA_REMINDER_TYPE, NotificationConstants.TYPE_EATING_WINDOW_ENDING)
                putExtra(NotificationConstants.EXTRA_PROTOCOL_NAME, protocol.title)
                putExtra(NotificationConstants.EXTRA_TARGET_HOURS, protocol.fastingHours)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                NotificationConstants.REQUEST_CODE_EATING_WINDOW,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            setAlarm(alarmManager, eatingEndTimestampMillis, pendingIntent)
        }
    }

    /**
     * Cancels any pending fasting alarms.
     */
    fun cancelFastingReminders(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        val fastEndedIntent = Intent(context, FastingAlarmReceiver::class.java).apply {
            action = NotificationConstants.ACTION_FASTING_REMINDER
        }
        val fastEndedPending = PendingIntent.getBroadcast(
            context,
            NotificationConstants.REQUEST_CODE_FAST_ENDED,
            fastEndedIntent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        fastEndedPending?.let {
            alarmManager.cancel(it)
            it.cancel()
        }

        val autophagyPending = PendingIntent.getBroadcast(
            context,
            NotificationConstants.REQUEST_CODE_AUTOPHAGY,
            fastEndedIntent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        autophagyPending?.let {
            alarmManager.cancel(it)
            it.cancel()
        }
    }

    /**
     * Displays an immediate notification.
     */
    fun showNotification(
        context: Context,
        title: String,
        body: String,
        notificationId: Int,
        isAutophagy: Boolean = false
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Accent color: Gold for autophagy, Teal for fasting milestones
        val accentColor = if (isAutophagy) {
            ContextCompat.getColor(context, android.R.color.holo_orange_dark)
        } else {
            ContextCompat.getColor(context, android.R.color.holo_green_dark)
        }

        val notification = NotificationCompat.Builder(context, NotificationConstants.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setColor(accentColor)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notification)
    }

    private fun setAlarm(alarmManager: AlarmManager, triggerAtMillis: Long, pendingIntent: PendingIntent) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
                } else {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            }
        } catch (e: SecurityException) {
            // Graceful fallback to non-exact while idle
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        }
    }
}
