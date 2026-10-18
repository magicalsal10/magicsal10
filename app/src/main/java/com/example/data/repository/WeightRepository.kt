package com.example.data.repository

import com.example.data.dao.WeightDao
import com.example.data.entity.WeightEntry
import kotlinx.coroutines.flow.Flow

class WeightRepository(private val weightDao: WeightDao) {

    val allWeightsDesc: Flow<List<WeightEntry>> = weightDao.getAllWeightsDesc()
    val allWeightsAsc: Flow<List<WeightEntry>> = weightDao.getAllWeightsAsc()
    val latestWeight: Flow<WeightEntry?> = weightDao.getLatestWeight()

    fun getWeightsSince(fromTimestamp: Long): Flow<List<WeightEntry>> {
        return weightDao.getWeightsSince(fromTimestamp)
    }

    suspend fun logWeight(
        weightKg: Float,
        timestamp: Long = System.currentTimeMillis(),
        notes: String? = null
    ): Long {
        val entry = WeightEntry(
            weightKg = weightKg,
            timestampMillis = timestamp,
            notes = notes
        )
        return weightDao.insertWeight(entry)
    }

    suspend fun deleteWeight(id: Long) {
        weightDao.deleteWeightById(id)
    }
}
