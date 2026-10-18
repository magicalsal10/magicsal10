package com.example.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NotificationPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("fastpulse_notification_prefs", Context.MODE_PRIVATE)

    private val _notificationsEnabled = MutableStateFlow(prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true))
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    private val _autophagyAlertEnabled = MutableStateFlow(prefs.getBoolean(KEY_AUTOPHAGY_ENABLED, true))
    val autophagyAlertEnabled: StateFlow<Boolean> = _autophagyAlertEnabled.asStateFlow()

    private val _eatingWindowAlertEnabled = MutableStateFlow(prefs.getBoolean(KEY_EATING_WINDOW_ENABLED, true))
    val eatingWindowAlertEnabled: StateFlow<Boolean> = _eatingWindowAlertEnabled.asStateFlow()

    fun setNotificationsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled).apply()
        _notificationsEnabled.value = enabled
    }

    fun setAutophagyAlertEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_AUTOPHAGY_ENABLED, enabled).apply()
        _autophagyAlertEnabled.value = enabled
    }

    fun setEatingWindowAlertEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_EATING_WINDOW_ENABLED, enabled).apply()
        _eatingWindowAlertEnabled.value = enabled
    }

    companion object {
        private const val KEY_NOTIFICATIONS_ENABLED = "key_notifications_enabled"
        private const val KEY_AUTOPHAGY_ENABLED = "key_autophagy_enabled"
        private const val KEY_EATING_WINDOW_ENABLED = "key_eating_window_enabled"
    }
}
