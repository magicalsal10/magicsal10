package com.example.data.repository

import com.example.data.dao.WaterDao
import com.example.data.entity.WaterLogEntry
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class WaterRepository(private val waterDao: WaterDao) {

    fun getTodayRange(): Pair<Long, Long> {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val start = calendar.timeInMillis
        val end = start + (24L * 3600 * 1000)
        return Pair(start, end)
    }

    fun getTodayLogs(): Flow<List<WaterLogEntry>> {
        val (start, end) = getTodayRange()
        return waterDao.getTodayLogs(start, end)
    }

    fun getAllLogs(): Flow<List<WaterLogEntry>> = waterDao.getAllLogs()

    suspend fun logWater(amountMl: Int, notes: String? = null): Long {
        val entry = WaterLogEntry(
            amountMl = amountMl,
            timestampMillis = System.currentTimeMillis(),
            notes = notes
        )
        return waterDao.insertWaterLog(entry)
    }

    suspend fun deleteWaterLog(id: Long) {
        waterDao.deleteWaterLog(id)
    }

    suspend fun clearTodayLogs() {
        val (start, end) = getTodayRange()
        waterDao.clearTodayLogs(start, end)
    }
}
