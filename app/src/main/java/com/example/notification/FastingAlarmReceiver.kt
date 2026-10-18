package com.example.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class FastingAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != NotificationConstants.ACTION_FASTING_REMINDER) return

        val type = intent.getStringExtra(NotificationConstants.EXTRA_REMINDER_TYPE) ?: return
        val protocolName = intent.getStringExtra(NotificationConstants.EXTRA_PROTOCOL_NAME) ?: "Fasting"
        val targetHours = intent.getFloatExtra(NotificationConstants.EXTRA_TARGET_HOURS, 16f)

        // Make sure channel is created
        FastingNotificationManager.createNotificationChannel(context)

        when (type) {
            NotificationConstants.TYPE_FAST_ENDED -> {
                FastingNotificationManager.showNotification(
                    context = context,
                    title = "Fasting Goal Reached! 🎉",
                    body = "Congratulations! You completed your $protocolName fast (${targetHours.toInt()}h). Your body has lowered insulin and burned fat reserves. You can break your fast or keep going for autophagy bonus hours!",
                    notificationId = NotificationConstants.NOTIFICATION_ID_FAST_ENDED,
                    isAutophagy = false
                )
            }
            NotificationConstants.TYPE_AUTOPHAGY_REACHED -> {
                FastingNotificationManager.showNotification(
                    context = context,
                    title = "Autophagy Zone Reached! ✨",
                    body = "16 Hours Fasted! AMPK is in full swing and your lysosomes are recycling damaged cellular components. You are now in peak cellular renewal.",
                    notificationId = NotificationConstants.NOTIFICATION_ID_AUTOPHAGY,
                    isAutophagy = true
                )
            }
            NotificationConstants.TYPE_EATING_WINDOW_ENDING -> {
                FastingNotificationManager.showNotification(
                    context = context,
                    title = "Eating Window Ending Soon ⏳",
                    body = "Your eating window is coming to a close. Wrap up meals and prepare to initiate your next $protocolName fast to sustain metabolic health.",
                    notificationId = NotificationConstants.NOTIFICATION_ID_EATING_WINDOW,
                    isAutophagy = false
                )
            }
        }
    }
}
