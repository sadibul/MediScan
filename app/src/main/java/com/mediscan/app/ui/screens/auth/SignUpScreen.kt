package com.mediscan.app.ui.screens.auth

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.mediscan.app.R
import com.mediscan.app.core.theme.MediBlue
import com.mediscan.app.core.utils.NetworkResult
import com.mediscan.app.data.model.User
import com.mediscan.app.ui.components.common.MediButton
import com.mediscan.app.ui.components.common.MediTextField
import com.mediscan.app.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

/**
 * SignUpScreen — Registration with Patient/Doctor role toggle.
 * Doctor role shows extra fields: license number, specialization, hospital.
 */
@Composable
fun SignUpScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToPatientHome: () -> Unit,
    onNavigateToDoctorHome: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val signUpState by viewModel.signUpState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isDoctor by remember { mutableStateOf(false) }
    var licenseNumber by remember { mutableStateOf("") }
    var specialization by remember { mutableStateOf("") }
    var hospital by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // ── Google Sign-Up launcher ──
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                account.idToken?.let { idToken ->
                    viewModel.signInWithGoogle(
                        idToken = idToken,
                        onNewUser = {
                            // New Google user from Sign Up → create profile with role = isDoctor
                            val firebaseUser = viewModel.currentUser
                            if (firebaseUser != null) {
                                val user = User(
                                    id = firebaseUser.uid,
                                    email = firebaseUser.email ?: account.email ?: "",
                                    fullName = firebaseUser.displayName
                                        ?: account.displayName
                                        ?: "${account.givenName ?: ""} ${account.familyName ?: ""}".trim()
                                            .ifBlank { "User" },
                                    profileImageUrl = firebaseUser.photoUrl?.toString(),
                                    userType = if (isDoctor) "doctor" else "patient",
                                )
                                viewModel.saveGoogleUserProfile(user)
                                if (isDoctor) onNavigateToDoctorHome() else onNavigateToPatientHome()
                            }
                        },
                        onExistingUser = { user ->
                            // Already has an account — just go to the right home
                            if (user.userType == "doctor") onNavigateToDoctorHome()
                            else onNavigateToPatientHome()
                        }
                    )
                }
            } catch (e: ApiException) {
                scope.launch {
                    snackbarHostState.showSnackbar("Google Sign-Up failed: ${e.localizedMessage}")
                }
            }
        }
    }

    LaunchedEffect(signUpState) {
        when (val state = signUpState) {
            is NetworkResult.Success -> {
                if (isDoctor) onNavigateToDoctorHome() else onNavigateToPatientHome()
            }
            is NetworkResult.Error -> {
                snackbarHostState.showSnackbar(state.message)
                viewModel.resetSignUpState()
            }
            else -> {}
        }
    }

    val isLoading = signUpState is NetworkResult.Loading

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF4F6FB))
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // ── Gradient Header with Logo ──
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(270.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF1A237E),
                                    Color(0xFF3F51B5),
                                    Color(0xFF5C6BC0)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // App name branding
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Medi",
                                color = Color.White,
                                fontSize = 36.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Scan",
                                color = Color(0xFF90CAF9),
                                fontSize = 36.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Create Account",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Join MediScan today",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }

                // ── Form Card overlapping header ──
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .offset(y = (-28).dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Full Name
                        MediTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = "Full Name",
                            leadingIcon = {
                                Icon(
                                    Icons.Filled.Person, null,
                                    tint = Color(0xFF3F51B5)
                                )
                            },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isLoading
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // Email
                        MediTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = "Email",
                            leadingIcon = {
                                Icon(
                                    Icons.Filled.Email, null,
                                    tint = Color(0xFF3F51B5)
                                )
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isLoading
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // Phone
                        MediTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = "Phone Number",
                            leadingIcon = {
                                Icon(
                                    Icons.Filled.Phone, null,
                                    tint = Color(0xFF3F51B5)
                                )
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Phone,
                                imeAction = ImeAction.Next
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isLoading
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // Password
                        MediTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = "Password",
                            leadingIcon = {
                                Icon(
                                    Icons.Filled.Lock, null,
                                    tint = Color(0xFF3F51B5)
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                        null,
                                        tint = Color(0xFF3F51B5)
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Next
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isLoading
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // Confirm Password
                        MediTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = "Confirm Password",
                            leadingIcon = {
                                Icon(
                                    Icons.Filled.Lock, null,
                                    tint = Color(0xFF3F51B5)
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = {
                                    confirmPasswordVisible = !confirmPasswordVisible
                                }) {
                                    Icon(
                                        if (confirmPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                        null,
                                        tint = Color(0xFF3F51B5)
                                    )
                                }
                            },
                            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isLoading
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // ── Role Selection as styled toggle cards ──
                        Text(
                            text = "I am a:",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF333333),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Patient toggle card
                            Surface(
                                onClick = { isDoctor = false },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                color = if (!isDoctor) Color(0xFF3F51B5) else Color(0xFFF0F2F8),
                                border = if (!isDoctor) null else BorderStroke(
                                    1.dp,
                                    Color(0xFFDDDDDD)
                                ),
                                enabled = !isLoading
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "👤  Patient",
                                        color = if (!isDoctor) Color.White else Color(0xFF555555),
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                            // Doctor toggle card
                            Surface(
                                onClick = { isDoctor = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                color = if (isDoctor) Color(0xFF3F51B5) else Color(0xFFF0F2F8),
                                border = if (isDoctor) null else BorderStroke(
                                    1.dp,
                                    Color(0xFFDDDDDD)
                                ),
                                enabled = !isLoading
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "🩺  Doctor",
                                        color = if (isDoctor) Color.White else Color(0xFF555555),
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }

                        // Doctor-specific fields
                        if (isDoctor) {
                            Spacer(modifier = Modifier.height(14.dp))
                            MediTextField(
                                value = licenseNumber,
                                onValueChange = { licenseNumber = it },
                                label = "Medical License Number",
                                leadingIcon = {
                                    Icon(
                                        Icons.Filled.Badge, null,
                                        tint = Color(0xFF3F51B5)
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isLoading
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            MediTextField(
                                value = specialization,
                                onValueChange = { specialization = it },
                                label = "Specialization (e.g. Cardiologist)",
                                leadingIcon = {
                                    Icon(
                                        Icons.Filled.MedicalServices, null,
                                        tint = Color(0xFF3F51B5)
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isLoading
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            MediTextField(
                                value = hospital,
                                onValueChange = { hospital = it },
                                label = "Hospital / Clinic Name",
                                leadingIcon = {
                                    Icon(
                                        Icons.Filled.LocalHospital, null,
                                        tint = Color(0xFF3F51B5)
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isLoading
                            )
                        }

                        Spacer(modifier = Modifier.height(22.dp))

                        // Sign Up button
                        MediButton(
                            text = "Sign Up",
                            isLoading = isLoading,
                            onClick = {
                                val user = User(
                                    fullName = fullName.trim(),
                                    phone = phone.trim(),
                                    userType = if (isDoctor) "doctor" else "patient",
                                    licenseNumber = if (isDoctor) licenseNumber.trim() else null,
                                    specialization = if (isDoctor) specialization.trim() else null,
                                    hospital = if (isDoctor) hospital.trim() else null,
                                )
                                viewModel.signUpWithEmail(email.trim(), password, user)
                            }
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // OR divider
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            HorizontalDivider(
                                modifier = Modifier.weight(1f),
                                color = Color(0xFFDDDDDD)
                            )
                            Text(
                                text = "  OR  ",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF999999),
                                fontWeight = FontWeight.Medium
                            )
                            HorizontalDivider(
                                modifier = Modifier.weight(1f),
                                color = Color(0xFFDDDDDD)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Google Sign-Up button
                        OutlinedButton(
                            onClick = {
                                val gso =
                                    GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                        .requestIdToken("756288246651-cnac4h8rl396sagurqitcb84048q97ub.apps.googleusercontent.com")
                                        .requestEmail()
                                        .requestProfile()
                                        .build()
                                val googleClient = GoogleSignIn.getClient(context, gso)
                                googleClient.signOut().addOnCompleteListener {
                                    googleSignInLauncher.launch(googleClient.signInIntent)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFFDDDDDD)),
                            enabled = !isLoading
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_google),
                                contentDescription = "Google",
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Sign Up with Google",
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF333333),
                                fontSize = 15.sp
                            )
                        }
                    }
                }

                // Login link
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding(bottom = 32.dp)
                        .offset(y = (-10).dp)
                ) {
                    Text(
                        text = "Already have an account? ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF777777)
                    )
                    Text(
                        text = "Login",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF1A237E),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onNavigateToLogin() }
                    )
                }
            }
        }
    }
}
