package com.mediscan.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.mediscan.app.core.constants.ApiEndpoints
import com.mediscan.app.core.utils.NetworkResult
import com.mediscan.app.data.model.Reminder
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ReminderRepository — Firestore CRUD for medicine reminders.
 */
@Singleton
class ReminderRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) {
    private val collection get() = firestore.collection(ApiEndpoints.REMINDERS_COLLECTION)

    /** Save a new reminder */
    suspend fun saveReminder(reminder: Reminder): NetworkResult<Reminder> {
        return try {
            val docRef = collection.document()
            val toSave = reminder.copy(id = docRef.id)
            docRef.set(toSave).await()
            NetworkResult.Success(toSave)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Failed to save reminder")
        }
    }

    /** Update an existing reminder */
    suspend fun updateReminder(reminder: Reminder): NetworkResult<Reminder> {
        return try {
            collection.document(reminder.id).set(reminder).await()
            NetworkResult.Success(reminder)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Failed to update reminder")
        }
    }

    /** Get all reminders for a patient */
    suspend fun getReminders(patientId: String): NetworkResult<List<Reminder>> {
        return try {
            val snapshot = collection
                .whereEqualTo("patientId", patientId)
                .get()
                .await()
            val reminders = snapshot.toObjects(Reminder::class.java)
                .sortedByDescending { it.createdAt }
            NetworkResult.Success(reminders)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Failed to fetch reminders")
        }
    }

    /** Get only active reminders for a patient (for doctor view).
     *  Uses client-side filter to avoid needing a Firestore composite index. */
    suspend fun getActiveReminders(patientId: String): NetworkResult<List<Reminder>> {
        return try {
            val snapshot = collection
                .whereEqualTo("patientId", patientId)
                .get()
                .await()
            val reminders = snapshot.toObjects(Reminder::class.java)
                .filter { it.isActive }
                .sortedByDescending { it.createdAt }
            NetworkResult.Success(reminders)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Failed to fetch active reminders")
        }
    }

    /** Delete a reminder */
    suspend fun deleteReminder(reminderId: String): NetworkResult<Unit> {
        return try {
            collection.document(reminderId).delete().await()
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Failed to delete reminder")
        }
    }

    /** Update reminder active status */
    suspend fun setReminderActive(reminderId: String, active: Boolean): NetworkResult<Unit> {
        return try {
            collection.document(reminderId).update("isActive", active).await()
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Failed to update reminder")
        }
    }
}
