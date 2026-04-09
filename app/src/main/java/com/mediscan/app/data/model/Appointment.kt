package com.mediscan.app.data.model

/**
 * Appointment stored in Firestore under appointments/{appointmentId}
 */
data class Appointment(
    val id: String = "",
    val patientId: String = "",
    val patientName: String = "",
    val patientProfileImageUrl: String? = null,
    val doctorId: String = "",
    val doctorName: String = "",
    val doctorProfileImageUrl: String? = null,
    val specialization: String = "",
    val dateTime: Long = 0L,
    val status: String = "scheduled",   // "scheduled", "confirmed", "completed", "cancelled"
    val complaint: String? = null,
    val createdAt: Long = System.currentTimeMillis(),

    // Doctor writes these when completing an appointment
    val doctorOrders: List<DoctorOrder> = emptyList(),
)
