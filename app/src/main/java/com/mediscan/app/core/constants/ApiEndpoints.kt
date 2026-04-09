package com.mediscan.app.core.constants

import android.os.Build


object ApiEndpoints {

    // ─── Cloud Server (Railway) ─────────────────────────────────
    // Always-on cloud URL — works from anywhere, any network
    private const val CLOUD_URL = "https://capstone-production-59e8.up.railway.app/"

    // ─── Local Server (for development/testing only) ────────────
    // Set USE_CLOUD = false to switch back to local Mac server
    private const val USE_CLOUD = true
    private const val PHYSICAL_DEVICE_IP = "10.136.147.203"
    private const val PORT = "8000"
    private const val EMULATOR_IP = "10.0.2.2"

    private val isEmulator: Boolean
        get() = (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.PRODUCT.contains("sdk")
                || Build.PRODUCT.contains("emulator"))

    val BASE_URL: String
        get() = if (USE_CLOUD) {
            CLOUD_URL
        } else if (isEmulator) {
            "http://$EMULATOR_IP:$PORT/"
        } else {
            "http://$PHYSICAL_DEVICE_IP:$PORT/"
        }

    // Endpoints
    const val HEALTH = "health"
    const val CHECK_QUALITY_BASE64 = "check-quality-base64"
    const val EXTRACT_BASE64 = "extract-base64"
    const val RESULTS = "results/{task_id}"
    const val DELETE_TASK = "task/{task_id}"

    // Firestore Collections
    const val USERS_COLLECTION = "users"
    const val PRESCRIPTIONS_COLLECTION = "prescriptions"
    const val APPOINTMENTS_COLLECTION = "appointments"
    const val NOTIFICATIONS_COLLECTION = "notifications"
    const val REMINDERS_COLLECTION = "reminders"

    // Firebase Storage Paths
    const val PRESCRIPTION_IMAGES_PATH = "prescription_images"
    const val PROFILE_IMAGES_PATH = "profile_images"
}
