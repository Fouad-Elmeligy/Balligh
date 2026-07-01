package com.example.ballighandroidapp.helpers.local.data.repository

import com.example.ballighandroidapp.helpers.local.data.dao.ReportDao
import com.example.ballighandroidapp.helpers.local.data.entities.ReportEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ReportRepositoryImpl @Inject constructor(
    private val reportDao: ReportDao
) : ReportRepository {
    override suspend fun insertReport(report: ReportEntity) = reportDao.insertReport(report)
    override suspend fun updateReport(report: ReportEntity) = reportDao.updateReport(report)
    override suspend fun deleteReport(report: ReportEntity) = reportDao.deleteReport(report)
    override suspend fun getReportById(id: Int): ReportEntity? = reportDao.getReportById(id)
    override fun getAllReports(): Flow<List<ReportEntity>> = reportDao.getAllReports()
    override fun getReportsByUser(userId: Int): Flow<List<ReportEntity>> = reportDao.getReportsByUser(userId)
    override fun getReportsByStatus(statusId: Int): Flow<List<ReportEntity>> = reportDao.getReportsByStatus(statusId)
    override fun getReportsBySeverity(severityId: Int): Flow<List<ReportEntity>> = reportDao.getReportsBySeverity(severityId)
    override fun getReportsByDistrict(districtName: String): Flow<List<ReportEntity>> = reportDao.getReportsByDistrict(districtName)
    override fun searchReports(searchQuery: String): Flow<List<ReportEntity>> = reportDao.searchReports(searchQuery)
    override fun filterReports(statusId: Int?, severityId: Int?, districtName: String?): Flow<List<ReportEntity>> = reportDao.filterReports(statusId, severityId, districtName)
    override suspend fun getTotalReportsCount(): Int = reportDao.getTotalReportsCount()
    override suspend fun getReportsCountByStatus(statusId: Int): Int = reportDao.getReportsCountByStatus(statusId)
    override suspend fun getReportsCountBySeverity(severityId: Int): Int = reportDao.getReportsCountBySeverity(severityId)
    override suspend fun getReportsCountByDistrict(districtName: String): Int = reportDao.getReportsCountByDistrict(districtName)
    override suspend fun getReportsCountByUserID(userId: Int): Int = reportDao.getReportsCountByUserID(userId)
}