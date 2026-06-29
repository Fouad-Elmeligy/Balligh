package com.example.ballighandroidapp.features.auth.register.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.ballighandroidapp.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor() : ViewModel() {

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

    var nationalIdErrorResId by mutableStateOf<Int?>(null)
        private set

    var passwordErrorResId by mutableStateOf<Int?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun onFullNameChange(newValue: String) {
        fullName = newValue
    }

    fun onPhoneChange(newValue: String) {
        // Restrict input to 11 digits maximum and only digits
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
                (phone.length == 10 || phone.length == 11) &&
                nationalId.isNotBlank() &&
                password.isNotBlank() &&
                agreedToTerms

    fun register(onSuccess: () -> Unit) {
        if (validate()) {
            // Registration logic would go here
            onSuccess()
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

        if (!agreedToTerms) {
            isValid = false
        }

        if (fullName.isBlank()) {
            isValid = false
        }

        if (!(phone.length == 10 || phone.length == 11)) {
            isValid = false
        }

        return isValid
    }
}
