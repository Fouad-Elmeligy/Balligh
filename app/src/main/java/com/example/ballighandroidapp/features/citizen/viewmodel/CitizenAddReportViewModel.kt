package com.example.ballighandroidapp.features.citizen.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ballighandroidapp.helpers.local.data.entities.ReportEntity
import com.example.ballighandroidapp.helpers.local.data.repository.ReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// ─── UI State ────────────────────────────────────────────────────────────────

data class CitizenAddReportUiState(
    val photoUri: Uri? = null,
    val location: String = "Nasr City, Abbas El Akkad St",
    val draftContent: String = "",
    val isLocationEditing: Boolean = false,
    val isGeneratingDraft: Boolean = false,
    val isSubmitting: Boolean = false,
    val submitSuccess: Boolean = false,
    val errorMessage: String? = null
)

// ─── ViewModel ───────────────────────────────────────────────────────────────

@HiltViewModel
class CitizenAddReportViewModel @Inject constructor(
    private val reportRepository: ReportRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CitizenAddReportUiState())
    val uiState: StateFlow<CitizenAddReportUiState> = _uiState.asStateFlow()

    // Called when the user picks or captures a photo
    fun onPhotoSelected(uri: Uri) {
        _uiState.update { it.copy(photoUri = uri) }
        generateDraftContent()
    }

    // Simulates AI draft generation from the selected photo
    private fun generateDraftContent() {
        _uiState.update { it.copy(isGeneratingDraft = true, draftContent = "") }
        viewModelScope.launch {
            // Simulated AI delay — replace with real Gemini API call
            kotlinx.coroutines.delay(1500)
            _uiState.update {
                it.copy(
                    isGeneratingDraft = false,
                    draftContent = "Dear Competent Authorities,\n\n" +
                        "Please be informed of a failure in one of the public street lighting poles " +
                        "at the location mentioned above (${it.location}). The failure is causing " +
                        "total darkness in the surrounding area, which may affect the safety of " +
                        "pedestrians and vehicles.\n\n" +
                        "We kindly request that you take the necessary measures for maintenance as " +
                        "soon as possible. Thank you for your cooperation."
                )
            }
        }
    }

    fun onLocationChanged(newLocation: String) {
        _uiState.update { it.copy(location = newLocation) }
    }

    fun onLocationEditToggle() {
        _uiState.update { it.copy(isLocationEditing = !it.isLocationEditing) }
    }

    fun onDraftContentChanged(newContent: String) {
        _uiState.update { it.copy(draftContent = newContent) }
    }

    fun onErrorDismissed() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    // Saves to Room as a draft (status = 2 = Waiting/Draft)
    fun saveAsDraft() {
        val state = _uiState.value
        if (state.photoUri == null) {
            _uiState.update { it.copy(errorMessage = "Please capture or upload a photo first.") }
            return
        }
        viewModelScope.launch {
            reportRepository.insertReport(
                ReportEntity(
                    userID = 1, // Replace with actual logged-in user ID
                    photoUrl = state.photoUri.toString(),
                    problemType = "Street Lighting",
                    severity = 2,
                    title = "Street Lighting Issue",
                    content = state.draftContent,
                    status = 2, // Waiting / Draft
                    district = "Nasr City",
                    location = state.location
                )
            )
            _uiState.update { it.copy(submitSuccess = true) }
        }
    }

    // Submits the report (status = 1 = UnderReview)
    fun sendReport() {
        val state = _uiState.value
        if (state.photoUri == null) {
            _uiState.update { it.copy(errorMessage = "Please capture or upload a photo first.") }
            return
        }
        if (state.draftContent.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Report content cannot be empty.") }
            return
        }
        _uiState.update { it.copy(isSubmitting = true) }
        viewModelScope.launch {
            reportRepository.insertReport(
                ReportEntity(
                    userID = 1, // Replace with actual logged-in user ID
                    photoUrl = state.photoUri.toString(),
                    problemType = "Street Lighting",
                    severity = 2,
                    title = "Street Lighting Issue",
                    content = state.draftContent,
                    status = 1, // UnderReview
                    district = "Nasr City",
                    location = state.location
                )
            )
            _uiState.update { it.copy(isSubmitting = false, submitSuccess = true) }
        }
    }
}
