package com.mediscan.app.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mediscan.app.core.utils.NetworkResult
import com.mediscan.app.data.model.Notification
import com.mediscan.app.data.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "NotificationVM"

/**
 * NotificationViewModel — manages notification state for both patients and doctors.
 * Provides real-time unread count and notification list.
 */
@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val auth: FirebaseAuth,
) : ViewModel() {

    private val _notifications = MutableStateFlow<NetworkResult<List<Notification>>>(NetworkResult.Idle)
    val notifications: StateFlow<NetworkResult<List<Notification>>> = _notifications

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount

    private var observeJob: Job? = null

    init {
        startObserving()
    }

    /** Start observing unread count in real-time */
    fun startObserving() {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            Log.w(TAG, "startObserving: uid is null, cannot observe unread count")
            return
        }
        // Cancel any previous observer to avoid duplicates
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            Log.d(TAG, "startObserving: listening for unread notifications for uid=$uid")
            notificationRepository.observeUnreadCount(uid).collectLatest { count ->
                Log.d(TAG, "unreadCount updated: $count")
                _unreadCount.value = count
            }
        }
    }

    /** Load all notifications */
    fun loadNotifications() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _notifications.value = NetworkResult.Loading
            _notifications.value = notificationRepository.getNotifications(uid)
        }
    }

    /** Mark a single notification as read */
    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            notificationRepository.markAsRead(notificationId)
            loadNotifications()
        }
    }

    /** Mark all notifications as read */
    fun markAllAsRead() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            notificationRepository.markAllAsRead(uid)
            loadNotifications()
        }
    }

    /** Delete all notifications */
    fun clearAll() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            notificationRepository.clearAllNotifications(uid)
            _notifications.value = NetworkResult.Success(emptyList())
        }
    }
}
