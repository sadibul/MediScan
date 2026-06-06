package com.mediscan.app.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.mediscan.app.core.utils.NetworkResult
import com.mediscan.app.data.model.Appointment
import com.mediscan.app.data.model.DoctorOrder
import com.mediscan.app.data.model.Notification
import com.mediscan.app.data.model.Prescription
import com.mediscan.app.data.model.Reminder
import com.mediscan.app.data.model.User
import com.mediscan.app.data.repository.AppointmentRepository
import com.mediscan.app.data.repository.NotificationRepository
import com.mediscan.app.data.repository.PrescriptionRepository
import com.mediscan.app.data.repository.ReminderRepository
import com.mediscan.app.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * DoctorViewModel — manages doctor portal state.
 * Used by DoctorMainScreen, DoctorAppointmentsScreen, DoctorRecordsScreen, DoctorProfileScreen.
 */
@HiltViewModel
class DoctorViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val appointmentRepository: AppointmentRepository,
    private val prescriptionRepository: PrescriptionRepository,
    private val notificationRepository: NotificationRepository,
    private val reminderRepository: ReminderRepository,
    private val auth: FirebaseAuth,
) : ViewModel() {

    // Doctor profile
    private val _doctorProfile = MutableStateFlow<NetworkResult<User>>(NetworkResult.Idle)
    val doctorProfile: StateFlow<NetworkResult<User>> = _doctorProfile

    // Doctor's appointments
    private val _appointments = MutableStateFlow<NetworkResult<List<Appointment>>>(NetworkResult.Idle)
    val appointments: StateFlow<NetworkResult<List<Appointment>>> = _appointments

    // Update appointment status state
    private val _updateAppointmentState = MutableStateFlow<NetworkResult<Unit>>(NetworkResult.Idle)
    val updateAppointmentState: StateFlow<NetworkResult<Unit>> = _updateAppointmentState

    // Profile update state
    private val _updateProfileState = MutableStateFlow<NetworkResult<Unit>>(NetworkResult.Idle)
    val updateProfileState: StateFlow<NetworkResult<Unit>> = _updateProfileState

    // Change password state
    private val _changePasswordState = MutableStateFlow<NetworkResult<Unit>>(NetworkResult.Idle)
    val changePasswordState: StateFlow<NetworkResult<Unit>> = _changePasswordState

    // Profile image upload state
    private val _uploadImageState = MutableStateFlow<NetworkResult<String>>(NetworkResult.Idle)
    val uploadImageState: StateFlow<NetworkResult<String>> = _uploadImageState

    // Analytics data — appointment counts per month (last 6 months)
    private val _monthlyAppointmentCounts = MutableStateFlow<List<Pair<String, Int>>>(emptyList())
    val monthlyAppointmentCounts: StateFlow<List<Pair<String, Int>>> = _monthlyAppointmentCounts

    // Total counts for stats cards
    private val _totalPatients = MutableStateFlow(0)
    val totalPatients: StateFlow<Int> = _totalPatients

    private val _completedAppointments = MutableStateFlow(0)
    val completedAppointments: StateFlow<Int> = _completedAppointments

    private val _pendingAppointments = MutableStateFlow(0)
    val pendingAppointments: StateFlow<Int> = _pendingAppointments

    // ── Patient detail / records (for confirmed appointment flow) ──
    private val _patientProfile = MutableStateFlow<NetworkResult<User>>(NetworkResult.Idle)
    val patientProfile: StateFlow<NetworkResult<User>> = _patientProfile

    private val _patientPrescriptions = MutableStateFlow<NetworkResult<List<Prescription>>>(NetworkResult.Idle)
    val patientPrescriptions: StateFlow<NetworkResult<List<Prescription>>> = _patientPrescriptions

    private val _patientReminders = MutableStateFlow<NetworkResult<List<Reminder>>>(NetworkResult.Idle)
    val patientReminders: StateFlow<NetworkResult<List<Reminder>>> = _patientReminders

    private val _completeWithOrdersState = MutableStateFlow<NetworkResult<Unit>>(NetworkResult.Idle)
    val completeWithOrdersState: StateFlow<NetworkResult<Unit>> = _completeWithOrdersState

    init {
        loadDoctorProfile()
        loadAppointments()
    }

    /** Load doctor profile from Firestore */
    fun loadDoctorProfile() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _doctorProfile.value = NetworkResult.Loading
            _doctorProfile.value = userRepository.getUserProfile(uid)
        }
    }

    /** Load all doctor's appointments */
    fun loadAppointments() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _appointments.value = NetworkResult.Loading
            val result = appointmentRepository.getDoctorAppointments(uid)
            _appointments.value = result

            // Compute analytics if successful
            if (result is NetworkResult.Success) {
                computeAnalytics(result.data)
            }
        }
    }

    /** Accept an appointment */
    fun acceptAppointment(appointmentId: String) {
        viewModelScope.launch {
            val appointment = findAppointment(appointmentId)
            _updateAppointmentState.value = NetworkResult.Loading
            _updateAppointmentState.value = appointmentRepository.updateAppointmentStatus(appointmentId, "confirmed")
            loadAppointments()
            if (_updateAppointmentState.value is NetworkResult.Success && appointment != null) {
                val doctorName = (_doctorProfile.value as? NetworkResult.Success)?.data?.fullName ?: "Your doctor"
                notificationRepository.sendNotification(
                    Notification(
                        recipientId = appointment.patientId,
                        senderId = auth.currentUser?.uid ?: "",
                        senderName = doctorName,
                        type = "appointment_accepted",
                        title = "Appointment Confirmed",
                        message = "Dr. $doctorName has confirmed your appointment.",
                        appointmentId = appointmentId,
                    )
                )
            }
        }
    }

    /** Cancel / reject an appointment */
    fun cancelAppointment(appointmentId: String) {
        viewModelScope.launch {
            val appointment = findAppointment(appointmentId)
            _updateAppointmentState.value = NetworkResult.Loading
            _updateAppointmentState.value = appointmentRepository.updateAppointmentStatus(appointmentId, "cancelled")
            loadAppointments()
            if (_updateAppointmentState.value is NetworkResult.Success && appointment != null) {
                val doctorName = (_doctorProfile.value as? NetworkResult.Success)?.data?.fullName ?: "Your doctor"
                notificationRepository.sendNotification(
                    Notification(
                        recipientId = appointment.patientId,
                        senderId = auth.currentUser?.uid ?: "",
                        senderName = doctorName,
                        type = "appointment_cancelled",
                        title = "Appointment Cancelled",
                        message = "Dr. $doctorName has cancelled your appointment.",
                        appointmentId = appointmentId,
                    )
                )
            }
        }
    }

    /** Mark appointment as completed */
    fun completeAppointment(appointmentId: String) {
        viewModelScope.launch {
            val appointment = findAppointment(appointmentId)
            _updateAppointmentState.value = NetworkResult.Loading
            _updateAppointmentState.value = appointmentRepository.updateAppointmentStatus(appointmentId, "completed")
            loadAppointments()
            if (_updateAppointmentState.value is NetworkResult.Success && appointment != null) {
                val doctorName = (_doctorProfile.value as? NetworkResult.Success)?.data?.fullName ?: "Your doctor"
                notificationRepository.sendNotification(
                    Notification(
                        recipientId = appointment.patientId,
                        senderId = auth.currentUser?.uid ?: "",
                        senderName = doctorName,
                        type = "appointment_completed",
                        title = "Appointment Completed",
                        message = "Dr. $doctorName has completed your appointment.",
                        appointmentId = appointmentId,
                    )
                )
            }
        }
    }

    /** Helper to find appointment from loaded list */
    private fun findAppointment(appointmentId: String): Appointment? {
        return (_appointments.value as? NetworkResult.Success)?.data?.find { it.id == appointmentId }
    }

    fun resetUpdateAppointmentState() {
        _updateAppointmentState.value = NetworkResult.Idle
    }

    /** Update doctor profile */
    fun updateDoctorProfile(user: User) {
        viewModelScope.launch {
            _updateProfileState.value = NetworkResult.Loading
            _updateProfileState.value = userRepository.updateUserProfile(user)
            if (_updateProfileState.value is NetworkResult.Success) {
                loadDoctorProfile()
                // Sync updated name/image/specialization into all appointment documents
                userRepository.syncAppointmentsWithProfile(user)
            }
        }
    }

    fun resetUpdateProfileState() {
        _updateProfileState.value = NetworkResult.Idle
    }

    /** Change password — reauthenticate then update */
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
                val credential = EmailAuthProvider.getCredential(email, currentPassword)
                user.reauthenticate(credential).await()
                user.updatePassword(newPassword).await()
                _changePasswordState.value = NetworkResult.Success(Unit)
            } catch (e: Exception) {
                val msg = when {
                    e.message?.contains("INVALID_LOGIN_CREDENTIALS", true) == true ||
                    e.message?.contains("wrong-password", true) == true ->
                        "Current password is incorrect"
                    else -> e.message ?: "Failed to change password"
                }
                _changePasswordState.value = NetworkResult.Error(msg)
            }
        }
    }

    fun resetChangePasswordState() {
        _changePasswordState.value = NetworkResult.Idle
    }

    /** Upload profile image */
    fun uploadProfileImage(imageUri: Uri) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _uploadImageState.value = NetworkResult.Loading
            val result = userRepository.uploadProfileImage(uid, imageUri)
            _uploadImageState.value = result
            if (result is NetworkResult.Success) {
                val current = (_doctorProfile.value as? NetworkResult.Success)?.data
                if (current != null) {
                    val updated = current.copy(profileImageUrl = result.data)
                    userRepository.updateUserProfile(updated)
                    loadDoctorProfile()
                    // Sync updated image into all appointment documents
                    userRepository.syncAppointmentsWithProfile(updated)
                }
            }
        }
    }

    fun resetUploadImageState() {
        _uploadImageState.value = NetworkResult.Idle
    }

    /** Get current UID */
    fun getCurrentUid(): String = auth.currentUser?.uid ?: ""

    /** Get display name */
    fun getDisplayName(): String = auth.currentUser?.displayName ?: ""

    // ═══════════════════════════════════════════════════
    // Patient Detail / Records (from Confirmed flow)
    // ═══════════════════════════════════════════════════

    /** Load a patient's profile by their UID */
    fun loadPatientProfile(patientId: String) {
        viewModelScope.launch {
            _patientProfile.value = NetworkResult.Loading
            _patientProfile.value = userRepository.getUserProfile(patientId)
        }
    }

    /** Load all prescriptions for a patient (for the chart) */
    fun loadPatientPrescriptions(patientId: String) {
        viewModelScope.launch {
            _patientPrescriptions.value = NetworkResult.Loading
            _patientPrescriptions.value = prescriptionRepository.getPrescriptions(patientId)
        }
    }

    /** Load active reminders for a patient (for Patient Current Medicine section) */
    fun loadPatientReminders(patientId: String) {
        viewModelScope.launch {
            _patientReminders.value = NetworkResult.Loading
            _patientReminders.value = reminderRepository.getActiveReminders(patientId)
        }
    }

    /** Complete an appointment with doctor orders + save as prescription for patient's Docs */
    fun completeAppointmentWithOrders(
        appointmentId: String,
        orders: List<DoctorOrder>,
        appointment: Appointment,
    ) {
        viewModelScope.launch {
            _completeWithOrdersState.value = NetworkResult.Loading
            _completeWithOrdersState.value = appointmentRepository.completeWithOrders(appointmentId, orders)
            if (_completeWithOrdersState.value is NetworkResult.Success) {
                // Also save as a Prescription in the patient's Docs
                val medications = orders.map { order ->
                    com.mediscan.app.data.model.Medication(
                        medicine = order.medicine,
                        doseStrength = order.doseStrength,
                        schedule = order.doseSchedule,
                    )
                }
                val tests = orders.mapNotNull { it.test?.takeIf { t -> t.isNotBlank() } }
                val doctorProfile = (_doctorProfile.value as? NetworkResult.Success)?.data
                // Use current date as visit date (the day doctor completes it)
                val currentDate = System.currentTimeMillis()
                // Use the patient's complaint/disease as diagnosis
                val complaintDiagnosis = appointment.complaint?.trim()?.takeIf { it.isNotBlank() }
                val diagList = complaintDiagnosis?.split(",")?.map { it.trim() }?.filter { it.isNotBlank() } ?: emptyList()
                val prescription = Prescription(
                    patientId = appointment.patientId,
                    doctorName = doctorProfile?.fullName ?: appointment.doctorName,
                    hospital = doctorProfile?.hospital,
                    visitDate = currentDate,
                    diagnosis = complaintDiagnosis,
                    diagnoses = diagList,
                    medications = medications,
                    tests = tests,
                    isDoctorPrescription = true,
                    appointmentId = appointmentId,
                    createdAt = currentDate,
                )
                prescriptionRepository.savePrescription(prescription)
                loadAppointments()
                // Notify patient about prescription
                val doctorName = doctorProfile?.fullName ?: appointment.doctorName
                notificationRepository.sendNotification(
                    Notification(
                        recipientId = appointment.patientId,
                        senderId = auth.currentUser?.uid ?: "",
                        senderName = doctorName,
                        type = "prescription_added",
                        title = "Prescription Added",
                        message = "Dr. $doctorName has completed your appointment and added a prescription.",
                        appointmentId = appointmentId,
                    )
                )
            }
        }
    }

    fun resetCompleteWithOrdersState() {
        _completeWithOrdersState.value = NetworkResult.Idle
    }

    fun resetPatientProfile() {
        _patientProfile.value = NetworkResult.Idle
    }

    fun resetPatientPrescriptions() {
        _patientPrescriptions.value = NetworkResult.Idle
    }

    fun resetPatientReminders() {
        _patientReminders.value = NetworkResult.Idle
    }

    // ── Analytics computation ──
    private fun computeAnalytics(appointments: List<Appointment>) {
        val uniquePatients = appointments.map { it.patientId }.distinct().size
        _totalPatients.value = uniquePatients
        _completedAppointments.value = appointments.count { it.status == "completed" }
        _pendingAppointments.value = appointments.count { it.status == "scheduled" || it.status == "confirmed" }

        // Monthly counts for last 6 months
        val cal = java.util.Calendar.getInstance()
        val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        val monthlyCounts = mutableListOf<Pair<String, Int>>()

        for (i in 5 downTo 0) {
            val c = java.util.Calendar.getInstance()
            c.add(java.util.Calendar.MONTH, -i)
            val month = c.get(java.util.Calendar.MONTH)
            val year = c.get(java.util.Calendar.YEAR)

            val count = appointments.count { appt ->
                val apptCal = java.util.Calendar.getInstance().apply { timeInMillis = appt.dateTime }
                apptCal.get(java.util.Calendar.MONTH) == month &&
                        apptCal.get(java.util.Calendar.YEAR) == year
            }
            monthlyCounts.add("${monthNames[month]}" to count)
        }
        _monthlyAppointmentCounts.value = monthlyCounts
    }
}
