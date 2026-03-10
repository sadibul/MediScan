package com.mediscan.app.core.constants

import android.os.Build

/**
 * API endpoint constants for the FastAPI backend server.
 *
 * The FastAPI server handles AI prescription extraction only.
 * All user data, auth, and storage go directly through Firebase.
 *
 * How it works:
 * - On Android Emulator: automatically uses 10.0.2.2 (maps to host PC's localhost)
 * - On a real phone: uses your Mac's IP on the same WiFi/hotspot network
 *
 * ⚠️ FOR UNIVERSITY DEMO:
 * 1. Turn on phone hotspot, connect Mac to it
 * 2. Find Mac's IP (run: ipconfig getifaddr en0)
 * 3. Change PHYSICAL_DEVICE_IP below to that IP
 * 4. Start server: cd Capstone && python -m uvicorn main:app --host 0.0.0.0 --port 8000
 */
object ApiEndpoints {

    // ┌─────────────────────────────────────────────────┐
    // │  ⬇️ CHANGE THIS to your Mac's WiFi/Hotspot IP  │
    // │  Run in terminal: ipconfig getifaddr en0        │
    // └─────────────────────────────────────────────────┘
    private const val PHYSICAL_DEVICE_IP = "10.215.144.203"

    private const val PORT = "8000"
    private const val EMULATOR_IP = "10.0.2.2"

    /**
     * Auto-detect: if running on emulator → use 10.0.2.2
     * If running on real phone → use the physical device IP above
     */
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
        get() = if (isEmulator) {
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
