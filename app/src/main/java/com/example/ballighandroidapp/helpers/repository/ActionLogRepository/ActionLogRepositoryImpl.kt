package com.example.ballighandroidapp.helpers.local.data.repository

import javax.inject.Inject
import com.example.ballighandroidapp.helpers.local.data.dao.ActionLogDao
import com.example.ballighandroidapp.helpers.local.data.entities.ActionLogEntity
import com.example.ballighandroidapp.helpers.repository.ActionLogRepository.ActionLogRepository
import kotlinx.coroutines.flow.Flow

class ActionLogRepositoryImpl @Inject constructor(
    private val actionLogDao: ActionLogDao
) : ActionLogRepository {
    override suspend fun insertLog(log: ActionLogEntity): Long = actionLogDao.insertLog(log)
    override fun getAllLogs(): Flow<List<ActionLogEntity>> = actionLogDao.getAllLogs()
    override fun getLogsByEmployee(employeeId: Int): Flow<List<ActionLogEntity>> = actionLogDao.getLogsByEmployee(employeeId)
    override fun getLogsByReport(reportId: Int): Flow<List<ActionLogEntity>> = actionLogDao.getLogsByReport(reportId)
    override fun getLogsByType(type: String): Flow<List<ActionLogEntity>> = actionLogDao.getLogsByType(type)
    override fun getLogsInTimeRange(startTime: Long, endTime: Long): Flow<List<ActionLogEntity>> = actionLogDao.getLogsInTimeRange(startTime, endTime)
    override suspend fun getLogsCountByEmployee(employeeId: Int): Int = actionLogDao.getLogsCountByEmployee(employeeId)
    override fun getRecentLogs(limit: Int): Flow<List<ActionLogEntity>> = actionLogDao.getRecentLogs(limit)
}