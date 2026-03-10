package com.mediscan.app.data.repository

import android.util.Base64
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.mediscan.app.core.constants.ApiEndpoints
import com.mediscan.app.core.utils.NetworkResult
import com.mediscan.app.data.model.ExtractionResult
import com.mediscan.app.data.model.Medication
import com.mediscan.app.data.model.Prescription
import com.mediscan.app.data.model.QualityCheck
import com.mediscan.app.data.remote.FastApiService
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "PrescriptionRepo"

/**
 * PrescriptionRepository — handles:
 * 1. FastAPI calls (quality check + extraction)
 * 2. Firebase Storage image upload
 * 3. Firestore prescription CRUD
 */
@Singleton
class PrescriptionRepository @Inject constructor(
    private val fastApiService: FastApiService,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val gson: Gson,
) {
    // ─── FastAPI Calls ───

    /** Quick quality check before extraction */
    suspend fun checkQuality(imageBytes: ByteArray): NetworkResult<QualityCheck> {
        return try {
            val base64 = Base64.encodeToString(imageBytes, Base64.NO_WRAP)
            val request = mapOf("image" to base64)
            val result = fastApiService.checkQuality(request)
            NetworkResult.Success(result)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Quality check failed")
        }
    }

    /** Main AI extraction — send image, get structured data. Skips quality check. */
    suspend fun extractPrescription(imageBytes: ByteArray): NetworkResult<ExtractionResult> {
        return try {
            val base64 = Base64.encodeToString(imageBytes, Base64.NO_WRAP)
            Log.d(TAG, "extractPrescription: sending ${base64.length} chars base64 (${imageBytes.size} bytes image)")
            val request = mapOf(
                "image" to base64,
                "skip_quality_check" to "true"
            )
            val result = fastApiService.extractPrescription(request)
            Log.d(TAG, "extractPrescription: received status=${result.status}, meds=${result.medications.size}, message=${result.message}")
            NetworkResult.Success(result)
        } catch (e: Exception) {
            Log.e(TAG, "extractPrescription failed", e)
            NetworkResult.Error(e.message ?: "Extraction failed. Is the AI server running?")
        }
    }

    // ─── Firebase Storage ───

    /** Upload prescription image to Firebase Storage */
    suspend fun uploadPrescriptionImage(
        patientId: String,
        prescriptionId: String,
        imageBytes: ByteArray,
    ): NetworkResult<String> {
        return try {
            val ref = storage.reference
                .child("${ApiEndpoints.PRESCRIPTION_IMAGES_PATH}/$patientId/$prescriptionId.jpg")
            ref.putBytes(imageBytes).await()
            val downloadUrl = ref.downloadUrl.await().toString()
            NetworkResult.Success(downloadUrl)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Image upload failed")
        }
    }

    // ─── Firestore CRUD ───

    /** Save prescription to Firestore */
    suspend fun savePrescription(prescription: Prescription): NetworkResult<String> {
        return try {
            val id = if (prescription.id.isBlank()) {
                UUID.randomUUID().toString()
            } else {
                prescription.id
            }
            val rxWithId = prescription.copy(id = id)
            firestore.collection(ApiEndpoints.PRESCRIPTIONS_COLLECTION)
                .document(id)
                .set(rxWithId)
                .await()
            NetworkResult.Success(id)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Failed to save prescription")
        }
    }

    /** Get all prescriptions for a patient, ordered by creation date */
    suspend fun getPrescriptions(patientId: String): NetworkResult<List<Prescription>> {
        return try {
            val snapshot = firestore.collection(ApiEndpoints.PRESCRIPTIONS_COLLECTION)
                .whereEqualTo("patientId", patientId)
                .get()
                .await()
            val prescriptions = snapshot.toObjects(Prescription::class.java)
                .sortedByDescending { it.createdAt }
            NetworkResult.Success(prescriptions)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Failed to fetch prescriptions")
        }
    }

    /** Get a single prescription by ID */
    suspend fun getPrescription(prescriptionId: String): NetworkResult<Prescription> {
        return try {
            val doc = firestore.collection(ApiEndpoints.PRESCRIPTIONS_COLLECTION)
                .document(prescriptionId)
                .get()
                .await()
            if (doc.exists()) {
                val rx = doc.toObject(Prescription::class.java)!!
                NetworkResult.Success(rx)
            } else {
                NetworkResult.Error("Prescription not found")
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Failed to fetch prescription")
        }
    }

    /** Delete a prescription and its image */
    suspend fun deletePrescription(prescription: Prescription): NetworkResult<Unit> {
        return try {
            // Delete from Firestore
            firestore.collection(ApiEndpoints.PRESCRIPTIONS_COLLECTION)
                .document(prescription.id)
                .delete()
                .await()

            // Delete image from Storage (if exists)
            if (!prescription.imageUrl.isNullOrBlank()) {
                try {
                    storage.getReferenceFromUrl(prescription.imageUrl).delete().await()
                } catch (_: Exception) {
                    // Image might not exist, ignore
                }
            }
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Failed to delete prescription")
        }
    }

    // ─── Helpers ───

    /** Convert ExtractionResult to a list of Medication models */
    fun extractionToMedications(result: ExtractionResult): List<Medication> {
        return result.medications.map { extracted ->
            Medication(
                medicine = extracted.medicine ?: "",
                doseStrength = extracted.doseStrength,
                schedule = extracted.schedule,
                duration = extracted.duration,
                confidence = extracted.confidence?.medicine,
            )
        }
    }

    /** Serialize ExtractionResult to JSON string for storage */
    fun extractionToJson(result: ExtractionResult): String {
        return gson.toJson(result)
    }
}
