package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weight_entries")
data class WeightEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestampMillis: Long = System.currentTimeMillis(),
    val weightKg: Float,
    val notes: String? = null
) {
    /**
     * Exact conversion using the international standard: 1 lb = 0.45359237 kg
     * Therefore lbs = kg / 0.45359237
     */
    val weightLbs: Float
        get() = weightKg / 0.45359237f
}
