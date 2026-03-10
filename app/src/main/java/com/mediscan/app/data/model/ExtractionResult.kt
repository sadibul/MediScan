package com.mediscan.app.data.model

import com.google.gson.annotations.SerializedName

/**
 * Maps directly to the FastAPI /extract-base64 JSON response.
 */
data class ExtractionResult(
    @SerializedName("prescription_id") val prescriptionId: String = "",
    @SerializedName("extraction_timestamp") val extractionTimestamp: String = "",
    @SerializedName("model_version") val modelVersion: String = "",
    @SerializedName("ocr_engine") val ocrEngine: String = "",
    val status: String = "",
    @SerializedName("task_id") val taskId: String = "",
    val medications: List<ExtractedMedication> = emptyList(),
    @SerializedName("medication_count") val medicationCount: Int = 0,
    val doctor: DoctorInfo? = null,
    @SerializedName("prescription_info") val prescriptionInfo: PrescriptionInfo? = null,
    @SerializedName("quality_check") val qualityCheck: QualityCheck? = null,
    val message: String? = null,
)

data class ExtractedMedication(
    val medicine: String? = null,
    @SerializedName("dose_strength") val doseStrength: String? = null,
    val schedule: String? = null,
    val duration: String? = null,
    val confidence: MedicationConfidence? = null,
)

data class MedicationConfidence(
    val medicine: Double? = null,
    @SerializedName("dose_strength") val doseStrength: Double? = null,
    val schedule: Double? = null,
    val duration: Double? = null,
)

data class DoctorInfo(
    val name: String? = null,
    val hospital: String? = null,
)

data class PrescriptionInfo(
    val date: String? = null,
    val diagnoses: List<String>? = null,
    val tests: List<String>? = null,
)

data class QualityCheck(
    @SerializedName("is_acceptable") val isAcceptable: Boolean = false,
    @SerializedName("quality_label") val qualityLabel: String = "",
    @SerializedName("quality_score") val qualityScore: Double = 0.0,
    val issues: List<String> = emptyList(),
    val recommendation: String? = null,
)

data class QualityCheckRequest(
    val image: String,
)
