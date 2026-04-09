package com.mediscan.app.data.model

import com.google.firebase.firestore.PropertyName

/**
 * Notification stored in Firestore under notifications/{notificationId}.
 * Used for both patient and doctor notifications.
 *
 * Note: @PropertyName ensures Firestore always uses "isRead" as the field name,
 * preventing Kotlin/JavaBean convention from stripping the "is" prefix to "read".
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
    @field:JvmField
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
)
