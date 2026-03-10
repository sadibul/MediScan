package com.mediscan.app.data.model

/**
 * Notification stored in Firestore under notifications/{notificationId}.
 * Used for both patient and doctor notifications.
 */
data class Notification(
    val id: String = "",
    val recipientId: String = "",         // who receives this notification
    val senderId: String = "",            // who triggered it
    val senderName: String = "",          // display name of the sender
    val type: String = "",                // "appointment_booked", "appointment_accepted", "appointment_cancelled", "appointment_completed", "prescription_added"
    val title: String = "",
    val message: String = "",
    val appointmentId: String = "",       // related appointment (optional)
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
)
