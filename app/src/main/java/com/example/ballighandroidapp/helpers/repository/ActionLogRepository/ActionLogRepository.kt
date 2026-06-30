package com.example.ballighandroidapp.helpers.repository.ActionLogRepository

import com.example.ballighandroidapp.helpers.local.data.entities.ActionLogEntity
import kotlinx.coroutines.flow.Flow

interface ActionLogRepository {
    suspend fun insertLog(log: ActionLogEntity): Long
    fun getAllLogs(): Flow<List<ActionLogEntity>>
    fun getLogsByEmployee(employeeId: Int): Flow<List<ActionLogEntity>>
    fun getLogsByReport(reportId: Int): Flow<List<ActionLogEntity>>
    fun getLogsByType(type: String): Flow<List<ActionLogEntity>>
    fun getLogsInTimeRange(startTime: Long, endTime: Long): Flow<List<ActionLogEntity>>
    suspend fun getLogsCountByEmployee(employeeId: Int): Int
    fun getRecentLogs(limit: Int): Flow<List<ActionLogEntity>>
}

