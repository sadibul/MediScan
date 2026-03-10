package com.mediscan.app.core.utils

/**
 * Sealed class representing the state of a network request.
 * Used across the app to handle loading, success, error, and idle states
 * in ViewModels and UI composables.
 */
sealed class NetworkResult<out T> {
    /** Initial state — no request has been made yet */
    data object Idle : NetworkResult<Nothing>()

    /** Request is in progress */
    data object Loading : NetworkResult<Nothing>()

    /** Request succeeded with data */
    data class Success<T>(val data: T) : NetworkResult<T>()

    /** Request failed with an error message */
    data class Error(val message: String) : NetworkResult<Nothing>()
}
