package com.mediscan.app.data.model

/**
 * Prescription stored in Firestore under prescriptions/{prescriptionId}
 */
data class Prescription(
    val id: String = "",
    val patientId: String = "",
    val doctorName: String? = null,
    val hospital: String? = null,
    val visitDate: Long = System.currentTimeMillis(),
    val diagnosis: String? = null,
    val diagnoses: List<String> = emptyList(),
    val tests: List<String> = emptyList(),
    val medications: List<Medication> = emptyList(),
    val imageUrl: String? = null,
    val rawExtractionJson: String? = null,
    val createdAt: Long = System.currentTimeMillis(),

    /** True if this prescription was written by a doctor (not AI-scanned). */
    val isDoctorPrescription: Boolean = false,
    /** The appointment ID that generated this prescription (doctor flow only). */
    val appointmentId: String? = null,
)
