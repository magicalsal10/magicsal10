package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.FastingSession
import kotlinx.coroutines.flow.Flow

@Dao
interface FastingDao {
    @Query("SELECT * FROM fasting_sessions WHERE isActive = 1 ORDER BY startTimeMillis DESC LIMIT 1")
    fun getActiveSession(): Flow<FastingSession?>

    @Query("SELECT * FROM fasting_sessions WHERE isActive = 0 ORDER BY startTimeMillis DESC")
    fun getCompletedSessions(): Flow<List<FastingSession>>

    @Query("SELECT * FROM fasting_sessions ORDER BY startTimeMillis DESC")
    fun getAllSessions(): Flow<List<FastingSession>>

    @Query("SELECT * FROM fasting_sessions WHERE isActive = 0 ORDER BY startTimeMillis DESC LIMIT :limit")
    fun getRecentCompleted(limit: Int = 10): Flow<List<FastingSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: FastingSession): Long

    @Update
    suspend fun updateSession(session: FastingSession)

    @Query("UPDATE fasting_sessions SET isActive = 0, actualEndTimeMillis = :endTime, notes = :notes, feeling = :feeling WHERE isActive = 1")
    suspend fun completeActiveSession(endTime: Long, notes: String? = null, feeling: String? = null)

    @Query("UPDATE fasting_sessions SET startTimeMillis = :newStartTime, targetEndTimeMillis = :newTargetEndTime WHERE isActive = 1")
    suspend fun updateActiveStartTime(newStartTime: Long, newTargetEndTime: Long)

    @Query("DELETE FROM fasting_sessions WHERE isActive = 1")
    suspend fun cancelActiveSessions()

    @Query("DELETE FROM fasting_sessions WHERE id = :id")
    suspend fun deleteSessionById(id: Long)
}
