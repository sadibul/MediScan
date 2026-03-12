package com.mediscan.app.core.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * PreferencesManager — secure local storage for Remember Me.
 * Uses EncryptedSharedPreferences with a fallback to regular prefs
 * if the encrypted file becomes corrupted (known Android issue).
 */
@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val prefs: SharedPreferences by lazy {
        try {
            val masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
            EncryptedSharedPreferences.create(
                "mediscan_secure_prefs",
                masterKey,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            Log.e("PreferencesManager", "EncryptedSharedPreferences corrupted, resetting", e)
            // Delete the corrupted file and try again
            try {
                context.getSharedPreferences("mediscan_secure_prefs", Context.MODE_PRIVATE)
                    .edit().clear().apply()
                val prefsFile = java.io.File(context.filesDir.parent, "shared_prefs/mediscan_secure_prefs.xml")
                if (prefsFile.exists()) prefsFile.delete()
                val masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
                EncryptedSharedPreferences.create(
                    "mediscan_secure_prefs",
                    masterKey,
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )
            } catch (e2: Exception) {
                Log.e("PreferencesManager", "Fallback to regular SharedPreferences", e2)
                // Ultimate fallback — use regular (unencrypted) prefs
                context.getSharedPreferences("mediscan_prefs_fallback", Context.MODE_PRIVATE)
            }
        }
    }

    companion object {
        private const val KEY_REMEMBER_ME = "remember_me"
        private const val KEY_SAVED_EMAIL = "saved_email"
    }

    var rememberMe: Boolean
        get() = try { prefs.getBoolean(KEY_REMEMBER_ME, false) } catch (_: Exception) { false }
        set(value) = try { prefs.edit().putBoolean(KEY_REMEMBER_ME, value).apply() } catch (_: Exception) {}

    var savedEmail: String
        get() = try { prefs.getString(KEY_SAVED_EMAIL, "") ?: "" } catch (_: Exception) { "" }
        set(value) = try { prefs.edit().putString(KEY_SAVED_EMAIL, value).apply() } catch (_: Exception) {}

    /** Call on successful login — saves or clears based on remember-me toggle */
    fun onLoginSuccess(email: String, remember: Boolean) {
        try {
            rememberMe = remember
            savedEmail = if (remember) email else ""
        } catch (_: Exception) {}
    }

    /** Call on logout — clear saved email */
    fun onLogout() {
        try {
            savedEmail = ""
            rememberMe = false
        } catch (_: Exception) {}
    }
}
