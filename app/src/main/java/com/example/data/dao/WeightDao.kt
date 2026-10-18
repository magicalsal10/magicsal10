package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.WeightEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface WeightDao {
    @Query("SELECT * FROM weight_entries ORDER BY timestampMillis DESC")
    fun getAllWeightsDesc(): Flow<List<WeightEntry>>

    @Query("SELECT * FROM weight_entries ORDER BY timestampMillis ASC")
    fun getAllWeightsAsc(): Flow<List<WeightEntry>>

    @Query("SELECT * FROM weight_entries ORDER BY timestampMillis DESC LIMIT 1")
    fun getLatestWeight(): Flow<WeightEntry?>

    @Query("SELECT * FROM weight_entries WHERE timestampMillis >= :fromTimestamp ORDER BY timestampMillis ASC")
    fun getWeightsSince(fromTimestamp: Long): Flow<List<WeightEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeight(entry: WeightEntry): Long

    @Update
    suspend fun updateWeight(entry: WeightEntry)

    @Query("DELETE FROM weight_entries WHERE id = :id")
    suspend fun deleteWeightById(id: Long)
}
