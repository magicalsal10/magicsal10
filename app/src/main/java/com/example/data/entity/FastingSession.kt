package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fasting_sessions")
data class FastingSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startTimeMillis: Long,
    val targetEndTimeMillis: Long,
    val actualEndTimeMillis: Long? = null,
    val targetFastingHours: Float,
    val protocolId: String,
    val protocolName: String,
    val isActive: Boolean = false,
    val notes: String? = null,
    val feeling: String? = null
) {
    val durationMillis: Long
        get() {
            val end = actualEndTimeMillis ?: System.currentTimeMillis()
            return (end - startTimeMillis).coerceAtLeast(0L)
        }

    val durationHours: Float
        get() = durationMillis / (1000f * 60f * 60f)

    /**
     * Hours spent past the 16-hour physiological threshold into the autophagy zone.
     */
    val autophagyZoneHours: Float
        get() = maxOf(0f, durationHours - 16f)

    val isGoalReached: Boolean
        get() = durationHours >= targetFastingHours
}
