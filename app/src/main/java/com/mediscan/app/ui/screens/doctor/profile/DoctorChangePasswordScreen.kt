package com.mediscan.app.ui.screens.doctor.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.mediscan.app.core.theme.ErrorRed
import com.mediscan.app.core.theme.TextSecondary
import com.mediscan.app.core.utils.NetworkResult
import com.mediscan.app.ui.components.common.MediButton
import com.mediscan.app.ui.components.common.MediTextField
import com.mediscan.app.ui.viewmodel.DoctorViewModel

/**
 * DoctorChangePasswordScreen — same UX as patient change password
 * but uses DoctorViewModel.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorChangePasswordScreen(
    viewModel: DoctorViewModel,
    onNavigateBack: () -> Unit,
) {
    val changePasswordState by viewModel.changePasswordState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var currentPassword by rememberSaveable { mutableStateOf("") }
    var newPassword by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }

    val passwordsMatch = newPassword == confirmPassword
    val newPasswordValid = newPassword.length >= 6
    val canSubmit = currentPassword.isNotBlank() && newPassword.isNotBlank() &&
            confirmPassword.isNotBlank() && passwordsMatch && newPasswordValid

    LaunchedEffect(changePasswordState) {
        when (changePasswordState) {
            is NetworkResult.Success -> {
                snackbarHostState.showSnackbar("Password changed successfully!")
                viewModel.resetChangePasswordState()
                onNavigateBack()
            }
            is NetworkResult.Error -> {
                snackbarHostState.showSnackbar((changePasswordState as NetworkResult.Error).message)
                viewModel.resetChangePasswordState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Change Password", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Enter your current password and choose a new password.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(24.dp))

            MediTextField(
                value = currentPassword,
                onValueChange = { currentPassword = it },
                label = "Current Password",
                visualTransformation = PasswordVisualTransformation(),
                keyboardType = KeyboardType.Password,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            MediTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = "New Password",
                visualTransformation = PasswordVisualTransformation(),
                keyboardType = KeyboardType.Password,
                modifier = Modifier.fillMaxWidth()
            )

            if (newPassword.isNotBlank() && !newPasswordValid) {
                Text("Password must be at least 6 characters", style = MaterialTheme.typography.bodySmall, color = ErrorRed, modifier = Modifier.padding(start = 4.dp, top = 4.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            MediTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "Confirm New Password",
                visualTransformation = PasswordVisualTransformation(),
                keyboardType = KeyboardType.Password,
                modifier = Modifier.fillMaxWidth()
            )

            if (confirmPassword.isNotBlank() && !passwordsMatch) {
                Text("Passwords do not match", style = MaterialTheme.typography.bodySmall, color = ErrorRed, modifier = Modifier.padding(start = 4.dp, top = 4.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))

            MediButton(
                text = "Change Password",
                onClick = { viewModel.changePassword(currentPassword, newPassword) },
                isLoading = changePasswordState is NetworkResult.Loading,
                enabled = canSubmit,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
