package com.mediscan.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mediscan.app.core.constants.ApiEndpoints
import com.mediscan.app.core.utils.NetworkResult
import com.mediscan.app.data.model.Appointment
import com.mediscan.app.data.model.DoctorOrder
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * AppointmentRepository — Firestore CRUD for appointments.
 * Used by both Patient and Doctor ViewModels.
 */
@Singleton
class AppointmentRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) {
    private val collection get() = firestore.collection(ApiEndpoints.APPOINTMENTS_COLLECTION)

    /** Get all appointments for a doctor, sorted by dateTime descending */
    suspend fun getDoctorAppointments(doctorId: String): NetworkResult<List<Appointment>> {
        return try {
            val snapshot = collection
                .whereEqualTo("doctorId", doctorId)
                .get()
                .await()
            val appointments = snapshot.toObjects(Appointment::class.java)
                .sortedByDescending { it.dateTime }
            NetworkResult.Success(appointments)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Failed to fetch appointments")
        }
    }

    /** Get all appointments for a patient, sorted by dateTime descending */
    suspend fun getPatientAppointments(patientId: String): NetworkResult<List<Appointment>> {
        return try {
            val snapshot = collection
                .whereEqualTo("patientId", patientId)
                .get()
                .await()
            val appointments = snapshot.toObjects(Appointment::class.java)
                .sortedByDescending { it.dateTime }
            NetworkResult.Success(appointments)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Failed to fetch appointments")
        }
    }

    /** Update appointment status (scheduled → confirmed → completed / cancelled) */
    suspend fun updateAppointmentStatus(appointmentId: String, newStatus: String): NetworkResult<Unit> {
        return try {
            collection.document(appointmentId)
                .update("status", newStatus)
                .await()
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Failed to update appointment status")
        }
    }

    /** Create / save a new appointment */
    suspend fun bookAppointment(appointment: Appointment): NetworkResult<Unit> {
        return try {
            val docRef = if (appointment.id.isBlank()) {
                collection.document()
            } else {
                collection.document(appointment.id)
            }
            val toSave = if (appointment.id.isBlank()) {
                appointment.copy(id = docRef.id)
            } else {
                appointment
            }
            docRef.set(toSave).await()
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Failed to book appointment")
        }
    }

    /** Cancel an appointment */
    suspend fun cancelAppointment(appointmentId: String): NetworkResult<Unit> {
        return updateAppointmentStatus(appointmentId, "cancelled")
    }

    /** Complete appointment with doctor orders (prescriptions written by the doctor) */
    suspend fun completeWithOrders(
        appointmentId: String,
        orders: List<DoctorOrder>,
    ): NetworkResult<Unit> {
        return try {
            collection.document(appointmentId)
                .update(
                    mapOf(
                        "status" to "completed",
                        "doctorOrders" to orders.map { order ->
                            mapOf(
                                "medicine" to order.medicine,
                                "test" to order.test,
                                "doseStrength" to order.doseStrength,
                                "doseSchedule" to order.doseSchedule,
                                "notes" to order.notes,
                            )
                        }
                    )
                )
                .await()
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Failed to complete appointment")
        }
    }

    /** Get completed appointments for a patient (with doctor orders) */
    suspend fun getCompletedAppointmentsForPatient(patientId: String): NetworkResult<List<Appointment>> {
        return try {
            val snapshot = collection
                .whereEqualTo("patientId", patientId)
                .whereEqualTo("status", "completed")
                .get()
                .await()
            val appointments = snapshot.toObjects(Appointment::class.java)
                .sortedByDescending { it.dateTime }
            NetworkResult.Success(appointments)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Failed to fetch completed appointments")
        }
    }
}
