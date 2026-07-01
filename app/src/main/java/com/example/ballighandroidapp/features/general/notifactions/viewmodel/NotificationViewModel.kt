package com.example.ballighandroidapp.features.citizen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ballighandroidapp.helpers.local.AppPreferences
import com.example.ballighandroidapp.helpers.local.data.entities.NotificationEntity
import com.example.ballighandroidapp.helpers.local.data.repository.NotificationRepository
import com.example.ballighandroidapp.helpers.local.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotificationUiState(
    val notifications: List<NotificationEntity> = emptyList(),
    val isLoading: Boolean = true
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val userRepository: UserRepository,
    private val appPreferences: AppPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    init {
        loadNotifications()
    }

    private fun loadNotifications() {
        viewModelScope.launch {
            val nationalId = appPreferences.loggedInNationalId ?: return@launch

            userRepository.getUserByNationalID(nationalId)
                .flatMapLatest { user ->
                    if (user == null) return@flatMapLatest flowOf(emptyList<NotificationEntity>())
                    notificationRepository.getNotificationsByUser(user.userID)
                }
                .collect { list ->
                    _uiState.update { it.copy(notifications = list, isLoading = false) }
                }
        }
    }

    fun markAsRead(notifId: Int) {
        viewModelScope.launch {
            notificationRepository.markAsRead(notifId)
        }
    }
}