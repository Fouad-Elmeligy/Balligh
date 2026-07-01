package com.example.ballighandroidapp.features.citizen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ballighandroidapp.helpers.local.AppPreferences
import com.example.ballighandroidapp.helpers.local.data.entities.ReportEntity
import com.example.ballighandroidapp.helpers.local.data.repository.ReportRepository
import com.example.ballighandroidapp.helpers.local.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CitizenHomeUiState(
    val userName: String? = null,
    val totalReports: Int = 0,
    val resolvedReports: Int = 0,
    val latestReports: List<ReportEntity> = emptyList()
)

data class CitizenReportsUiState(
    val reports: List<ReportEntity> = emptyList(),
    val selectedFilter: Int = 0 // 0: All, 1: Pending, 2: In Progress, 3: Completed
)

@HiltViewModel
class CitizenMainViewModel @Inject constructor(
    private val reportRepository: ReportRepository,
    private val userRepository: UserRepository,
    private val appPreferences: AppPreferences
) : ViewModel() {

    private val _homeState = MutableStateFlow(CitizenHomeUiState())
    val homeState: StateFlow<CitizenHomeUiState> = _homeState.asStateFlow()

    private val _reportsState = MutableStateFlow(CitizenReportsUiState())
    val reportsState: StateFlow<CitizenReportsUiState> = _reportsState.asStateFlow()

    init {
        loadHomeData()
        loadReports()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            val nationalId = appPreferences.loggedInNationalId ?: return@launch
            
            val userFlow = userRepository.getUserByNationalID(nationalId)
            
            userFlow.flatMapLatest { user ->
                if (user == null) return@flatMapLatest flowOf(CitizenHomeUiState())
                
                reportRepository.getReportsByUser(user.userID).map { list ->
                    val resolvedCount = list.count { it.status == 3 } // 3: Solved
                    CitizenHomeUiState(
                        userName = user.fullName,
                        totalReports = list.size,
                        resolvedReports = resolvedCount,
                        latestReports = list.take(2)
                    )
                }
            }.collect { state ->
                _homeState.value = state
            }
        }
    }

    fun setReportsFilter(filter: Int) {
        _reportsState.update { it.copy(selectedFilter = filter) }
        loadReports()
    }

    private fun loadReports() {
        viewModelScope.launch {
            val nationalId = appPreferences.loggedInNationalId ?: return@launch
            val user = userRepository.loginWithNationalID(nationalId, "") // This is just to get user ID, ideally fetch by ID
            // Actually let's use the flow more efficiently
            
            userRepository.getUserByNationalID(nationalId).collectLatest { user ->
                if (user == null) return@collectLatest
                
                val filter = _reportsState.value.selectedFilter
                reportRepository.getReportsByUser(user.userID).collect { list ->
                    val filteredList = when (filter) {
                        0 -> list // All
                        1 -> list.filter { it.status == 1 } // Pending -> UnderReview
                        2 -> list.filter { it.status == 2 } // In Progress -> Waiting
                        3 -> list.filter { it.status == 3 } // Completed -> Solved
                        else -> list
                    }
                    _reportsState.update { it.copy(reports = filteredList) }
                }
            }
        }
    }
}
