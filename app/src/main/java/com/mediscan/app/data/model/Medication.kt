package com.mediscan.app.data.model

/**
 * Single medication entry from a prescription.
 */
data class Medication(
    val medicine: String = "",
    val doseStrength: String? = null,
    val schedule: String? = null,
    val duration: String? = null,
    val confidence: Double? = null,
)
