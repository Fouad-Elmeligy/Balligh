package com.example.ballighandroidapp.helpers.local.data.dao

import androidx.room.*
import com.example.ballighandroidapp.helpers.local.data.entities.ActionLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActionLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: ActionLogEntity): Long

    @Query("SELECT * FROM action_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<ActionLogEntity>>

    @Query("SELECT * FROM action_logs WHERE employeeID = :employeeId ORDER BY timestamp DESC")
    fun getLogsByEmployee(employeeId: Int): Flow<List<ActionLogEntity>>

    @Query("SELECT * FROM action_logs WHERE targetReportID = :reportId ORDER BY timestamp DESC")
    fun getLogsByReport(reportId: Int): Flow<List<ActionLogEntity>>

    @Query("SELECT * FROM action_logs WHERE actionType = :type ORDER BY timestamp DESC")
    fun getLogsByType(type: String): Flow<List<ActionLogEntity>>

    @Query("SELECT * FROM action_logs WHERE timestamp BETWEEN :startTime AND :endTime")
    fun getLogsInTimeRange(
        startTime: Long,
        endTime: Long
    ): Flow<List<ActionLogEntity>>

    @Query("SELECT COUNT(*) FROM action_logs WHERE employeeID = :employeeId")
    suspend fun getLogsCountByEmployee(employeeId: Int): Int

    @Query("SELECT * FROM action_logs ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentLogs(limit: Int): Flow<List<ActionLogEntity>>
}