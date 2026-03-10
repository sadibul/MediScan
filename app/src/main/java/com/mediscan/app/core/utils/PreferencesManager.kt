package com.mediscan.app.core.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * PreferencesManager — secure local storage for Remember Me.
 * Uses EncryptedSharedPreferences so email isn't stored in plaintext.
 */
@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val prefs: SharedPreferences by lazy {
        val masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        EncryptedSharedPreferences.create(
            "mediscan_secure_prefs",
            masterKey,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    companion object {
        private const val KEY_REMEMBER_ME = "remember_me"
        private const val KEY_SAVED_EMAIL = "saved_email"
    }

    var rememberMe: Boolean
        get() = prefs.getBoolean(KEY_REMEMBER_ME, false)
        set(value) = prefs.edit().putBoolean(KEY_REMEMBER_ME, value).apply()

    var savedEmail: String
        get() = prefs.getString(KEY_SAVED_EMAIL, "") ?: ""
        set(value) = prefs.edit().putString(KEY_SAVED_EMAIL, value).apply()

    /** Call on successful login — saves or clears based on remember-me toggle */
    fun onLoginSuccess(email: String, remember: Boolean) {
        rememberMe = remember
        savedEmail = if (remember) email else ""
    }

    /** Call on logout — clear saved email */
    fun onLogout() {
        savedEmail = ""
        rememberMe = false
    }
}
