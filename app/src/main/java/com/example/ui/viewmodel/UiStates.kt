package com.example.ui.viewmodel

import androidx.compose.ui.graphics.Color
import com.example.data.entity.FastingSession
import com.example.data.entity.WaterLogEntry
import com.example.data.entity.WeightEntry
import com.example.model.FastingProtocol
import com.example.model.MetabolicStage

enum class WeightUnit(val label: String, val step: Float) {
    KG("kg", 0.1f),
    LBS("lbs", 0.2f)
}

enum class WaterUnit(val label: String) {
    ML("ml"),
    OZ("oz")
}

enum class ChartRange(val label: String, val days: Int) {
    WEEK("7D", 7),
    MONTH("30D", 30),
    THREE_MONTHS("90D", 90),
    ALL("All", Int.MAX_VALUE)
}

data class WeightChartPoint(
    val timestampMillis: Long,
    val rawWeight: Float, // Converted to user's selected unit
    val rollingAverage: Float?, // Converted to user's selected unit
    val dateLabel: String
)

data class WeeklyFastingStats(
    val totalFastedHours: Float = 0f,
    val autophagyZoneHours: Float = 0f,
    val averageFastHours: Float = 0f,
    val completedFastsCount: Int = 0,
    val longestFastHours: Float = 0f
)

data class FastingTimerUiState(
    val isFasting: Boolean = false,
    val session: FastingSession? = null,
    val elapsedMillis: Long = 0L,
    val remainingMillis: Long = 0L,
    val progress: Float = 0f,
    val isOvertime: Boolean = false,
    val overtimeMillis: Long = 0L,
    val currentStage: MetabolicStage = MetabolicStage.ANABOLIC,
    val stageProgress: Float = 0f,
    val millisToNextStage: Long? = null,
    val selectedProtocol: FastingProtocol = FastingProtocol.LEANGAINS_16_8,
    val customHours: Float = 16f
)

data class DailyWaterState(
    val goalMl: Int = 2500,
    val currentIntakeMl: Int = 0,
    val progress: Float = 0f,
    val remainingMl: Int = 2500,
    val logs: List<WaterLogEntry> = emptyList(),
    val isGoalReached: Boolean = false,
    val unit: WaterUnit = WaterUnit.ML
)
