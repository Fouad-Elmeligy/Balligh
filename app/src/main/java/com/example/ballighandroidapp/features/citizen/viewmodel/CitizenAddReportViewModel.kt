package com.example.ballighandroidapp.features.citizen.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ballighandroidapp.R
import com.example.ballighandroidapp.helpers.local.AppPreferences
import com.example.ballighandroidapp.helpers.local.data.entities.ReportEntity
import com.example.ballighandroidapp.helpers.local.data.repository.ReportRepository
import com.example.ballighandroidapp.helpers.local.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI State for the Add Report screen.
 * Encapsulates all form data and submission status.
 */
data class CitizenAddReportUiState(
    val attachedPhotoPath: String? = null,
    val title: String = "",
    val district: String = "Nasr City", // Default district
    val location: String = "",
    val problemType: String = "General",
    val draftContent: String = "",
    val currentUserId: Int? = null,
    val isLocationEditing: Boolean = true,
    val isGeneratingDraft: Boolean = false,
    val isSubmitting: Boolean = false,
    val submitSuccess: Boolean = false,
    val errorMessageResId: Int? = null
)

@HiltViewModel
class CitizenAddReportViewModel @Inject constructor(
    private val reportRepository: ReportRepository,
    private val userRepository: UserRepository,
    private val appPreferences: AppPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(CitizenAddReportUiState())
    val uiState: StateFlow<CitizenAddReportUiState> = _uiState.asStateFlow()

    init {
        fetchCurrentUserSession()
    }

    private fun fetchCurrentUserSession() {
        val nationalId = appPreferences.loggedInNationalId ?: return
        viewModelScope.launch {
            userRepository.getUserByNationalID(nationalId).collectLatest { user ->
                _uiState.update { it.copy(currentUserId = user?.userID) }
            }
        }
    }

    // --- Form Update Methods ---

    fun onPhotoSelected(path: String) {
        _uiState.update { it.copy(attachedPhotoPath = path) }
    }

    fun onPhotoRemoved() {
        _uiState.update { it.copy(attachedPhotoPath = null) }
    }

    fun onTitleChanged(newTitle: String) {
        _uiState.update { it.copy(title = newTitle) }
    }

    fun onDistrictChanged(newDistrict: String) {
        _uiState.update { it.copy(district = newDistrict) }
    }

    fun onLocationChanged(newLocation: String) {
        _uiState.update { it.copy(location = newLocation) }
    }

    /**
     * Toggles the location field between read-only and editable mode.
     */
    fun onLocationEditToggle() {
        _uiState.update { it.copy(isLocationEditing = !it.isLocationEditing) }
    }

    /**
     * Updates the type of problem being reported (e.g., Lighting, Road Damage).
     */
    fun onProblemTypeChanged(newType: String) {
        _uiState.update { it.copy(problemType = newType) }
    }

    fun onDraftContentChanged(newContent: String) {
        _uiState.update { it.copy(draftContent = newContent) }
    }

    fun onErrorDismissed() {
        _uiState.update { it.copy(errorMessageResId = null) }
    }

    private fun validate(): Boolean {
        val state = _uiState.value
        return when {
            state.currentUserId == null -> {
                _uiState.update { it.copy(errorMessageResId = R.string.error_session_expired) }
                false
            }
            state.attachedPhotoPath == null -> {
                _uiState.update { it.copy(errorMessageResId = R.string.error_photo_required) }
                false
            }
            state.title.isBlank() -> {
                _uiState.update { it.copy(errorMessageResId = R.string.error_title_required) }
                false
            }
            state.draftContent.isBlank() -> {
                _uiState.update { it.copy(errorMessageResId = R.string.error_content_required) }
                false
            }
            else -> true
        }
    }

    fun sendReport() {
        if (!validate()) return

        val state = _uiState.value
        _uiState.update { it.copy(isSubmitting = true) }

        viewModelScope.launch {
            try {
                val newReport = ReportEntity(
                    userID = state.currentUserId!!,
                    photoUrl = state.attachedPhotoPath!!,
                    problemType = state.problemType,
                    severity = 2, // Default Medium
                    title = state.title.trim(),
                    content = state.draftContent.trim(),
                    status = 1, // 1: UnderReview
                    district = state.district,
                    location = state.location.ifBlank { state.district }
                )

                reportRepository.insertReport(newReport)
                _uiState.update { it.copy(isSubmitting = false, submitSuccess = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        errorMessageResId = R.string.error_something_went_wrong
                    )
                }
            }
        }
    }

    fun saveAsDraft() {
        if (_uiState.value.attachedPhotoPath == null) {
            _uiState.update { it.copy(errorMessageResId = R.string.error_photo_required) }
            return
        }

        viewModelScope.launch {
            val state = _uiState.value
            val draft = ReportEntity(
                userID = state.currentUserId ?: 1,
                photoUrl = state.attachedPhotoPath!!,
                problemType = state.problemType,
                severity = 1,
                title = state.title.ifBlank { "Draft Report" },
                content = state.draftContent,
                status = 2, // 2: Waiting/Draft
                district = state.district,
                location = state.location
            )
            reportRepository.insertReport(draft)
            _uiState.update { it.copy(submitSuccess = true) }
        }
    }
}
