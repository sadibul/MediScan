package com.mediscan.app.ui.screens.patient.docs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediscan.app.core.utils.NetworkResult
import com.mediscan.app.data.model.Prescription
import com.mediscan.app.data.repository.PrescriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * PrescriptionDetailViewModel — loads, updates & deletes a single prescription.
 */
@HiltViewModel
class PrescriptionDetailViewModel @Inject constructor(
    private val prescriptionRepository: PrescriptionRepository,
) : ViewModel() {

    private val _prescription =
        MutableStateFlow<NetworkResult<Prescription>>(NetworkResult.Idle)
    val prescription: StateFlow<NetworkResult<Prescription>> = _prescription

    private val _deleteState = MutableStateFlow<NetworkResult<Unit>>(NetworkResult.Idle)
    val deleteState: StateFlow<NetworkResult<Unit>> = _deleteState

    private val _updateState = MutableStateFlow<NetworkResult<String>>(NetworkResult.Idle)
    val updateState: StateFlow<NetworkResult<String>> = _updateState

    /** Fetch a single prescription by ID */
    fun loadPrescription(prescriptionId: String) {
        viewModelScope.launch {
            _prescription.value = NetworkResult.Loading
            _prescription.value = prescriptionRepository.getPrescription(prescriptionId)
        }
    }

    /** Update the prescription (uses savePrescription which does set/upsert) */
    fun updatePrescription(prescription: Prescription) {
        viewModelScope.launch {
            _updateState.value = NetworkResult.Loading
            val result = prescriptionRepository.savePrescription(prescription)
            _updateState.value = result
            // Refresh the local state with the updated prescription
            if (result is NetworkResult.Success) {
                _prescription.value = NetworkResult.Success(prescription)
            }
        }
    }

    fun resetUpdateState() {
        _updateState.value = NetworkResult.Idle
    }

    /** Delete the prescription */
    fun deletePrescription(prescription: Prescription) {
        viewModelScope.launch {
            _deleteState.value = NetworkResult.Loading
            _deleteState.value = prescriptionRepository.deletePrescription(prescription)
        }
    }
}
