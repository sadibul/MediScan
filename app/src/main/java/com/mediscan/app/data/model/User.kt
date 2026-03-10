package com.mediscan.app.data.model

/**
 * User data model — stored in Firestore under users/{uid}
 * Shared between Patient and Doctor roles.
 * Role-specific fields are nullable (null if not applicable).
 */
data class User(
    val id: String = "",                        // Firebase UID
    val email: String = "",
    val fullName: String = "",
    val phone: String = "",
    val profileImageUrl: String? = null,
    val userType: String = "patient",           // "patient" or "doctor"
    val createdAt: Long = System.currentTimeMillis(),

    // Patient-specific
    val dateOfBirth: String? = null,
    val bloodGroup: String? = null,
    val address: String? = null,
    val emergencyContact: String? = null,
    val height: String? = null,             // e.g. "5.9" (in ft)
    val weight: String? = null,             // e.g. "68" (in kg)

    // Doctor-specific
    val licenseNumber: String? = null,
    val specialization: String? = null,
    val hospital: String? = null,
    val consultationFee: String? = null,
    val availableDays: List<String>? = null,
    val availableTimeRange: String? = null,
)
