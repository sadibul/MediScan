package com.mediscan.app.data.model

/**
 * A single order/prescription item written by a doctor after an appointment.
 * Stored within an Appointment document in the `doctorOrders` list.
 */
data class DoctorOrder(
    val medicine: String = "",
    val test: String? = null,
    val doseStrength: String? = null,
    val doseSchedule: String? = null,
    val notes: String? = null,
)
