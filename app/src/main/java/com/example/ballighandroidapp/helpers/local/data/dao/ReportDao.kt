package com.example.ballighandroidapp.helpers.local.data.dao

import androidx.room.*
import com.example.ballighandroidapp.helpers.local.data.entities.ReportEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReportDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: ReportEntity)

    @Update
    suspend fun updateReport(report: ReportEntity)

    @Delete
    suspend fun deleteReport(report: ReportEntity)

    @Query("SELECT * FROM reports WHERE reportID = :id")
    suspend fun getReportById(id: Int): ReportEntity?

    @Query("SELECT * FROM reports ORDER BY dateReported DESC")
    fun getAllReports(): Flow<List<ReportEntity>>

    @Query("SELECT * FROM reports WHERE userID = :userId ORDER BY dateReported DESC")
    fun getReportsByUser(userId: Int): Flow<List<ReportEntity>>

    @Query("SELECT * FROM reports WHERE status = :statusId ORDER BY dateReported DESC")
    fun getReportsByStatus(statusId: Int): Flow<List<ReportEntity>>

    @Query("SELECT * FROM reports WHERE severity = :severityId ORDER BY dateReported DESC")
    fun getReportsBySeverity(severityId: Int): Flow<List<ReportEntity>>

    @Query("SELECT * FROM reports WHERE district = :districtName ORDER BY dateReported DESC")
    fun getReportsByDistrict(districtName: String): Flow<List<ReportEntity>>

    @Query("SELECT * FROM reports WHERE title LIKE '%' || :searchQuery || '%' OR content LIKE '%' || :searchQuery || '%'")
    fun searchReports(searchQuery: String): Flow<List<ReportEntity>>

    @Query("""
        SELECT * FROM reports 
        WHERE (:statusId IS NULL OR status = :statusId)
        AND (:severityId IS NULL OR severity = :severityId)
        AND (:districtName IS NULL OR district = :districtName)
        ORDER BY dateReported DESC
    """)
    fun filterReports(statusId: Int?, severityId: Int?, districtName: String?): Flow<List<ReportEntity>>

    @Query("SELECT COUNT(*) FROM reports")
    suspend fun getTotalReportsCount(): Int

    @Query("SELECT COUNT(*) FROM reports WHERE status = :statusId")
    suspend fun getReportsCountByStatus(statusId: Int): Int

    @Query("SELECT COUNT(*) FROM reports WHERE severity = :severityId")
    suspend fun getReportsCountBySeverity(severityId: Int): Int

    @Query("SELECT COUNT(*) FROM reports WHERE district = :districtName")
    suspend fun getReportsCountByDistrict(districtName: String): Int

    @Query("SELECT COUNT(*) FROM reports WHERE UserID = :userId")
    suspend fun getReportsCountByUserID(userId: Int): Int
}