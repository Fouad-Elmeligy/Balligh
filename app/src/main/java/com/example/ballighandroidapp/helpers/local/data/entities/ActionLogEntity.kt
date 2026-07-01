package com.example.ballighandroidapp.helpers.local.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "action_logs",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userID"],
            childColumns = ["employeeID"],
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = ReportEntity::class,
            parentColumns = ["reportID"],
            childColumns = ["targetReportID"],
            onDelete = ForeignKey.NO_ACTION
        )
    ],
    indices = [Index(value = ["employeeID"]), Index(value = ["targetReportID"])]
)
data class ActionLogEntity(
    @PrimaryKey(autoGenerate = true)
    val logID: Int = 0,
    val employeeID: Int,
    val actionType: String, // "STATUS_UPDATE", "REPORT_ASSIGNED", "ACCOUNT_BLOCKED"
    val targetReportID: Int?,
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
)