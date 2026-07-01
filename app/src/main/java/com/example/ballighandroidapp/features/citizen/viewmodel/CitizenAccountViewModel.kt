package com.example.ballighandroidapp.features.citizen.viewmodel

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ballighandroidapp.R
import com.example.ballighandroidapp.helpers.local.AppPreferences
import com.example.ballighandroidapp.helpers.local.data.entities.UserEntity
import com.example.ballighandroidapp.helpers.local.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CitizenAccountViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val appPreferences: AppPreferences
) : ViewModel() {

    private val _user = MutableStateFlow<UserEntity?>(null)
    val user: StateFlow<UserEntity?> = _user.asStateFlow()

    var isNotificationsEnabled by mutableStateOf(appPreferences.isNotificationsEnabled)
        private set

    var currentLanguageCode by mutableStateOf("en")
        private set

    init {
        loadCurrentUserData()
        syncLanguageState()
    }

    private fun syncLanguageState() {
        val currentLocales = AppCompatDelegate.getApplicationLocales()
        currentLanguageCode = if (currentLocales.toLanguageTags().startsWith("ar")) "ar" else "en"
    }

    /**
     * Reads the loggedInNationalId from preferences and fetches active UserEntity flow.
     * Exposed for public access to sync sessions after Login/Register.
     */
    fun loadCurrentUserData() {
        val nationalId = appPreferences.loggedInNationalId ?: return
        viewModelScope.launch {
            userRepository.getUserByNationalID(nationalId).collect {
                _user.value = it
            }
        }
    }

    /**
     * Dynamically updates application resource configuration and flips interface direction.
     * Triggers instant global recomposition with correct localized XML strings.
     */
    fun changeLanguage(langCode: String) {
        if (currentLanguageCode == langCode) return

        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(langCode)
        AppCompatDelegate.setApplicationLocales(appLocale)
        syncLanguageState()
    }

    fun toggleNotifications(): Int {
        val newState = !appPreferences.isNotificationsEnabled
        appPreferences.isNotificationsEnabled = newState
        isNotificationsEnabled = newState
        return if (newState) R.string.status_completed else R.string.status_delayed
    }

    // --- Edit Profile State ---
    var editName by mutableStateOf("")
    var editPhone by mutableStateOf("")
    var editPassword by mutableStateOf("")
    var editPhotoPath by mutableStateOf<String?>(null)

    var editNameError by mutableStateOf<Int?>(null)
    var editPhoneError by mutableStateOf<Int?>(null)
    var editPasswordError by mutableStateOf<Int?>(null)
    var isSaving by mutableStateOf(false)

    fun prepareEdit() {
        _user.value?.let {
            editName = it.fullName
            editPhone = it.phone
            editPassword = it.password
            editPhotoPath = it.profilePhotoPath
            resetErrors()
        }
    }

    private fun resetErrors() {
        editNameError = null
        editPhoneError = null
        editPasswordError = null
    }

    fun updateProfile(onSuccess: () -> Unit) {
        if (validateEdit()) {
            viewModelScope.launch {
                isSaving = true
                _user.value?.let { currentUser ->
                    val updatedUser = currentUser.copy(
                        fullName = editName,
                        phone = editPhone,
                        password = editPassword,
                        profilePhotoPath = editPhotoPath
                    )
                    userRepository.updateUser(updatedUser)
                    isSaving = false
                    onSuccess()
                }
            }
        }
    }

    private fun validateEdit(): Boolean {
        var isValid = true
        val nameRegex = "^[a-zA-Z\\s\\u0600-\\u06FF]+$".toRegex()

        if (editName.isBlank()) {
            editNameError = R.string.error_name_empty
            isValid = false
        } else if (!nameRegex.matches(editName)) {
            editNameError = R.string.error_invalid_name
            isValid = false
        }

        if (editPhone.length != 11) {
            editPhoneError = R.string.error_invalid_phone
            isValid = false
        }

        if (editPassword.length < 8) {
            editPasswordError = R.string.error_password_length
            isValid = false
        }

        return isValid
    }
}
