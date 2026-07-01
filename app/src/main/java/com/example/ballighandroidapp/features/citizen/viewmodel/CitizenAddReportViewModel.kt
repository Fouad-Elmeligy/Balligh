package com.example.ballighandroidapp.features.citizen.viewmodel

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
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
 * UI State for the Add/Edit/View Report screen.
 * Supports two modes:
 *  - CREATE: reportId == null, isEditMode == false, blank form.
 *  - EDIT/VIEW: reportId != null, isEditMode == true, pre-filled from Room.
 */
data class CitizenAddReportUiState(
    // ── Mode ─────────────────────────────────────────────────────────────
    val reportId: Int? = null,
    val isEditMode: Boolean = false,
    val isLoadingReport: Boolean = false,

    // ── Original entity kept for updates/deletes ─────────────────────────
    val originalReport: ReportEntity? = null,

    // ── Form fields ──────────────────────────────────────────────────────
    val attachedPhotoPath: String? = null,
    val title: String = "",
    val district: String = "Nasr City",
    val location: String = "",
    val problemType: String = "General",
    val draftContent: String = "",
    val currentUserId: Int? = null,

    // ── Status (view-only in edit mode) ──────────────────────────────────
    val reportStatus: Int = 1,

    // ── UI state / flags ─────────────────────────────────────────────────
    val isLocationEditing: Boolean = true,
    val isGeneratingDraft: Boolean = false,
    val isSubmitting: Boolean = false,
    val isDeleting: Boolean = false,
    val submitSuccess: Boolean = false,
    val deleteSuccess: Boolean = false,
    val errorMessageResId: Int? = null
)

@HiltViewModel
class CitizenAddReportViewModel @Inject constructor(
    private val reportRepository: ReportRepository,
    private val userRepository: UserRepository,
    private val appPreferences: AppPreferences,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(CitizenAddReportUiState())
    val uiState: StateFlow<CitizenAddReportUiState> = _uiState.asStateFlow()

    init {
        fetchCurrentUserSession()
        
        // ─── 1. Extract reportId from SavedStateHandle ───
        val reportId: Int = savedStateHandle["reportId"] ?: -1
        if (reportId != -1) {
            loadReportForEditing(reportId)
        }
    }

    private fun fetchCurrentUserSession() {
        val nationalId = appPreferences.loggedInNationalId ?: return
        viewModelScope.launch {
            userRepository.getUserByNationalID(nationalId).collectLatest { user ->
                _uiState.update { it.copy(currentUserId = user?.userID) }
            }
        }
    }

    // ─── 2. EDIT MODE: load existing report ───────────────────────────────────

    fun loadReportForEditing(id: Int) {
        _uiState.update { it.copy(isLoadingReport = true) }
        viewModelScope.launch {
            val report = reportRepository.getReportById(id)
            if (report != null) {
                _uiState.update {
                    it.copy(
                        isLoadingReport = false,
                        isEditMode = true,
                        reportId = report.reportID,
                        originalReport = report,
                        attachedPhotoPath = report.photoUrl,
                        title = report.title,
                        district = report.district,
                        location = report.location ?: "",
                        problemType = report.problemType,
                        draftContent = report.content,
                        reportStatus = report.status,
                        isLocationEditing = false
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoadingReport = false,
                        errorMessageResId = R.string.error_something_went_wrong
                    )
                }
            }
        }
    }

    // ─── Form Update Methods ────────────────────────────────────────────────

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

    fun onLocationEditToggle() {
        _uiState.update { it.copy(isLocationEditing = !it.isLocationEditing) }
    }

    fun onProblemTypeChanged(newType: String) {
        _uiState.update { it.copy(problemType = newType) }
    }

    fun onDraftContentChanged(newContent: String) {
        _uiState.update { it.copy(draftContent = newContent) }
    }

    fun onErrorDismissed() {
        _uiState.update { it.copy(errorMessageResId = null) }
    }

    // ─── Validation ──────────────────────────────────────────────────────

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

    // ─── Save Logic (Insert or Update) ────────────────────────────────────

    fun sendReport() {
        val state = _uiState.value
        if (state.isEditMode) {
            updateReport()
        } else {
            insertNewReport()
        }
    }

    private fun insertNewReport() {
        if (!validate()) return

        val state = _uiState.value
        _uiState.update { it.copy(isSubmitting = true) }

        viewModelScope.launch {
            try {
                val newReport = ReportEntity(
                    userID = state.currentUserId!!,
                    photoUrl = state.attachedPhotoPath!!,
                    problemType = state.problemType,
                    severity = 2,
                    title = state.title.trim(),
                    content = state.draftContent.trim(),
                    status = 1,
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

    fun updateReport() {
        val state = _uiState.value
        val original = state.originalReport ?: return
        if (!validate()) return

        _uiState.update { it.copy(isSubmitting = true) }
        viewModelScope.launch {
            try {
                val updated = original.copy(
                    photoUrl = state.attachedPhotoPath!!,
                    problemType = state.problemType,
                    title = state.title.trim(),
                    content = state.draftContent.trim(),
                    district = state.district,
                    location = state.location.ifBlank { state.district }
                )
                reportRepository.updateReport(updated)
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

    fun deleteReport() {
        val original = _uiState.value.originalReport ?: return
        _uiState.update { it.copy(isDeleting = true) }
        viewModelScope.launch {
            try {
                reportRepository.deleteReport(original)
                _uiState.update { it.copy(isDeleting = false, deleteSuccess = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isDeleting = false,
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
                status = 2,
                district = state.district,
                location = state.location
            )
            reportRepository.insertReport(draft)
            _uiState.update { it.copy(submitSuccess = true) }
        }
    }
}
