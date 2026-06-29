package com.example.ballighandroidapp.helpers.local.data.dao

import androidx.room.*
import com.example.ballighandroidapp.helpers.local.data.entities.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE userID = :id")
    suspend fun getUserById(id: Int): UserEntity?

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE role = :roleId")
    fun getUsersByRole(roleId: Int): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE district = :districtName")
    fun getUsersByDistrict(districtName: String): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE phone = :phone AND password = :password LIMIT 1")
    suspend fun loginWithPhone(phone: String, password: String): UserEntity?

    @Query("SELECT * FROM users WHERE nationalID = :nationalID AND password = :password LIMIT 1")
    suspend fun loginWithNationalID(nationalID: String, password: String): UserEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE phone = :phone)")
    suspend fun isPhoneRegistered(phone: String): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE nationalID = :nationalID)")
    suspend fun isNationalIDRepeated(nationalID: String): Boolean

    @Query("""
        SELECT userID FROM users 
        WHERE (nationalID = :identifier OR phone = :identifier) 
        AND password = :password 
        LIMIT 1
    """)
    suspend fun getUserIdByCredentials(identifier: String, password: String): Int?
}