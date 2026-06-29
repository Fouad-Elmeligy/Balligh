package com.example.ballighandroidapp.helpers.local.data.repository

import com.example.ballighandroidapp.helpers.local.data.entities.NotificationEntity
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    suspend fun insertNotification(notification: NotificationEntity)
    fun getNotificationsByUser(userId: Int): Flow<List<NotificationEntity>>
    fun getUnreadNotifications(userId: Int): Flow<List<NotificationEntity>>
    suspend fun markAsRead(notifId: Int)
    suspend fun markAllAsRead(userId: Int)
}