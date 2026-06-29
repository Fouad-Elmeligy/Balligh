package com.example.ballighandroidapp.helpers.local.data.dao

import androidx.room.*
import com.example.ballighandroidapp.helpers.local.data.entities.NotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("SELECT * FROM notifications WHERE userId = :userID ORDER BY timeDelivered DESC")
    fun getNotificationsByUser(userID: Int): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM notifications WHERE userId = :userID AND isRead = 1 ORDER BY timeDelivered DESC")
    fun getUnreadNotifications(userID: Int): Flow<List<NotificationEntity>>

    @Query("UPDATE notifications SET isRead = 2 WHERE notifId = :notifId")
    suspend fun markAsRead(notifId: Int)

    @Query("UPDATE notifications SET isRead = 2 WHERE userID = :userID")
    suspend fun markAllAsRead(userID: Int)
}