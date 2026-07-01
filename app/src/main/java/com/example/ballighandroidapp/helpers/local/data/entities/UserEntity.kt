package com.example.ballighandroidapp.helpers.local.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val userID: Int = 0,
    val fullName: String,
    val nationalID: String,
    val password: String,
    val phone: String,
    val role: Int, // 1: Citizen, 2: Employee, 3: Admin
    val district: String,
    val profilePhotoPath: String? = null,
    // So the employee can't delete the citizen just block it
    val accountStatus: Int = 1 // 1: Active, 2: Blocked, 3: Deactivated
)
