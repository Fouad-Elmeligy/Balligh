package com.example.ballighandroidapp.features.auth.login.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ballighandroidapp.R
import com.example.ballighandroidapp.helpers.local.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    var nationalId by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var rememberMe by mutableStateOf(false)
        private set

    var nationalIdErrorResId by mutableStateOf<Int?>(null)
        private set

    var passwordErrorResId by mutableStateOf<Int?>(null)
        private set

    var generalErrorResId by mutableStateOf<Int?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

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
        generalErrorResId = null
    }

    fun onRememberMeChange(newValue: Boolean) {
        rememberMe = newValue
    }

    fun login(onSuccess: () -> Unit) {
        if (validate()) {
            viewModelScope.launch {
                isLoading = true
                try {
                    val user = userRepository.loginWithNationalID(nationalId, password)
                    if (user != null) {
                        when (user.accountStatus) {
                            1 -> onSuccess()
                            2 -> generalErrorResId = R.string.error_account_blocked
                            else -> generalErrorResId = R.string.error_invalid_credentials
                        }
                    } else {
                        generalErrorResId = R.string.error_invalid_credentials
                    }
                } catch (e: Exception) {
                    generalErrorResId = R.string.status_evaluating
                } finally {
                    isLoading = false
                }
            }
        }
    }

    private fun validate(): Boolean {
        var isValid = true

        if (nationalId.length != 14) {
            nationalIdErrorResId = R.string.error_invalid_national_id
            isValid = false
        }

        // Advanced Password Validation: Min 8 chars, Uppercase, Lowercase, Number
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

        return isValid
    }
}
