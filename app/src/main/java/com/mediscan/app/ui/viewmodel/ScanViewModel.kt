package com.mediscan.app.ui.viewmodel

import android.util.Log
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
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
            val imageUrlResult = prescriptionRepository.uploadPrescriptionImage(
                patientId = uid,
                prescriptionId = prescriptionId,
                imageBytes = imageBytes,
            )

            val imageUrl = when (imageUrlResult) {
                is NetworkResult.Success -> imageUrlResult.data
                is NetworkResult.Error -> null // Save without image URL
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
