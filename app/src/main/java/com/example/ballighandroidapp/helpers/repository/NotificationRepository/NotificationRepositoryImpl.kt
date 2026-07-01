package com.example.ballighandroidapp.helpers.local.data.repository

import com.example.ballighandroidapp.helpers.local.data.dao.NotificationDao
import com.example.ballighandroidapp.helpers.local.data.entities.NotificationEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val notificationDao: NotificationDao
) : NotificationRepository {
    override suspend fun insertNotification(notification: NotificationEntity) = notificationDao.insertNotification(notification)
    override fun getNotificationsByUser(userId: Int): Flow<List<NotificationEntity>> = notificationDao.getNotificationsByUser(userId)
    override fun getUnreadNotifications(userId: Int): Flow<List<NotificationEntity>> = notificationDao.getUnreadNotifications(userId)
    override suspend fun markAsRead(notifId: Int) = notificationDao.markAsRead(notifId)
    override suspend fun markAllAsRead(userId: Int) = notificationDao.markAllAsRead(userId)
}