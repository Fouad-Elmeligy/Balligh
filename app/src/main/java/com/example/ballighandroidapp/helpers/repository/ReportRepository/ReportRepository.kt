package com.example.ballighandroidapp.helpers.local.data.repository

import com.example.ballighandroidapp.helpers.local.data.entities.ReportEntity
import kotlinx.coroutines.flow.Flow

interface ReportRepository {
    suspend fun insertReport(report: ReportEntity)
    suspend fun updateReport(report: ReportEntity)
    suspend fun deleteReport(report: ReportEntity)
    suspend fun getReportById(id: Int): ReportEntity?
    fun getAllReports(): Flow<List<ReportEntity>>
    fun getReportsByUser(userId: Int): Flow<List<ReportEntity>>
    fun getReportsByStatus(statusId: Int): Flow<List<ReportEntity>>
    fun getReportsBySeverity(severityId: Int): Flow<List<ReportEntity>>
    fun getReportsByDistrict(districtName: String): Flow<List<ReportEntity>>
    fun searchReports(searchQuery: String): Flow<List<ReportEntity>>
    fun filterReports(statusId: Int?, severityId: Int?, districtName: String?): Flow<List<ReportEntity>>
    suspend fun getTotalReportsCount(): Int
    suspend fun getReportsCountByStatus(statusId: Int): Int
    suspend fun getReportsCountBySeverity(severityId: Int): Int
    suspend fun getReportsCountByDistrict(districtName: String): Int
    suspend fun getReportsCountByUserID(userId: Int): Int
}