package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.entity.WaterLogEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface WaterDao {
    @Query("SELECT * FROM water_logs WHERE timestampMillis >= :startOfDayMillis AND timestampMillis < :endOfDayMillis ORDER BY timestampMillis DESC")
    fun getTodayLogs(startOfDayMillis: Long, endOfDayMillis: Long): Flow<List<WaterLogEntry>>

    @Query("SELECT * FROM water_logs ORDER BY timestampMillis DESC")
    fun getAllLogs(): Flow<List<WaterLogEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWaterLog(entry: WaterLogEntry): Long

    @Query("DELETE FROM water_logs WHERE id = :id")
    suspend fun deleteWaterLog(id: Long)

    @Query("DELETE FROM water_logs WHERE timestampMillis >= :startOfDayMillis AND timestampMillis < :endOfDayMillis")
    suspend fun clearTodayLogs(startOfDayMillis: Long, endOfDayMillis: Long)
}
