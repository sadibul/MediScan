package com.mediscan.app.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.mediscan.app.core.utils.NetworkResult
import com.mediscan.app.data.model.Appointment
import com.mediscan.app.data.model.Notification
import com.mediscan.app.data.model.User
import com.mediscan.app.data.repository.AppointmentRepository
import com.mediscan.app.data.repository.NotificationRepository
import com.mediscan.app.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * PatientViewModel — manages patient dashboard state.
 * Used by PatientHomeScreen, PatientProfileScreen, EditProfileScreen, ChangePasswordScreen.
 */
@HiltViewModel
class PatientViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val appointmentRepository: AppointmentRepository,
    private val notificationRepository: NotificationRepository,
    private val auth: FirebaseAuth,
) : ViewModel() {

    // Current user profile
    private val _userProfile = MutableStateFlow<NetworkResult<User>>(NetworkResult.Idle)
    val userProfile: StateFlow<NetworkResult<User>> = _userProfile

    // Upcoming appointments
    private val _appointments = MutableStateFlow<NetworkResult<List<Appointment>>>(NetworkResult.Idle)
    val appointments: StateFlow<NetworkResult<List<Appointment>>> = _appointments

    // Profile update state
    private val _updateProfileState = MutableStateFlow<NetworkResult<Unit>>(NetworkResult.Idle)
    val updateProfileState: StateFlow<NetworkResult<Unit>> = _updateProfileState

    // Change password state
    private val _changePasswordState = MutableStateFlow<NetworkResult<Unit>>(NetworkResult.Idle)
    val changePasswordState: StateFlow<NetworkResult<Unit>> = _changePasswordState

    // Profile image upload state
    private val _uploadImageState = MutableStateFlow<NetworkResult<String>>(NetworkResult.Idle)
    val uploadImageState: StateFlow<NetworkResult<String>> = _uploadImageState

    // Completed appointments with doctor orders
    private val _doctorOrders = MutableStateFlow<NetworkResult<List<Appointment>>>(NetworkResult.Idle)
    val doctorOrders: StateFlow<NetworkResult<List<Appointment>>> = _doctorOrders

    init {
        loadUserProfile()
        loadUpcomingAppointments()
    }

    /** Load user profile from Firestore */
    fun loadUserProfile() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _userProfile.value = NetworkResult.Loading
            _userProfile.value = userRepository.getUserProfile(uid)
        }
    }

    /** Load upcoming appointments */
    fun loadUpcomingAppointments() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _appointments.value = NetworkResult.Loading
            _appointments.value = userRepository.getUpcomingAppointments(uid)
        }
    }

    /** Update user profile */
    fun updateUserProfile(user: User) {
        viewModelScope.launch {
            _updateProfileState.value = NetworkResult.Loading
            _updateProfileState.value = userRepository.updateUserProfile(user)
            // Refresh profile after update and sync denormalized data in appointments
            if (_updateProfileState.value is NetworkResult.Success) {
                loadUserProfile()
                // Sync updated name/image into all appointment documents
                userRepository.syncAppointmentsWithProfile(user)
            }
        }
    }

    /** Cancel an appointment */
    fun cancelAppointment(appointmentId: String) {
        viewModelScope.launch {
            // Find appointment before cancelling to get doctorId
            val appointment = (_appointments.value as? NetworkResult.Success)?.data
                ?.find { it.id == appointmentId }
            userRepository.cancelAppointment(appointmentId)
            // Notify doctor
            if (appointment != null) {
                val patientName = (_userProfile.value as? NetworkResult.Success<User>)?.data?.fullName
                    ?: auth.currentUser?.displayName ?: "A patient"
                notificationRepository.sendNotification(
                    Notification(
                        recipientId = appointment.doctorId,
                        senderId = auth.currentUser?.uid ?: "",
                        senderName = patientName,
                        type = "appointment_cancelled",
                        title = "Appointment Cancelled",
                        message = "$patientName has cancelled their appointment.",
                        appointmentId = appointmentId,
                    )
                )
            }
            loadUpcomingAppointments() // refresh list
        }
    }

    /** Load completed appointments that have doctor orders */
    fun loadDoctorOrders() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _doctorOrders.value = NetworkResult.Loading
            val result = appointmentRepository.getCompletedAppointmentsForPatient(uid)
            // Filter only appointments that actually have doctor orders
            _doctorOrders.value = when (result) {
                is NetworkResult.Success -> {
                    NetworkResult.Success(result.data.filter { it.doctorOrders.isNotEmpty() })
                }
                else -> result
            }
        }
    }

    /** Reset update state (after showing success/error) */
    fun resetUpdateState() {
        _updateProfileState.value = NetworkResult.Idle
    }

    /**
     * Change password — re-authenticates with current password then updates.
     * Works only for email/password accounts (not Google sign-in).
     */
    fun changePassword(currentPassword: String, newPassword: String) {
        val user = auth.currentUser ?: run {
            _changePasswordState.value = NetworkResult.Error("Not signed in")
            return
        }
        val email = user.email ?: run {
            _changePasswordState.value = NetworkResult.Error("No email associated with account")
            return
        }
        viewModelScope.launch {
            _changePasswordState.value = NetworkResult.Loading
            try {
                // Re-authenticate
                val credential = EmailAuthProvider.getCredential(email, currentPassword)
                user.reauthenticate(credential).await()
                // Update password
                user.updatePassword(newPassword).await()
                _changePasswordState.value = NetworkResult.Success(Unit)
            } catch (e: Exception) {
                val msg = when {
                    e.message?.contains("INVALID_LOGIN_CREDENTIALS", true) == true ||
                    e.message?.contains("wrong-password", true) == true ->
                        "Current password is incorrect"
                    e.message?.contains("requires-recent-login", true) == true ->
                        "Please sign out and sign back in before changing your password"
                    else -> e.message ?: "Failed to change password"
                }
                _changePasswordState.value = NetworkResult.Error(msg)
            }
        }
    }

    /** Reset change password state */
    fun resetChangePasswordState() {
        _changePasswordState.value = NetworkResult.Idle
    }

    /**
     * Upload profile image to Firebase Storage and update user profile.
     */
    fun uploadProfileImage(imageUri: Uri) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _uploadImageState.value = NetworkResult.Loading
            val result = userRepository.uploadProfileImage(uid, imageUri)
            _uploadImageState.value = result
            // If upload succeeded, update the user profile with the new URL
            if (result is NetworkResult.Success) {
                val currentUser = (_userProfile.value as? NetworkResult.Success)?.data
                if (currentUser != null) {
                    val updatedUser = currentUser.copy(profileImageUrl = result.data)
                    userRepository.updateUserProfile(updatedUser)
                    loadUserProfile() // refresh
                    // Sync updated image into all appointment documents
                    userRepository.syncAppointmentsWithProfile(updatedUser)
                }
            }
        }
    }

    /** Reset upload image state */
    fun resetUploadImageState() {
        _uploadImageState.value = NetworkResult.Idle
    }

    /** Get current user display name (from Firebase Auth) */
    fun getDisplayName(): String {
        return auth.currentUser?.displayName ?: ""
    }

    /** Get current user UID */
    fun getCurrentUid(): String {
        return auth.currentUser?.uid ?: ""
    }
}
