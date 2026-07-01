package com.example.ballighandroidapp.features.auth.register.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ballighandroidapp.R
import com.example.ballighandroidapp.helpers.local.AppPreferences
import com.example.ballighandroidapp.helpers.local.data.entities.UserEntity
import com.example.ballighandroidapp.helpers.local.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val appPreferences: AppPreferences
) : ViewModel() {

    var fullName by mutableStateOf("")
        private set

    var phone by mutableStateOf("")
        private set

    var nationalId by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var agreedToTerms by mutableStateOf(false)
        private set

    var fullNameErrorResId by mutableStateOf<Int?>(null)
        private set

    var phoneErrorResId by mutableStateOf<Int?>(null)
        private set

    var nationalIdErrorResId by mutableStateOf<Int?>(null)
        private set

    var passwordErrorResId by mutableStateOf<Int?>(null)
        private set

    var generalErrorResId by mutableStateOf<Int?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    private val nameRegex = "^[a-zA-Z\\s\\u0600-\\u06FF]+$".toRegex()

    fun onFullNameChange(newValue: String) {
        fullName = newValue
        fullNameErrorResId = null
    }

    fun onPhoneChange(newValue: String) {
        if (newValue.length <= 11 && newValue.all { it.isDigit() }) {
            phone = newValue
            phoneErrorResId = null
        }
    }

    fun onNationalIdChange(newValue: String) {
        if (newValue.length <= 14 && newValue.all { it.isDigit() }) {
            nationalId = newValue
            nationalIdErrorResId = null
            generalErrorResId = null
        }
    }

    fun onPasswordChange(newValue: String) {
        password = newValue
        passwordErrorResId = null
    }

    fun onAgreedToTermsChange(newValue: Boolean) {
        agreedToTerms = newValue
    }

    fun register(onSuccess: () -> Unit) {
        if (validate()) {
            viewModelScope.launch {
                isLoading = true
                try {
                    if (userRepository.isNationalIDRepeated(nationalId)) {
                        nationalIdErrorResId = R.string.error_national_id_exists
                        isLoading = false
                        return@launch
                    }

                    val newUser = UserEntity(
                        fullName = fullName.trim(),
                        nationalID = nationalId,
                        password = password,
                        phone = phone,
                        role = 1, // Citizen
                        district = "",
                        accountStatus = 1
                    )
                    userRepository.insertUser(newUser)

                    appPreferences.loggedInNationalId = nationalId
                    appPreferences.currentUserRole = 1
                    appPreferences.isUserLoggedIn = true

                    onSuccess()
                } catch (e: Exception) {
                    generalErrorResId = R.string.error_something_went_wrong
                } finally {
                    isLoading = false
                }
            }
        }
    }

    private fun validate(): Boolean {
        var isValid = true

        if (fullName.isBlank()) {
            fullNameErrorResId = R.string.error_name_empty
            isValid = false
        } else if (!nameRegex.matches(fullName)) {
            fullNameErrorResId = R.string.error_invalid_name
            isValid = false
        }

        if (phone.isBlank()) {
            phoneErrorResId = R.string.error_phone_empty
            isValid = false
        } else if (phone.length < 10) {
            phoneErrorResId = R.string.error_invalid_phone
            isValid = false
        }

        if (nationalId == "99999999999999" || nationalId == "88888888888888") {
            nationalIdErrorResId = R.string.error_national_id_exists
            isValid = false
        } else if (nationalId.length != 14) {
            nationalIdErrorResId = R.string.error_invalid_national_id
            isValid = false
        }

        if (password.isBlank()) {
            passwordErrorResId = R.string.error_password_empty
            isValid = false
        } else if (password.length < 8) {
            passwordErrorResId = R.string.error_password_length
            isValid = false
        }

        if (!agreedToTerms) {
            generalErrorResId = R.string.error_must_accept_terms
            isValid = false
        }

        return isValid
    }
}