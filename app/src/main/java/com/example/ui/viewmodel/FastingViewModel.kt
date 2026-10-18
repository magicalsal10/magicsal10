package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.entity.FastingSession
import com.example.data.entity.WeightEntry
import com.example.data.repository.FastingRepository
import com.example.data.repository.WeightRepository
import com.example.model.FastingProtocol
import com.example.model.MetabolicStage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FastingViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val fastingRepository = FastingRepository(database.fastingDao())
    private val weightRepository = WeightRepository(database.weightDao())
    private val waterRepository = com.example.data.repository.WaterRepository(database.waterDao())
    private val notificationPrefs = com.example.data.NotificationPreferences(application)

    // Notification State
    val notificationsEnabled: StateFlow<Boolean> = notificationPrefs.notificationsEnabled
    val autophagyAlertEnabled: StateFlow<Boolean> = notificationPrefs.autophagyAlertEnabled
    val eatingWindowAlertEnabled: StateFlow<Boolean> = notificationPrefs.eatingWindowAlertEnabled

    // UI Preferences
    private val _isMetric = MutableStateFlow(false) // false = lbs, true = kg
    val isMetric: StateFlow<Boolean> = _isMetric.asStateFlow()

    private val _weightUnit = MutableStateFlow(WeightUnit.LBS)
    val weightUnit: StateFlow<WeightUnit> = _weightUnit.asStateFlow()

    private val _waterGoalMl = MutableStateFlow(2500)
    val waterGoalMl: StateFlow<Int> = _waterGoalMl.asStateFlow()

    private val _waterUnit = MutableStateFlow(WaterUnit.ML)
    val waterUnit: StateFlow<WaterUnit> = _waterUnit.asStateFlow()

    private val _chartRange = MutableStateFlow(ChartRange.MONTH)
    val chartRange: StateFlow<ChartRange> = _chartRange.asStateFlow()

    private val _selectedProtocol = MutableStateFlow(FastingProtocol.LEANGAINS_16_8)
    val selectedProtocol: StateFlow<FastingProtocol> = _selectedProtocol.asStateFlow()

    private val _customHours = MutableStateFlow(16f)
    val customHours: StateFlow<Float> = _customHours.asStateFlow()

    // Clock ticker for active countdown/count-up
    private val _nowMillis = MutableStateFlow(System.currentTimeMillis())

    // Database reactive streams
    val activeSession: StateFlow<FastingSession?> = fastingRepository.activeSession
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val completedSessions: StateFlow<List<FastingSession>> = fastingRepository.completedSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayWaterLogs: StateFlow<List<com.example.data.entity.WaterLogEntry>> = waterRepository.getTodayLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dailyWaterState: StateFlow<DailyWaterState> = combine(
        todayWaterLogs,
        _waterGoalMl,
        _waterUnit
    ) { logs, goal, unit ->
        val totalMl = logs.sumOf { it.amountMl }
        val progress = if (goal > 0) (totalMl.toFloat() / goal.toFloat()) else 0f
        val remaining = (goal - totalMl).coerceAtLeast(0)
        DailyWaterState(
            goalMl = goal,
            currentIntakeMl = totalMl,
            progress = progress,
            remainingMl = remaining,
            logs = logs,
            isGoalReached = totalMl >= goal,
            unit = unit
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DailyWaterState())

    val allWeightsDesc: StateFlow<List<WeightEntry>> = weightRepository.allWeightsDesc
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val latestWeight: StateFlow<WeightEntry?> = weightRepository.latestWeight
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Derived Timer UI State
    val timerUiState: StateFlow<FastingTimerUiState> = combine(
        activeSession,
        _nowMillis,
        _selectedProtocol,
        _customHours
    ) { session, now, protocol, customH ->
        if (session != null && session.isActive) {
            val elapsed = (now - session.startTimeMillis).coerceAtLeast(0L)
            val totalTargetMillis = (session.targetFastingHours * 3600 * 1000).toLong()
            val remaining = (session.targetEndTimeMillis - now).coerceAtLeast(0L)
            val isOvertime = now > session.targetEndTimeMillis
            val overtime = if (isOvertime) now - session.targetEndTimeMillis else 0L
            val progress = if (totalTargetMillis > 0) {
                elapsed.toFloat() / totalTargetMillis.toFloat()
            } else 0f

            val stage = MetabolicStage.fromElapsedMillis(elapsed)
            val stageProg = MetabolicStage.getStageProgress(elapsed, stage)
            val toNext = MetabolicStage.getMillisToNextStage(elapsed)

            val matchedProtocol = FastingProtocol.entries.firstOrNull { it.id == session.protocolId }
                ?: FastingProtocol.CUSTOM

            FastingTimerUiState(
                isFasting = true,
                session = session,
                elapsedMillis = elapsed,
                remainingMillis = remaining,
                progress = progress,
                isOvertime = isOvertime,
                overtimeMillis = overtime,
                currentStage = stage,
                stageProgress = stageProg,
                millisToNextStage = toNext,
                selectedProtocol = matchedProtocol,
                customHours = session.targetFastingHours
            )
        } else {
            // Idle / Eating window state
            FastingTimerUiState(
                isFasting = false,
                session = null,
                elapsedMillis = 0L,
                remainingMillis = 0L,
                progress = 0f,
                isOvertime = false,
                overtimeMillis = 0L,
                currentStage = MetabolicStage.ANABOLIC,
                stageProgress = 0f,
                millisToNextStage = null,
                selectedProtocol = protocol,
                customHours = customH
            )
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        FastingTimerUiState()
    )

    // Derived 7-Day Moving Average for the latest weight
    val sevenDayMovingAverage: StateFlow<Float?> = combine(
        allWeightsDesc,
        _weightUnit
    ) { weights, unit ->
        if (weights.isEmpty()) return@combine null
        val latestTimestamp = weights.first().timestampMillis
        val sevenDaysPrior = latestTimestamp - (7L * 24 * 3600 * 1000)
        val recentWindow = weights.filter { it.timestampMillis in sevenDaysPrior..latestTimestamp }
        if (recentWindow.isEmpty()) return@combine null

        val avgKg = recentWindow.map { it.weightKg }.average().toFloat()
        if (unit == WeightUnit.LBS) avgKg / 0.45359237f else avgKg
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Derived Weight Chart Points based on selected chart range
    val weightChartData: StateFlow<List<WeightChartPoint>> = combine(
        weightRepository.allWeightsAsc,
        _weightUnit,
        _chartRange
    ) { allAsc, unit, range ->
        if (allAsc.isEmpty()) return@combine emptyList()

        val now = System.currentTimeMillis()
        val rangeCutoff = if (range.days == Int.MAX_VALUE) {
            0L
        } else {
            now - (range.days.toLong() * 24 * 3600 * 1000)
        }

        val filtered = allAsc.filter { it.timestampMillis >= rangeCutoff }
        val listToProcess = if (filtered.isEmpty()) allAsc.takeLast(7) else filtered

        val dateFormat = SimpleDateFormat("MMM d", Locale.getDefault())

        listToProcess.map { entry ->
            val weightVal = if (unit == WeightUnit.LBS) entry.weightLbs else entry.weightKg

            // Compute 7-day rolling average up to this entry's timestamp
            val weekStart = entry.timestampMillis - (7L * 24 * 3600 * 1000)
            val window = allAsc.filter { it.timestampMillis in weekStart..entry.timestampMillis }
            val rollingAvg = if (window.isNotEmpty()) {
                val avgKg = window.map { it.weightKg }.average().toFloat()
                if (unit == WeightUnit.LBS) avgKg / 0.45359237f else avgKg
            } else null

            WeightChartPoint(
                timestampMillis = entry.timestampMillis,
                rawWeight = weightVal,
                rollingAverage = rollingAvg,
                dateLabel = dateFormat.format(Date(entry.timestampMillis))
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Weekly analytics for Fasting & Autophagy Zone
    val weeklyStats: StateFlow<WeeklyFastingStats> = completedSessions.combine(_nowMillis) { sessions, now ->
        val oneWeekAgo = now - (7L * 24 * 3600 * 1000)
        val pastWeekSessions = sessions.filter { it.startTimeMillis >= oneWeekAgo }

        var totalHours = 0f
        var autophagyHours = 0f
        var maxDuration = 0f

        pastWeekSessions.forEach { s ->
            val dur = s.durationHours
            totalHours += dur
            autophagyHours += s.autophagyZoneHours
            if (dur > maxDuration) maxDuration = dur
        }

        val count = pastWeekSessions.size
        val avgHours = if (count > 0) totalHours / count else 0f

        WeeklyFastingStats(
            totalFastedHours = totalHours,
            autophagyZoneHours = autophagyHours,
            averageFastHours = avgHours,
            completedFastsCount = count,
            longestFastHours = maxDuration
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), WeeklyFastingStats())

    init {
        // Continuous ticker loop (every 1 second)
        viewModelScope.launch {
            while (true) {
                delay(1000L)
                _nowMillis.value = System.currentTimeMillis()
            }
        }

        // Populate sample data on first launch if database is clean
        viewModelScope.launch {
            seedSampleDataIfEmpty()
        }
    }

    private suspend fun seedSampleDataIfEmpty() {
        val existing = database.weightDao().getAllWeightsAsc().firstOrNull()
        if (existing.isNullOrEmpty()) {
            val now = System.currentTimeMillis()
            val dayMs = 24 * 3600 * 1000L
            // Seed 14 days of weight entries showing realistic fluctuations and downward trend
            val baseKg = 81.5f
            val sampleDeltas = listOf(-0.1f, +0.3f, -0.2f, -0.4f, +0.1f, -0.3f, -0.1f, -0.5f, +0.2f, -0.3f, -0.2f, +0.1f, -0.4f, -0.2f)
            var current = baseKg
            sampleDeltas.forEachIndexed { index, delta ->
                current += delta
                val time = now - ((sampleDeltas.size - 1 - index) * dayMs)
                weightRepository.logWeight(
                    weightKg = current,
                    timestamp = time,
                    notes = if (index % 4 == 0) "Morning after 18h fast" else null
                )
            }

            // Also seed 3 completed past fasts
            val pastFasts = listOf(
                Triple(now - (3 * dayMs), 16.5f, FastingProtocol.LEANGAINS_16_8),
                Triple(now - (2 * dayMs), 18.2f, FastingProtocol.WARRIOR_18_6),
                Triple(now - (1 * dayMs), 20.0f, FastingProtocol.WARRIOR_20_4)
            )
            pastFasts.forEach { (startTime, durationHours, proto) ->
                val end = startTime + (durationHours * 3600 * 1000).toLong()
                database.fastingDao().insertSession(
                    FastingSession(
                        startTimeMillis = startTime,
                        targetEndTimeMillis = startTime + (proto.fastingHours * 3600 * 1000).toLong(),
                        actualEndTimeMillis = end,
                        targetFastingHours = proto.fastingHours,
                        protocolId = proto.id,
                        protocolName = proto.title,
                        isActive = false,
                        notes = "Felt steady energy throughout the fat burning phase.",
                        feeling = "Energized"
                    )
                )
            }

            // Seed initial water entries for today
            waterRepository.logWater(500, "Morning hydration upon waking")
            waterRepository.logWater(250, "Electrolytes during fast")
            waterRepository.logWater(500, "Green tea with catechins")
        }
    }

    // Fasting Controls
    fun selectProtocol(protocol: FastingProtocol) {
        _selectedProtocol.value = protocol
    }

    fun setCustomHours(hours: Float) {
        _customHours.value = hours.coerceIn(1f, 72f)
    }

    fun startFast(startTimeMillis: Long = System.currentTimeMillis()) {
        viewModelScope.launch {
            val proto = _selectedProtocol.value
            val customH = _customHours.value
            val sessionId = fastingRepository.startFast(
                protocol = proto,
                customHours = customH,
                startTime = startTimeMillis
            )
            val hours = if (proto == FastingProtocol.CUSTOM) customH else proto.fastingHours
            val targetEndTime = startTimeMillis + (hours * 3600 * 1000).toLong()
            val session = FastingSession(
                id = sessionId,
                startTimeMillis = startTimeMillis,
                targetEndTimeMillis = targetEndTime,
                targetFastingHours = hours,
                protocolId = proto.id,
                protocolName = if (proto == FastingProtocol.CUSTOM) "Custom (${hours.toInt()}h)" else proto.title,
                isActive = true
            )
            com.example.notification.FastingNotificationManager.scheduleFastingReminders(getApplication(), session)
        }
    }

    fun completeFast(notes: String? = null, feeling: String? = null) {
        viewModelScope.launch {
            val session = activeSession.value
            fastingRepository.completeActiveFast(
                endTime = System.currentTimeMillis(),
                notes = notes,
                feeling = feeling
            )
            com.example.notification.FastingNotificationManager.cancelFastingReminders(getApplication())

            // Schedule eating window end reminder if applicable
            val proto = session?.let { FastingProtocol.fromId(it.protocolId) } ?: _selectedProtocol.value
            val eatingHours = proto.eatingHours
            if (eatingHours > 0f) {
                val eatingEndTime = System.currentTimeMillis() + (eatingHours * 3600 * 1000).toLong()
                com.example.notification.FastingNotificationManager.scheduleEatingWindowEndReminder(
                    context = getApplication(),
                    eatingEndTimestampMillis = eatingEndTime,
                    protocol = proto
                )
            }
        }
    }

    fun cancelFast() {
        viewModelScope.launch {
            fastingRepository.cancelActiveFast()
            com.example.notification.FastingNotificationManager.cancelFastingReminders(getApplication())
        }
    }

    fun adjustStartTime(newStartTime: Long) {
        viewModelScope.launch {
            val session = activeSession.value ?: return@launch
            fastingRepository.updateStartTime(newStartTime, session.targetFastingHours)
            val updatedTargetEndTime = newStartTime + (session.targetFastingHours * 3600 * 1000).toLong()
            val updatedSession = session.copy(
                startTimeMillis = newStartTime,
                targetEndTimeMillis = updatedTargetEndTime
            )
            com.example.notification.FastingNotificationManager.cancelFastingReminders(getApplication())
            com.example.notification.FastingNotificationManager.scheduleFastingReminders(getApplication(), updatedSession)
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        notificationPrefs.setNotificationsEnabled(enabled)
        val session = activeSession.value
        if (session != null && session.isActive) {
            if (enabled) {
                com.example.notification.FastingNotificationManager.scheduleFastingReminders(getApplication(), session)
            } else {
                com.example.notification.FastingNotificationManager.cancelFastingReminders(getApplication())
            }
        }
    }

    fun setAutophagyAlertEnabled(enabled: Boolean) {
        notificationPrefs.setAutophagyAlertEnabled(enabled)
        val session = activeSession.value
        if (session != null && session.isActive && notificationPrefs.notificationsEnabled.value) {
            com.example.notification.FastingNotificationManager.scheduleFastingReminders(getApplication(), session)
        }
    }

    fun setEatingWindowAlertEnabled(enabled: Boolean) {
        notificationPrefs.setEatingWindowAlertEnabled(enabled)
    }

    fun sendTestNotification() {
        com.example.notification.FastingNotificationManager.createNotificationChannel(getApplication())
        com.example.notification.FastingNotificationManager.showNotification(
            context = getApplication(),
            title = "FastPulse Notification Active! 🔔",
            body = "Your local notification system is primed. You will receive alerts when your fasting goal is reached, when you enter peak autophagy, and when your eating window closes.",
            notificationId = com.example.notification.NotificationConstants.NOTIFICATION_ID_TEST,
            isAutophagy = false
        )
    }

    fun deleteSession(sessionId: Long) {
        viewModelScope.launch {
            fastingRepository.deleteSession(sessionId)
        }
    }

    // Weight Tracker Controls
    fun setMetric(metric: Boolean) {
        _isMetric.value = metric
        _weightUnit.value = if (metric) WeightUnit.KG else WeightUnit.LBS
    }

    fun setWeightUnit(unit: WeightUnit) {
        val metric = (unit == WeightUnit.KG)
        _isMetric.value = metric
        _weightUnit.value = unit
    }

    fun toggleWeightUnit() {
        val newMetric = !_isMetric.value
        _isMetric.value = newMetric
        _weightUnit.value = if (newMetric) WeightUnit.KG else WeightUnit.LBS
    }

    fun setChartRange(range: ChartRange) {
        _chartRange.value = range
    }

    fun logWeight(weightValue: Float, notes: String? = null, timestamp: Long = System.currentTimeMillis()) {
        viewModelScope.launch {
            val weightKg = if (!_isMetric.value) {
                weightValue * 0.45359237f
            } else {
                weightValue
            }
            weightRepository.logWeight(
                weightKg = weightKg,
                timestamp = timestamp,
                notes = notes
            )
        }
    }

    fun deleteWeight(id: Long) {
        viewModelScope.launch {
            weightRepository.deleteWeight(id)
        }
    }

    // Water Intake Controls
    fun logWater(amountMl: Int, note: String? = null) {
        viewModelScope.launch {
            waterRepository.logWater(amountMl, note)
        }
    }

    fun deleteWaterLog(id: Long) {
        viewModelScope.launch {
            waterRepository.deleteWaterLog(id)
        }
    }

    fun clearTodayWater() {
        viewModelScope.launch {
            waterRepository.clearTodayLogs()
        }
    }

    fun setWaterGoal(goalMl: Int) {
        _waterGoalMl.value = goalMl.coerceIn(500, 6000)
    }

    fun toggleWaterUnit() {
        _waterUnit.value = if (_waterUnit.value == WaterUnit.ML) WaterUnit.OZ else WaterUnit.ML
    }
}
