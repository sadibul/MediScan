package com.mediscan.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mediscan.app.core.utils.NetworkResult
import com.mediscan.app.data.model.Prescription
import com.mediscan.app.data.repository.PrescriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * DocsViewModel — manages prescription history list:
 * - Load all prescriptions for current patient
 * - Delete a prescription with confirmation
 */
@HiltViewModel
class DocsViewModel @Inject constructor(
    private val prescriptionRepository: PrescriptionRepository,
    private val auth: FirebaseAuth,
) : ViewModel() {

    private val _prescriptions =
        MutableStateFlow<NetworkResult<List<Prescription>>>(NetworkResult.Idle)
    val prescriptions: StateFlow<NetworkResult<List<Prescription>>> = _prescriptions

    private val _deleteState = MutableStateFlow<NetworkResult<Unit>>(NetworkResult.Idle)
    val deleteState: StateFlow<NetworkResult<Unit>> = _deleteState

    /** Load all prescriptions for the signed-in patient */
    fun loadPrescriptions() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _prescriptions.value = NetworkResult.Loading
            _prescriptions.value = prescriptionRepository.getPrescriptions(uid)
        }
    }

    /** Delete a prescription (Firestore doc + Storage image) */
    fun deletePrescription(prescription: Prescription) {
        viewModelScope.launch {
            _deleteState.value = NetworkResult.Loading
            _deleteState.value = prescriptionRepository.deletePrescription(prescription)
        }
    }

    /** Reset delete state after handling */
    fun resetDeleteState() {
        _deleteState.value = NetworkResult.Idle
    }
}
