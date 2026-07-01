package com.example.ballighandroidapp.helpers.local.data.repository

import com.example.ballighandroidapp.helpers.local.data.entities.UserEntity
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun insertUser(user: UserEntity)
    suspend fun updateUser(user: UserEntity)
    suspend fun getUserById(id: Int): UserEntity?
    fun getUserByNationalID(nationalID: String): Flow<UserEntity?>
    fun getAllUsers(): Flow<List<UserEntity>>
    fun getUsersByRole(roleId: Int): Flow<List<UserEntity>>
    fun getUsersByDistrict(districtName: String): Flow<List<UserEntity>>
    suspend fun loginWithPhone(phone: String, password: String): UserEntity?
    suspend fun loginWithNationalID(nationalID: String, password: String): UserEntity?
    suspend fun isPhoneRegistered(phone: String): Boolean
    suspend fun isNationalIDRepeated(nationalID: String): Boolean
    suspend fun getUserIdByCredentials(identifier: String, password: String): Int?
}