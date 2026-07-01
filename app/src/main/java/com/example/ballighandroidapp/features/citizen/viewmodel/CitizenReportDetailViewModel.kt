package com.example.ballighandroidapp.features.citizen.viewmodel

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

// ─── Status Step Model ────────────────────────────────────────────────────────

data class ReportStatusStep(
    val labelRes: Int,
    val descriptionRes: Int,
    val isCompleted: Boolean,
    val isActive: Boolean
)

// ─── UI State ─────────────────────────────────────────────────────────────────

data class CitizenReportDetailUiState(
    val report: ReportEntity? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val noteText: String = "",
    val isNoteSubmitting: Boolean = false,
    val noteSubmitSuccess: Boolean = false
)

// ─── ViewModel ────────────────────────────────────────────────────────────────

@HiltViewModel
class CitizenReportDetailViewModel @Inject constructor(
    private val reportRepository: ReportRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CitizenReportDetailUiState())
    val uiState: StateFlow<CitizenReportDetailUiState> = _uiState.asStateFlow()

    fun loadReport(reportId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val report = reportRepository.getReportById(reportId)
            _uiState.update { it.copy(report = report, isLoading = false) }
        }
    }

    fun onNoteTextChanged(text: String) {
        _uiState.update { it.copy(noteText = text) }
    }

    fun onErrorDismissed() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun onFollowUp() {
        val report = _uiState.value.report ?: return
        _uiState.update { it.copy(isNoteSubmitting = true) }
        viewModelScope.launch {
            // Update report status to follow-up (status 2 = Waiting)
            reportRepository.updateReport(report.copy(status = 2))
            _uiState.update {
                it.copy(
                    isNoteSubmitting = false,
                    noteSubmitSuccess = true,
                    noteText = ""
                )
            }
        }
    }

    // Derives the status steps from the report's current status int
    fun getStatusSteps(status: Int): List<Pair<Int, Int>> {
        // Returns list of (labelRes placeholder, isCompletedUpTo)
        // Handled in the screen using hardcoded step logic per status int
        return emptyList()
    }
}
