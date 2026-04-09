package com.mediscan.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import javax.inject.Inject

/**
 * BookingViewModel — manages doctor search, doctor detail, appointment booking,
 * and patient-side appointment list.
 */
@HiltViewModel
class BookingViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val appointmentRepository: AppointmentRepository,
    private val notificationRepository: NotificationRepository,
    private val auth: FirebaseAuth,
) : ViewModel() {

    // ── Doctor search ──
    private val _doctors = MutableStateFlow<NetworkResult<List<User>>>(NetworkResult.Idle)
    val doctors: StateFlow<NetworkResult<List<User>>> = _doctors

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedSpecialization = MutableStateFlow("All")
    val selectedSpecialization: StateFlow<String> = _selectedSpecialization

    // ── Doctor detail ──
    private val _doctorDetail = MutableStateFlow<NetworkResult<User>>(NetworkResult.Idle)
    val doctorDetail: StateFlow<NetworkResult<User>> = _doctorDetail

    // ── Booking state ──
    private val _bookingState = MutableStateFlow<NetworkResult<Unit>>(NetworkResult.Idle)
    val bookingState: StateFlow<NetworkResult<Unit>> = _bookingState

    // ── Patient appointments ──
    private val _patientAppointments = MutableStateFlow<NetworkResult<List<Appointment>>>(NetworkResult.Idle)
    val patientAppointments: StateFlow<NetworkResult<List<Appointment>>> = _patientAppointments

    // ── Cancel appointment state ──
    private val _cancelState = MutableStateFlow<NetworkResult<Unit>>(NetworkResult.Idle)
    val cancelState: StateFlow<NetworkResult<Unit>> = _cancelState

    init {
        loadDoctors()
    }

    // ═══════════════════════════════════════════════════
    // Doctor Search
    // ═══════════════════════════════════════════════════

    fun loadDoctors() {
        viewModelScope.launch {
            _doctors.value = NetworkResult.Loading
            _doctors.value = userRepository.getDoctors()
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateSpecializationFilter(specialization: String) {
        _selectedSpecialization.value = specialization
    }

    /** Get filtered doctor list based on search query and specialization */
    fun getFilteredDoctors(allDoctors: List<User>): List<User> {
        val query = _searchQuery.value.lowercase().trim()
        val spec = _selectedSpecialization.value

        return allDoctors.filter { doctor ->
            val matchesSearch = query.isEmpty() ||
                    doctor.fullName.lowercase().contains(query) ||
                    (doctor.specialization?.lowercase()?.contains(query) == true) ||
                    (doctor.hospital?.lowercase()?.contains(query) == true)

            val matchesSpec = spec == "All" ||
                    doctor.specialization.equals(spec, ignoreCase = true)

            matchesSearch && matchesSpec
        }
    }

    /** Extract unique specializations from loaded doctors */
    fun getSpecializations(doctors: List<User>): List<String> {
        val specs = doctors.mapNotNull { it.specialization }
            .filter { it.isNotBlank() }
            .distinct()
            .sorted()
        return listOf("All") + specs
    }

    // ═══════════════════════════════════════════════════
    // Doctor Detail
    // ═══════════════════════════════════════════════════

    fun loadDoctorDetail(doctorId: String) {
        viewModelScope.launch {
            _doctorDetail.value = NetworkResult.Loading
            _doctorDetail.value = userRepository.getDoctorById(doctorId)
        }
    }

    // ═══════════════════════════════════════════════════
    // Appointment Booking
    // ═══════════════════════════════════════════════════

    fun bookAppointment(
        doctor: User,
        dateTimeMillis: Long,
        complaint: String,
    ) {
        val currentUser = auth.currentUser ?: return
        viewModelScope.launch {
            _bookingState.value = NetworkResult.Loading

            // Fetch patient name and profile image
            val patientProfile = when (val profileResult = userRepository.getUserProfile(currentUser.uid)) {
                is NetworkResult.Success -> profileResult.data
                else -> null
            }
            val patientName = patientProfile?.fullName ?: currentUser.displayName ?: "Patient"
            val patientProfileImageUrl = patientProfile?.profileImageUrl

            val appointment = Appointment(
                patientId = currentUser.uid,
                patientName = patientName,
                patientProfileImageUrl = patientProfileImageUrl,
                doctorId = doctor.id,
                doctorName = doctor.fullName,
                doctorProfileImageUrl = doctor.profileImageUrl,
                specialization = doctor.specialization ?: "",
                dateTime = dateTimeMillis,
                status = "scheduled",
                complaint = complaint.ifBlank { null },
                createdAt = System.currentTimeMillis(),
            )

            _bookingState.value = appointmentRepository.bookAppointment(appointment)
            // Send notification to doctor
            if (_bookingState.value is NetworkResult.Success) {
                notificationRepository.sendNotification(
                    Notification(
                        recipientId = doctor.id,
                        senderId = currentUser.uid,
                        senderName = patientName,
                        type = "appointment_booked",
                        title = "New Appointment Request",
                        message = "$patientName has booked an appointment with you.",
                    )
                )
            }
        }
    }

    fun resetBookingState() {
        _bookingState.value = NetworkResult.Idle
    }

    // ═══════════════════════════════════════════════════
    // Patient Appointments
    // ═══════════════════════════════════════════════════

    fun loadPatientAppointments() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _patientAppointments.value = NetworkResult.Loading
            _patientAppointments.value = appointmentRepository.getPatientAppointments(uid)
        }
    }

    fun cancelAppointment(appointmentId: String) {
        viewModelScope.launch {
            _cancelState.value = NetworkResult.Loading
            // Get appointment details before cancelling (for notification)
            val appointments = (_patientAppointments.value as? NetworkResult.Success)?.data
            val appointment = appointments?.find { it.id == appointmentId }
            _cancelState.value = appointmentRepository.cancelAppointment(appointmentId)
            if (_cancelState.value is NetworkResult.Success && appointment != null) {
                loadPatientAppointments()
                // Notify the doctor
                notificationRepository.sendNotification(
                    Notification(
                        recipientId = appointment.doctorId,
                        senderId = auth.currentUser?.uid ?: "",
                        senderName = appointment.patientName,
                        type = "appointment_cancelled",
                        title = "Appointment Cancelled",
                        message = "${appointment.patientName} has cancelled their appointment.",
                        appointmentId = appointmentId,
                    )
                )
            }
        }
    }

    fun resetCancelState() {
        _cancelState.value = NetworkResult.Idle
    }
}
