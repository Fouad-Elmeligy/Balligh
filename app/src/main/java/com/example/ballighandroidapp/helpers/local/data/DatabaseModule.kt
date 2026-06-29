package com.example.ballighandroidapp.helpers.local.data

import android.content.Context
import androidx.room.Room
import com.example.ballighandroidapp.data.local.data.dao.BallighDatabase
import com.example.ballighandroidapp.helpers.local.data.dao.ActionLogDao
import com.example.ballighandroidapp.helpers.local.data.dao.NotificationDao
import com.example.ballighandroidapp.helpers.local.data.dao.ReportDao
import com.example.ballighandroidapp.helpers.local.data.dao.UserDao
import com.example.ballighandroidapp.helpers.local.data.repository.ActionLogRepositoryImpl
import com.example.ballighandroidapp.helpers.local.data.repository.NotificationRepository
import com.example.ballighandroidapp.helpers.local.data.repository.NotificationRepositoryImpl
import com.example.ballighandroidapp.helpers.local.data.repository.ReportRepository
import com.example.ballighandroidapp.helpers.local.data.repository.ReportRepositoryImpl
import com.example.ballighandroidapp.helpers.local.data.repository.UserRepository
import com.example.ballighandroidapp.helpers.local.data.repository.UserRepositoryImpl
import com.example.ballighandroidapp.helpers.repository.ActionLogRepository.ActionLogRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): BallighDatabase {
        return Room.databaseBuilder(
            context,
            BallighDatabase::class.java,
            "balligh_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideUserDao(database: BallighDatabase): UserDao {
        return database.userDao()
    }
    @Provides
    @Singleton
    fun provideUserRepository(userDao: UserDao): UserRepository {
        return UserRepositoryImpl(userDao)
    }


    @Provides
    @Singleton
    fun provideReportsDao(database: BallighDatabase): ReportDao {
        return database.reportDao()
    }
    @Provides
    @Singleton
    fun provideReportsRepository(reportDao: ReportDao): ReportRepository {
        return ReportRepositoryImpl(reportDao)
    }

    @Provides
    @Singleton
    fun provideNotifactionDao(database: BallighDatabase): NotificationDao {
        return database.notificationDao()
    }
    @Provides
    @Singleton
    fun provideNotifactionsRepository(notificationDao: NotificationDao): NotificationRepository {
        return NotificationRepositoryImpl(notificationDao)
    }

    @Provides
    @Singleton
    fun provideActionLogDao(database: BallighDatabase): ActionLogDao {
        return database.actionsLogDao()
    }
    @Provides
    @Singleton
    fun provideActionLogRepository(actionLogDao: ActionLogDao): ActionLogRepository {
        return ActionLogRepositoryImpl(actionLogDao)
    }

}