package com.example.ballighandroidapp.helpers.local.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "notifications",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userID"],
            childColumns = ["userID"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["userID"])]
)
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val notifId: Int = 0,
    val userID: Int,
    val message: String,
    val timeDelivered: Long = System.currentTimeMillis(),
    val isRead: Boolean //  1: Not Read, 2: Read
)