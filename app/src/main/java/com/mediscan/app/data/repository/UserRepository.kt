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

    /** Fetch upcoming appointments for a patient (status = "scheduled", sorted by dateTime ascending) */
    suspend fun getUpcomingAppointments(patientId: String): NetworkResult<List<Appointment>> {
        return try {
            val now = System.currentTimeMillis()
            // Fetch scheduled + confirmed appointments (not just scheduled)
            val snapshot = firestore.collection(ApiEndpoints.APPOINTMENTS_COLLECTION)
                .whereEqualTo("patientId", patientId)
                .whereIn("status", listOf("scheduled", "confirmed"))
                .get()
                .await()
            val appointments = snapshot.toObjects(Appointment::class.java)
                .filter { it.dateTime >= now }
                .sortedBy { it.dateTime }
                .take(10)
            NetworkResult.Success(appointments)
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
