package com.example.ballighandroidapp.helpers.local.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "reports",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userID"],
            childColumns = ["userID"],
            onDelete = ForeignKey.CASCADE //delete the reports related to the citizen
        )
    ],
    indices = [Index(value = ["userID"])]
)
data class ReportEntity(
    @PrimaryKey(autoGenerate = true)
    val reportID: Int = 0,
    val userID: Int,
    val photoUrl: String,
    val problemType: String,
    val severity: Int, // 1: Low, 2: Medium, 3: Critical
    val title: String,
    val content: String,
    val status: Int, // 1: UnderReview, 2: Waiting, 3: Solved, 4: Refused,
    val district: String,
    val location: String?,
    val dateReported: Long = System.currentTimeMillis()
)