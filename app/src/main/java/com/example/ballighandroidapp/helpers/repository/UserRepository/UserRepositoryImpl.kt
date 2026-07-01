package com.example.ballighandroidapp.helpers.local.data.repository

import com.example.ballighandroidapp.helpers.local.data.dao.UserDao
import com.example.ballighandroidapp.helpers.local.data.entities.UserEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : UserRepository {
    override suspend fun insertUser(user: UserEntity) = userDao.insertUser(user)
    override suspend fun updateUser(user: UserEntity) = userDao.updateUser(user)
    override suspend fun getUserById(id: Int): UserEntity? = userDao.getUserById(id)
    override fun getUserByNationalID(nationalID: String): Flow<UserEntity?> = userDao.getUserByNationalID(nationalID)
    override fun getAllUsers(): Flow<List<UserEntity>> = userDao.getAllUsers()
    override fun getUsersByRole(roleId: Int): Flow<List<UserEntity>> = userDao.getUsersByRole(roleId)
    override fun getUsersByDistrict(districtName: String): Flow<List<UserEntity>> = userDao.getUsersByDistrict(districtName)
    override suspend fun loginWithPhone(phone: String, password: String): UserEntity? = userDao.loginWithPhone(phone, password)
    override suspend fun loginWithNationalID(nationalID: String, password: String): UserEntity? = userDao.loginWithNationalID(nationalID, password)
    override suspend fun isPhoneRegistered(phone: String): Boolean = userDao.isPhoneRegistered(phone)
    override suspend fun isNationalIDRepeated(nationalID: String): Boolean = userDao.isNationalIDRepeated(nationalID)
    override suspend fun getUserIdByCredentials(identifier: String, password: String): Int? = userDao.getUserIdByCredentials(identifier, password)
}
