package com.mediscan.app.data.model

/**
 * Reminder stored in Firestore under reminders/{reminderId}.
 * Represents a medicine reminder set by a patient.
 *
 * @property id Firestore document ID
 * @property patientId The UID of the patient who created this reminder
 * @property medicineName Name of the medicine
 * @property description Optional usage instructions / notes
 * @property timeDurationDays How many days this reminder should run (e.g. 30 = next 30 days)
 * @property medicineTimes List of times in "HH:mm" 24-hour format (e.g. ["09:00", "19:00"])
 * @property daysOfWeek List of day names the reminder is active (e.g. ["Sun", "Tue", "Thu", "Sat"])
 * @property startDate Epoch millis of when the reminder was created / started
 * @property isActive Whether the reminder is currently active
 * @property createdAt Epoch millis of creation
 */
data class Reminder(
    val id: String = "",
    val patientId: String = "",
    val medicineName: String = "",
    val description: String = "",
    val timeDurationDays: Int = 0,
    val medicineTimes: List<String> = emptyList(),
    val daysOfWeek: List<String> = emptyList(),
    val startDate: Long = System.currentTimeMillis(),
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
)
