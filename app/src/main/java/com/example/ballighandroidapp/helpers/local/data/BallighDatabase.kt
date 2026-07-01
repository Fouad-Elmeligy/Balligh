package com.example.ballighandroidapp.data.local.data.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.ballighandroidapp.helpers.local.data.dao.ActionLogDao
import com.example.ballighandroidapp.helpers.local.data.dao.NotificationDao
import com.example.ballighandroidapp.helpers.local.data.dao.ReportDao
import com.example.ballighandroidapp.helpers.local.data.dao.UserDao
import com.example.ballighandroidapp.helpers.local.data.entities.ActionLogEntity
import com.example.ballighandroidapp.helpers.local.data.entities.NotificationEntity
import com.example.ballighandroidapp.helpers.local.data.entities.ReportEntity
import com.example.ballighandroidapp.helpers.local.data.entities.UserEntity

@Database(
    entities = [
        UserEntity::class,
        ReportEntity::class,
        NotificationEntity::class,
        ActionLogEntity::class
    ],
    version = 2,
    exportSchema = true
)
abstract class BallighDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun reportDao(): ReportDao
    abstract fun notificationDao(): NotificationDao
    abstract fun actionsLogDao(): ActionLogDao

    companion object {
        @Volatile
        private var INSTANCE: BallighDatabase? = null

        fun getDatabase(context: Context): BallighDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BallighDatabase::class.java,
                    "balligh_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}