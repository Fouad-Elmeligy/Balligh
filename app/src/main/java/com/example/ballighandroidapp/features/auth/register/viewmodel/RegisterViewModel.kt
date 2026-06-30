package com.example.ballighandroidapp.features.auth.register.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ballighandroidapp.R
import com.example.ballighandroidapp.helpers.local.data.entities.UserEntity
import com.example.ballighandroidapp.helpers.local.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val userRepository: UserRepository
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

    var nationalIdErrorResId by mutableStateOf<Int?>(null)
        private set

    var passwordErrorResId by mutableStateOf<Int?>(null)
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
        }
    }

    fun onNationalIdChange(newValue: String) {
        if (newValue.length <= 14 && newValue.all { it.isDigit() }) {
            nationalId = newValue
            nationalIdErrorResId = null
        }
    }

    fun onPasswordChange(newValue: String) {
        password = newValue
        passwordErrorResId = null
    }

    fun onAgreedToTermsChange(newValue: Boolean) {
        agreedToTerms = newValue
    }

    val isFormValid: Boolean
        get() = fullName.isNotBlank() &&
                nameRegex.matches(fullName) &&
                (phone.length == 10 || phone.length == 11) &&
                nationalId.length == 14 &&
                password.isNotBlank() &&
                agreedToTerms

    fun register(onSuccess: () -> Unit) {
        if (validate()) {
            viewModelScope.launch {
                isLoading = true
                try {
                    val newUser = UserEntity(
                        fullName = fullName,
                        nationalID = nationalId,
                        password = password,
                        phone = phone,
                        role = 1, // Default role: Citizen
                        district = "", // Default empty as not in UI
                        accountStatus = 1 // Active
                    )
                    userRepository.insertUser(newUser)
                    onSuccess()
                } catch (e: Exception) {
                    // Handle error if needed
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

        if (nationalId.length != 14) {
            nationalIdErrorResId = R.string.error_invalid_national_id
            isValid = false
        }

        val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$".toRegex()
        if (password.isBlank()) {
            passwordErrorResId = R.string.error_password_empty
            isValid = false
        } else if (password.length < 8) {
            passwordErrorResId = R.string.error_password_length
            isValid = false
        } else if (!passwordPattern.matches(password)) {
            passwordErrorResId = R.string.error_password_weak
            isValid = false
        }

        if (!agreedToTerms) {
            isValid = false
        }

        return isValid
    }
}
