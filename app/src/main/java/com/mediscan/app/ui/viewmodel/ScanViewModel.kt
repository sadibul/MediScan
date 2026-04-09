package com.mediscan.app.ui.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mediscan.app.core.utils.NetworkResult
import com.mediscan.app.data.model.ExtractionResult
import com.mediscan.app.data.model.Medication
import com.mediscan.app.data.model.Prescription
import com.mediscan.app.data.model.QualityCheck
import com.mediscan.app.data.repository.PrescriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import javax.inject.Inject

private const val TAG = "ScanViewModel"

/**
 * ScanViewModel — manages the full scan-to-save pipeline:
 * 1. Capture image (CameraX)
 * 2. Quality check (FastAPI)
 * 3. Extract prescription (FastAPI)
 * 4. User edits results
 * 5. Upload image → Firebase Storage
 * 6. Save prescription → Firestore
 */
@HiltViewModel
class ScanViewModel @Inject constructor(
    private val prescriptionRepository: PrescriptionRepository,
    private val auth: FirebaseAuth,
) : ViewModel() {

    // Current scan state
    private val _scanState = MutableStateFlow<ScanState>(ScanState.Initial)
    val scanState: StateFlow<ScanState> = _scanState

    // Monotonically increasing counter — gives each scan a unique stable key
    private val _scanCounter = MutableStateFlow(0)
    val scanCounter: StateFlow<Int> = _scanCounter

    // Captured image bytes (kept in memory for upload)
    private var capturedImageBytes: ByteArray? = null

    // Extraction result (for the bottom sheet)
    private val _extractionResult = MutableStateFlow<ExtractionResult?>(null)
    val extractionResult: StateFlow<ExtractionResult?> = _extractionResult

    // Save state
    private val _saveState = MutableStateFlow<NetworkResult<String>>(NetworkResult.Idle)
    val saveState: StateFlow<NetworkResult<String>> = _saveState

    /**
     * Process a captured image through the AI pipeline:
     * Sends photo directly to AI extraction — no quality checks.
     * Whatever the AI returns is shown for the user to edit/save.
     */
    fun processImage(imageBytes: ByteArray) {
        capturedImageBytes = imageBytes
        Log.d(TAG, "processImage: received ${imageBytes.size} bytes")
        viewModelScope.launch {
            _scanState.value = ScanState.Extracting

            when (val extractResult = prescriptionRepository.extractPrescription(imageBytes)) {
                is NetworkResult.Success -> {
                    val result = extractResult.data
                    Log.d(TAG, "═══ EXTRACTION RESULT DUMP ═══")
                    Log.d(TAG, "prescriptionId=${result.prescriptionId}")
                    Log.d(TAG, "status=${result.status}")
                    Log.d(TAG, "message=${result.message}")
                    Log.d(TAG, "medications count=${result.medications.size}")
                    result.medications.forEachIndexed { i, med ->
                        Log.d(TAG, "  med[$i]: name='${med.medicine}', dose='${med.doseStrength}', schedule='${med.schedule}', duration='${med.duration}'")
                    }
                    Log.d(TAG, "doctor: name='${result.doctor?.name}', hospital='${result.doctor?.hospital}'")
                    Log.d(TAG, "prescriptionInfo: date='${result.prescriptionInfo?.date}', diagnoses=${result.prescriptionInfo?.diagnoses}, tests=${result.prescriptionInfo?.tests}")
                    Log.d(TAG, "qualityCheck: acceptable=${result.qualityCheck?.isAcceptable}, label=${result.qualityCheck?.qualityLabel}, score=${result.qualityCheck?.qualityScore}")
                    Log.d(TAG, "═══ END DUMP ═══")
                    _extractionResult.value = result
                    _scanCounter.value++
                    // Always show the result sheet — let user edit whatever the AI extracted.
                    // Even if fields are empty, the user can fill them in manually.
                    _scanState.value = ScanState.ResultReady(result)
                }
                is NetworkResult.Error -> {
                    Log.e(TAG, "Extraction error: ${extractResult.message}")
                    _scanState.value = ScanState.Error(extractResult.message)
                }
                else -> {}
            }
        }
    }

    /**
     * Save the edited prescription to Firebase (Storage + Firestore).
     * Called from ExtractionResultSheet after user edits and taps "Save".
     */
    fun savePrescription(
        doctorName: String,
        hospital: String,
        visitDate: Long,
        diagnosis: String,
        diagnoses: List<String>,
        tests: List<String>,
        medications: List<Medication>,
    ) {
        val uid = auth.currentUser?.uid ?: return
        val imageBytes = capturedImageBytes ?: return
        val extractionJson = _extractionResult.value?.let {
            prescriptionRepository.extractionToJson(it)
        }

        viewModelScope.launch {
            _saveState.value = NetworkResult.Loading

            // Step 1: Upload image to Firebase Storage
            val prescriptionId = "rx_${System.currentTimeMillis()}"
            Log.d(TAG, "savePrescription: uploading image (${imageBytes.size} bytes) for $prescriptionId")
            val imageUrlResult = prescriptionRepository.uploadPrescriptionImage(
                patientId = uid,
                prescriptionId = prescriptionId,
                imageBytes = imageBytes,
            )

            val imageUrl = when (imageUrlResult) {
                is NetworkResult.Success -> {
                    Log.d(TAG, "savePrescription: image uploaded OK → ${imageUrlResult.data}")
                    imageUrlResult.data
                }
                is NetworkResult.Error -> {
                    Log.e(TAG, "savePrescription: image upload FAILED → ${imageUrlResult.message}")
                    null
                }
                else -> null
            }

            // Step 2: Create Prescription object
            val prescription = Prescription(
                id = prescriptionId,
                patientId = uid,
                doctorName = doctorName.ifBlank { null },
                hospital = hospital.ifBlank { null },
                visitDate = visitDate,
                diagnosis = diagnosis.ifBlank { null },
                diagnoses = diagnoses.filter { it.isNotBlank() },
                tests = tests.filter { it.isNotBlank() },
                medications = medications,
                imageUrl = imageUrl,
                rawExtractionJson = extractionJson,
                createdAt = System.currentTimeMillis(),
            )

            // Step 3: Save to Firestore
            Log.d(TAG, "savePrescription: saving to Firestore, imageUrl=${prescription.imageUrl}")
            _saveState.value = prescriptionRepository.savePrescription(prescription)
        }
    }

    /** Reset to initial state (after save or retake) */
    fun resetScan() {
        _scanState.value = ScanState.Initial
        _extractionResult.value = null
        _saveState.value = NetworkResult.Idle
        capturedImageBytes = null
    }

    /** Go back to camera from error/rejected state */
    fun retryCapture() {
        _scanState.value = ScanState.Camera
        capturedImageBytes = null
    }

    /**
     * Process a gallery image: shows loading state immediately, then decodes,
     * applies EXIF rotation, re-encodes as JPEG — all on IO thread — then
     * sends to the AI pipeline. Much faster UX than blocking the main thread.
     */
    fun processGalleryImage(uri: Uri, context: Context) {
        // Show loading UI immediately — no waiting for decode
        _scanState.value = ScanState.Extracting

        viewModelScope.launch {
            try {
                // All heavy work on IO thread
                val jpegBytes = withContext(Dispatchers.IO) {
                    // Step 1: Read EXIF rotation
                    val rotationDegrees = try {
                        context.contentResolver.openInputStream(uri)?.use { stream ->
                            val exif = ExifInterface(stream)
                            when (exif.getAttributeInt(
                                ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_NORMAL
                            )) {
                                ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                                ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                                ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                                else -> 0f
                            }
                        } ?: 0f
                    } catch (_: Exception) { 0f }

                    // Step 2: Decode bitmap at full resolution
                    val bitmap = context.contentResolver.openInputStream(uri)?.use { stream ->
                        BitmapFactory.decodeStream(stream)
                    }

                    if (bitmap == null) {
                        Log.e(TAG, "Gallery: failed to decode bitmap")
                        return@withContext null
                    }

                    Log.d(TAG, "Gallery: decoded ${bitmap.width}x${bitmap.height}, rotation=$rotationDegrees")

                    // Step 3: Apply EXIF rotation if needed
                    val finalBitmap = if (rotationDegrees != 0f) {
                        val matrix = Matrix().apply { postRotate(rotationDegrees) }
                        val rotated = Bitmap.createBitmap(
                            bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
                        )
                        bitmap.recycle()
                        rotated
                    } else {
                        bitmap
                    }

                    // Step 4: Compress to JPEG at 95% (matches camera quality)
                    val out = ByteArrayOutputStream()
                    finalBitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
                    val bytes = out.toByteArray()
                    out.close()
                    finalBitmap.recycle()

                    Log.d(TAG, "Gallery: JPEG ${bytes.size} bytes")
                    bytes
                }

                if (jpegBytes != null && jpegBytes.isNotEmpty()) {
                    // Feed into the same pipeline as camera
                    capturedImageBytes = jpegBytes
                    when (val extractResult = prescriptionRepository.extractPrescription(jpegBytes)) {
                        is NetworkResult.Success -> {
                            val result = extractResult.data
                            Log.d(TAG, "Gallery extraction: ${result.medications.size} meds found")
                            _extractionResult.value = result
                            _scanCounter.value++
                            _scanState.value = ScanState.ResultReady(result)
                        }
                        is NetworkResult.Error -> {
                            Log.e(TAG, "Gallery extraction error: ${extractResult.message}")
                            _scanState.value = ScanState.Error(extractResult.message)
                        }
                        else -> {}
                    }
                } else {
                    _scanState.value = ScanState.Error("Could not read the selected image")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Gallery processing failed", e)
                _scanState.value = ScanState.Error("Failed to process gallery image: ${e.message}")
            }
        }
    }

    /** Switch to camera mode */
    fun openCamera() {
        _scanState.value = ScanState.Camera
    }
}

/** Sealed class representing the scan screen states */
sealed class ScanState {
    data object Initial : ScanState()
    data object Camera : ScanState()
    data object CheckingQuality : ScanState()
    data object Extracting : ScanState()
    data class ResultReady(val result: ExtractionResult) : ScanState()
    data class Rejected(val message: String, val quality: QualityCheck?) : ScanState()
    data class Error(val message: String) : ScanState()
}
