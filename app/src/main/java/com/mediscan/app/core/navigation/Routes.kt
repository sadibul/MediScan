package com.mediscan.app.core.navigation

/**
 * Defines all navigation route constants for the MediScan app.
 * Used by NavGraph.kt to set up navigation destinations.
 */
object Routes {
    // Auth Flow
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val SIGN_UP = "sign_up"

    // Patient Flow
    const val PATIENT_MAIN = "patient_main"
    const val PATIENT_HOME = "patient_home"
    const val PATIENT_SCAN = "patient_scan"
    const val PATIENT_DOCS = "patient_docs"
    const val PATIENT_PROFILE = "patient_profile"
    const val PATIENT_EDIT_PROFILE = "patient_edit_profile"
    const val PRESCRIPTION_DETAIL = "prescription_detail/{prescriptionId}"

    // Doctor Flow
    const val DOCTOR_MAIN = "doctor_main"
    const val DOCTOR_APPOINTMENTS = "doctor_appointments"
    const val DOCTOR_RECORDS = "doctor_records"
    const val DOCTOR_PROFILE = "doctor_profile"

    // Shared
    const val DOCTOR_SEARCH = "doctor_search"
    const val DOCTOR_DETAIL = "doctor_detail/{doctorId}?fromAppointment={fromAppointment}&appointmentDateTime={appointmentDateTime}&appointmentComplaint={appointmentComplaint}"
    const val BOOK_APPOINTMENT = "book_appointment/{doctorId}"
    const val PATIENT_APPOINTMENTS = "patient_appointments"
    const val NEARBY_HOSPITALS = "nearby_hospitals"

    // Doctor ↔ Patient prescription flow
    const val PATIENT_RECORDS = "patient_records/{patientId}"
    const val DOCTOR_ORDERS = "doctor_orders"

    // Notifications
    const val NOTIFICATIONS = "notifications"

    // Helper functions to create routes with arguments
    fun prescriptionDetail(prescriptionId: String) =
        "prescription_detail/$prescriptionId"

    fun doctorDetail(doctorId: String) =
        "doctor_detail/$doctorId"

    fun doctorDetailFromAppointment(
        doctorId: String,
        appointmentDateTime: Long,
        appointmentComplaint: String?
    ): String {
        val encoded = java.net.URLEncoder.encode(appointmentComplaint ?: "", "UTF-8")
        return "doctor_detail/$doctorId?fromAppointment=true&appointmentDateTime=$appointmentDateTime&appointmentComplaint=$encoded"
    }

    fun bookAppointment(doctorId: String) =
        "book_appointment/$doctorId"

    fun patientRecords(patientId: String) =
        "patient_records/$patientId"
}
