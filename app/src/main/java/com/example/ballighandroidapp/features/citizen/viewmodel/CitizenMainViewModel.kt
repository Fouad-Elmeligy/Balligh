package com.example.ballighandroidapp.features.citizen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ballighandroidapp.helpers.local.AppPreferences
import com.example.ballighandroidapp.helpers.local.data.entities.ReportEntity
import com.example.ballighandroidapp.helpers.local.data.repository.ReportRepository
import com.example.ballighandroidapp.helpers.local.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

@OptIn(ExperimentalCoroutinesApi::class)
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

    private val _selectedFilterFlow = MutableStateFlow(0)

    init {
        loadHomeData()
        observeAndFilterReports()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            val nationalId = appPreferences.loggedInNationalId ?: return@launch

            userRepository.getUserByNationalID(nationalId).flatMapLatest { user ->
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
        _selectedFilterFlow.value = filter
        _reportsState.update { it.copy(selectedFilter = filter) }
    }

    private fun observeAndFilterReports() {
        viewModelScope.launch {
            val nationalId = appPreferences.loggedInNationalId ?: return@launch

            userRepository.getUserByNationalID(nationalId)
                .flatMapLatest { user ->
                    if (user == null) return@flatMapLatest flowOf(emptyList<ReportEntity>())

                    reportRepository.getReportsByUser(user.userID)
                        .combine(_selectedFilterFlow) { reportsList, currentFilter ->
                            when (currentFilter) {
                                0 -> reportsList // الكل
                                1 -> reportsList.filter { it.status == 1 } // Under Review
                                2 -> reportsList.filter { it.status == 2 } // Waiting
                                3 -> reportsList.filter { it.status == 3 } // Solved
                                else -> reportsList
                            }
                        }
                }.collect { filteredList ->
                    _reportsState.update { it.copy(reports = filteredList) }
                }
        }
    }
}