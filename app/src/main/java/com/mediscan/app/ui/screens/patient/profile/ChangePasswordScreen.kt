package com.mediscan.app.ui.screens.patient.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mediscan.app.core.theme.ErrorRed
import com.mediscan.app.core.theme.TextSecondary
import com.mediscan.app.core.theme.WarningOrange
import com.mediscan.app.core.utils.NetworkResult
import com.mediscan.app.ui.components.common.MediButton
import com.mediscan.app.ui.components.common.MediTextField
import com.mediscan.app.ui.viewmodel.PatientViewModel

private val HeaderGradient
    @Composable get() = Brush.horizontalGradient(
        listOf(Color(0xFF1A237E), Color(0xFF3F51B5), Color(0xFF5C6BC0))
    )

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    viewModel: PatientViewModel,
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
            TopAppBar(
                title = {
                    Text("Change Password", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 20.sp)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                modifier = Modifier.background(HeaderGradient),
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFFF4F6FB),
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // ── Header icon + description ──
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color(0xFF1A237E).copy(alpha = 0.08f), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Default.Lock,
                            null,
                            tint = Color(0xFF1A237E),
                            modifier = Modifier.size(28.dp),
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Update Your Password",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A237E),
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Enter your current password and choose a new password.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Password Fields Card ──
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
            ) {
                Column {
                    // Top accent bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .background(Color(0xFF3F51B5), RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                    )
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            "Security",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A237E),
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        MediTextField(
                            value = currentPassword,
                            onValueChange = { currentPassword = it },
                            label = "Current Password",
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardType = KeyboardType.Password,
                            modifier = Modifier.fillMaxWidth(),
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        MediTextField(
                            value = newPassword,
                            onValueChange = { newPassword = it },
                            label = "New Password",
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardType = KeyboardType.Password,
                            modifier = Modifier.fillMaxWidth(),
                        )

                        if (newPassword.isNotBlank() && !newPasswordValid) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Password must be at least 6 characters",
                                style = MaterialTheme.typography.bodySmall,
                                color = WarningOrange,
                                modifier = Modifier.padding(start = 4.dp),
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        MediTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = "Confirm New Password",
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardType = KeyboardType.Password,
                            modifier = Modifier.fillMaxWidth(),
                        )

                        if (confirmPassword.isNotBlank() && !passwordsMatch) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Passwords do not match",
                                style = MaterialTheme.typography.bodySmall,
                                color = ErrorRed,
                                modifier = Modifier.padding(start = 4.dp),
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Gradient Submit Button ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .background(
                        if (canSubmit) Brush.horizontalGradient(listOf(Color(0xFF1A237E), Color(0xFF3F51B5)))
                        else Brush.horizontalGradient(listOf(Color(0xFF1A237E).copy(alpha = 0.3f), Color(0xFF3F51B5).copy(alpha = 0.3f))),
                        RoundedCornerShape(16.dp),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                MediButton(
                    text = "Change Password",
                    onClick = { viewModel.changePassword(currentPassword, newPassword) },
                    isLoading = changePasswordState is NetworkResult.Loading,
                    enabled = canSubmit,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
