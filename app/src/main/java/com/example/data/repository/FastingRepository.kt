package com.example.data.repository

import com.example.data.dao.FastingDao
import com.example.data.entity.FastingSession
import com.example.model.FastingProtocol
import kotlinx.coroutines.flow.Flow

class FastingRepository(private val fastingDao: FastingDao) {

    val activeSession: Flow<FastingSession?> = fastingDao.getActiveSession()
    val completedSessions: Flow<List<FastingSession>> = fastingDao.getCompletedSessions()
    val allSessions: Flow<List<FastingSession>> = fastingDao.getAllSessions()
    val recentSessions: Flow<List<FastingSession>> = fastingDao.getRecentCompleted(15)

    suspend fun startFast(
        protocol: FastingProtocol,
        customHours: Float? = null,
        startTime: Long = System.currentTimeMillis()
    ): Long {
        // Cancel any lingering active sessions first
        fastingDao.cancelActiveSessions()

        val hours = if (protocol == FastingProtocol.CUSTOM && customHours != null) {
            customHours
        } else {
            protocol.fastingHours
        }

        val targetEnd = startTime + (hours * 3600 * 1000).toLong()

        val session = FastingSession(
            startTimeMillis = startTime,
            targetEndTimeMillis = targetEnd,
            targetFastingHours = hours,
            protocolId = protocol.id,
            protocolName = if (protocol == FastingProtocol.CUSTOM) "Custom (${hours.toInt()}h)" else protocol.title,
            isActive = true
        )
        return fastingDao.insertSession(session)
    }

    suspend fun completeActiveFast(
        endTime: Long = System.currentTimeMillis(),
        notes: String? = null,
        feeling: String? = null
    ) {
        fastingDao.completeActiveSession(
            endTime = endTime,
            notes = notes,
            feeling = feeling
        )
    }

    suspend fun updateStartTime(newStartTime: Long, targetHours: Float) {
        val newTargetEndTime = newStartTime + (targetHours * 3600 * 1000).toLong()
        fastingDao.updateActiveStartTime(newStartTime, newTargetEndTime)
    }

    suspend fun cancelActiveFast() {
        fastingDao.cancelActiveSessions()
    }

    suspend fun deleteSession(id: Long) {
        fastingDao.deleteSessionById(id)
    }
}
