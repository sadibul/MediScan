package com.mediscan.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.mediscan.app.core.constants.ApiEndpoints
import com.mediscan.app.core.utils.NetworkResult
import com.mediscan.app.data.model.Notification
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * NotificationRepository — Firestore CRUD for in-app notifications.
 */
@Singleton
class NotificationRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) {
    private val collection get() = firestore.collection(ApiEndpoints.NOTIFICATIONS_COLLECTION)

    /** Send a notification (create in Firestore) */
    suspend fun sendNotification(notification: Notification): NetworkResult<Unit> {
        return try {
            val docRef = collection.document()
            val toSave = notification.copy(id = docRef.id)
            docRef.set(toSave).await()
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Failed to send notification")
        }
    }

    /** Get all notifications for a user, sorted by createdAt descending */
    suspend fun getNotifications(userId: String): NetworkResult<List<Notification>> {
        return try {
            val snapshot = collection
                .whereEqualTo("recipientId", userId)
                .get()
                .await()
            val notifications = snapshot.toObjects(Notification::class.java)
                .sortedByDescending { it.createdAt }
            NetworkResult.Success(notifications)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Failed to fetch notifications")
        }
    }

    /** Get unread notification count for a user */
    suspend fun getUnreadCount(userId: String): Int {
        return try {
            val snapshot = collection
                .whereEqualTo("recipientId", userId)
                .get()
                .await()
            snapshot.documents.count { doc -> doc.getBoolean("isRead") == false }
        } catch (_: Exception) {
            0
        }
    }

    /** Listen to unread count in real-time */
    fun observeUnreadCount(userId: String): Flow<Int> = callbackFlow {
        val listener = collection
            .whereEqualTo("recipientId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(0)
                    return@addSnapshotListener
                }
                val unread = snapshot?.documents?.count { doc ->
                    doc.getBoolean("isRead") == false
                } ?: 0
                trySend(unread)
            }
        awaitClose { listener.remove() }
    }

    /** Mark a single notification as read */
    suspend fun markAsRead(notificationId: String): NetworkResult<Unit> {
        return try {
            collection.document(notificationId)
                .update("isRead", true)
                .await()
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Failed to mark notification as read")
        }
    }

    /** Mark all notifications as read for a user */
    suspend fun markAllAsRead(userId: String): NetworkResult<Unit> {
        return try {
            val snapshot = collection
                .whereEqualTo("recipientId", userId)
                .get()
                .await()
            val unreadDocs = snapshot.documents.filter { doc ->
                doc.getBoolean("isRead") == false
            }
            if (unreadDocs.isNotEmpty()) {
                val batch = firestore.batch()
                unreadDocs.forEach { doc ->
                    batch.update(doc.reference, "isRead", true)
                }
                batch.commit().await()
            }
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Failed to mark all as read")
        }
    }

    /** Delete all notifications for a user */
    suspend fun clearAllNotifications(userId: String): NetworkResult<Unit> {
        return try {
            val snapshot = collection
                .whereEqualTo("recipientId", userId)
                .get()
                .await()
            if (snapshot.documents.isNotEmpty()) {
                val batch = firestore.batch()
                snapshot.documents.forEach { doc ->
                    batch.delete(doc.reference)
                }
                batch.commit().await()
            }
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Failed to clear notifications")
        }
    }
}
