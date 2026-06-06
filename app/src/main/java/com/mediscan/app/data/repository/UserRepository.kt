package com.mediscan.app.data.repository

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.mediscan.app.core.constants.ApiEndpoints
import com.mediscan.app.core.utils.NetworkResult
import com.mediscan.app.data.model.Appointment
import com.mediscan.app.data.model.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * UserRepository — handles Firestore operations for user profiles
 * and patient-related data (appointments, etc.).
 */
@Singleton
class UserRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
) {
    /** Fetch user profile from Firestore */
    suspend fun getUserProfile(uid: String): NetworkResult<User> {
        return try {
            val doc = firestore.collection(ApiEndpoints.USERS_COLLECTION)
                .document(uid)
                .get()
                .await()
            if (doc.exists()) {
                val user = doc.toObject(User::class.java)!!
                NetworkResult.Success(user)
            } else {
                NetworkResult.Error("User profile not found")
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Failed to fetch user profile")
        }
    }

    /** Update user profile in Firestore */
    suspend fun updateUserProfile(user: User): NetworkResult<Unit> {
        return try {
            firestore.collection(ApiEndpoints.USERS_COLLECTION)
                .document(user.id)
                .set(user)
                .await()
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Failed to update profile")
        }
    }

    /**
     * Fetch upcoming appointments for a patient.
     * Includes both "scheduled" and "confirmed" statuses.
     * Shows appointments from the start of today (not just future timestamps)
     * so that confirmed appointments for today are always visible.
     *
     * Uses two separate queries to avoid Firestore composite index issues
     * with whereEqualTo + whereIn on different fields.
     */
    suspend fun getUpcomingAppointments(patientId: String): NetworkResult<List<Appointment>> {
        return try {
            // Start of today (midnight) so today's appointments always show
            val calendar = java.util.Calendar.getInstance().apply {
                set(java.util.Calendar.HOUR_OF_DAY, 0)
                set(java.util.Calendar.MINUTE, 0)
                set(java.util.Calendar.SECOND, 0)
                set(java.util.Calendar.MILLISECOND, 0)
            }
            val startOfToday = calendar.timeInMillis

            // Query 1: scheduled appointments
            val scheduledSnapshot = firestore.collection(ApiEndpoints.APPOINTMENTS_COLLECTION)
                .whereEqualTo("patientId", patientId)
                .whereEqualTo("status", "scheduled")
                .get()
                .await()

            // Query 2: confirmed appointments
            val confirmedSnapshot = firestore.collection(ApiEndpoints.APPOINTMENTS_COLLECTION)
                .whereEqualTo("patientId", patientId)
                .whereEqualTo("status", "confirmed")
                .get()
                .await()

            val allAppointments = (
                scheduledSnapshot.toObjects(Appointment::class.java) +
                confirmedSnapshot.toObjects(Appointment::class.java)
            )
                .filter { it.dateTime >= startOfToday }
                .sortedBy { it.dateTime }
                .take(10)

            NetworkResult.Success(allAppointments)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Failed to fetch appointments")
        }
    }

    /** Cancel an appointment */
    suspend fun cancelAppointment(appointmentId: String): NetworkResult<Unit> {
        return try {
            firestore.collection(ApiEndpoints.APPOINTMENTS_COLLECTION)
                .document(appointmentId)
                .update("status", "cancelled")
                .await()
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Failed to cancel appointment")
        }
    }

    /** Fetch all doctors (for doctor search) */
    suspend fun getDoctors(): NetworkResult<List<User>> {
        return try {
            val snapshot = firestore.collection(ApiEndpoints.USERS_COLLECTION)
                .whereEqualTo("userType", "doctor")
                .get()
                .await()
            val doctors = snapshot.toObjects(User::class.java)
            NetworkResult.Success(doctors)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Failed to fetch doctors")
        }
    }

    /** Fetch a single doctor profile by ID */
    suspend fun getDoctorById(doctorId: String): NetworkResult<User> {
        return try {
            val doc = firestore.collection(ApiEndpoints.USERS_COLLECTION)
                .document(doctorId)
                .get()
                .await()
            if (doc.exists()) {
                val user = doc.toObject(User::class.java)!!
                NetworkResult.Success(user)
            } else {
                NetworkResult.Error("Doctor not found")
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Failed to fetch doctor profile")
        }
    }

    /**
     * Sync denormalized patient/doctor data in all active appointment documents.
     * Called after a profile update (name, image) so that the doctor's appointment list
     * always shows the latest patient info and vice-versa.
     */
    suspend fun syncAppointmentsWithProfile(user: User) {
        try {
            if (user.userType == "patient") {
                // Update all appointments where this user is the patient
                val snapshots = firestore.collection(ApiEndpoints.APPOINTMENTS_COLLECTION)
                    .whereEqualTo("patientId", user.id)
                    .get()
                    .await()
                val batch = firestore.batch()
                for (doc in snapshots.documents) {
                    batch.update(
                        doc.reference,
                        mapOf(
                            "patientName" to user.fullName,
                            "patientProfileImageUrl" to user.profileImageUrl,
                        )
                    )
                }
                batch.commit().await()
            } else if (user.userType == "doctor") {
                // Update all appointments where this user is the doctor
                val snapshots = firestore.collection(ApiEndpoints.APPOINTMENTS_COLLECTION)
                    .whereEqualTo("doctorId", user.id)
                    .get()
                    .await()
                val batch = firestore.batch()
                for (doc in snapshots.documents) {
                    batch.update(
                        doc.reference,
                        mapOf(
                            "doctorName" to user.fullName,
                            "doctorProfileImageUrl" to user.profileImageUrl,
                            "specialization" to (user.specialization ?: ""),
                        )
                    )
                }
                batch.commit().await()
            }
        } catch (_: Exception) {
            // Best-effort sync — don't block the profile update if this fails
        }
    }

    /** Upload profile image to Firebase Storage, return download URL */
    suspend fun uploadProfileImage(uid: String, imageUri: Uri): NetworkResult<String> {
        return try {
            val ref = storage.reference
                .child("profile_images/$uid.jpg")
            ref.putFile(imageUri).await()
            val downloadUrl = ref.downloadUrl.await().toString()
            NetworkResult.Success(downloadUrl)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Failed to upload profile image")
        }
    }
}
