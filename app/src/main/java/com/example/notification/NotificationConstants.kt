package com.example.notification

object NotificationConstants {
    const val CHANNEL_ID = "fastpulse_reminders_channel"
    const val CHANNEL_NAME = "Fasting & Autophagy Alerts"
    const val CHANNEL_DESCRIPTION = "Alerts for when your fasting goal is reached, autophagy begins, or your eating window closes."

    const val ACTION_FASTING_REMINDER = "com.example.ACTION_FASTING_REMINDER"

    const val EXTRA_REMINDER_TYPE = "EXTRA_REMINDER_TYPE"
    const val EXTRA_PROTOCOL_NAME = "EXTRA_PROTOCOL_NAME"
    const val EXTRA_TARGET_HOURS = "EXTRA_TARGET_HOURS"

    const val TYPE_FAST_ENDED = "TYPE_FAST_ENDED"
    const val TYPE_AUTOPHAGY_REACHED = "TYPE_AUTOPHAGY_REACHED"
    const val TYPE_EATING_WINDOW_ENDING = "TYPE_EATING_WINDOW_ENDING"

    const val NOTIFICATION_ID_FAST_ENDED = 1001
    const val NOTIFICATION_ID_AUTOPHAGY = 1002
    const val NOTIFICATION_ID_EATING_WINDOW = 1003
    const val NOTIFICATION_ID_TEST = 1004

    const val REQUEST_CODE_FAST_ENDED = 2001
    const val REQUEST_CODE_AUTOPHAGY = 2002
    const val REQUEST_CODE_EATING_WINDOW = 2003
}
