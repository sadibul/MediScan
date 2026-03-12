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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.mediscan.app.core.utils.PreferencesManager
import com.mediscan.app.ui.components.common.MediButton
import com.mediscan.app.ui.components.common.MediTextField
import com.mediscan.app.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

/**
 * LoginScreen — Email/Password + Google Sign-In.
 * On success, fetches user profile and navigates to Patient or Doctor home.
 */
@Composable
fun LoginScreen(
    onNavigateToSignUp: () -> Unit,
    onNavigateToPatientHome: () -> Unit,
    onNavigateToDoctorHome: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val loginState by viewModel.loginState.collectAsState()
    val userProfileState by viewModel.userProfileState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Remember Me — load saved email on first composition (crash-safe)
    val preferencesManager = remember {
        try { PreferencesManager(context) } catch (_: Exception) { null }
    }
    var email by remember { mutableStateOf(preferencesManager?.savedEmail ?: "") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var rememberMe by remember { mutableStateOf(preferencesManager?.rememberMe ?: false) }

    // ── Google Sign-In launcher ──
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
                            // New Google user on Login screen → auto-create as patient
                            // using Google display name + email
                            val firebaseUser = viewModel.currentUser
                            if (firebaseUser != null) {
                                val user = com.mediscan.app.data.model.User(
                                    id = firebaseUser.uid,
                                    email = firebaseUser.email ?: "",
                                    fullName = firebaseUser.displayName ?: "User",
                                    profileImageUrl = firebaseUser.photoUrl?.toString(),
                                    userType = "patient",
                                )
                                viewModel.saveGoogleUserProfile(user)
                                onNavigateToPatientHome()
                            }
                        },
                        onExistingUser = { user ->
                            if (user.userType == "doctor") onNavigateToDoctorHome()
                            else onNavigateToPatientHome()
                        }
                    )
                }
            } catch (e: ApiException) {
                scope.launch {
                    snackbarHostState.showSnackbar("Google Sign-In failed: ${e.localizedMessage}")
                }
            }
        }
    }

    // Handle login state changes
    LaunchedEffect(loginState) {
        when (val state = loginState) {
            is NetworkResult.Success -> {
                // Save remember-me preference
                preferencesManager?.onLoginSuccess(email, rememberMe)
                viewModel.fetchUserProfile(state.data.uid)
            }
            is NetworkResult.Error -> {
                snackbarHostState.showSnackbar(state.message)
                viewModel.resetLoginState()
            }
            else -> {}
        }
    }

    // Navigate based on user role after profile fetch
    LaunchedEffect(userProfileState) {
        when (val state = userProfileState) {
            is NetworkResult.Success -> {
                if (state.data.userType == "doctor") onNavigateToDoctorHome()
                else onNavigateToPatientHome()
            }
            is NetworkResult.Error -> {
                snackbarHostState.showSnackbar("Could not load profile: ${state.message}")
            }
            else -> {}
        }
    }

    val isLoading = loginState is NetworkResult.Loading

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
                        .height(310.dp)
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
                            text = "Welcome Back",
                            color = Color.White,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Sign in to continue",
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
                        .offset(y = (-30).dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Email field
                        MediTextField(
                            value = email,
                            onValueChange = { email = it; emailError = null },
                            label = "Email",
                            leadingIcon = {
                                Icon(
                                    Icons.Filled.Email, contentDescription = null,
                                    tint = Color(0xFF3F51B5)
                                )
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            ),
                            isError = emailError != null,
                            errorMessage = emailError,
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isLoading
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Password field
                        MediTextField(
                            value = password,
                            onValueChange = { password = it; passwordError = null },
                            label = "Password",
                            leadingIcon = {
                                Icon(
                                    Icons.Filled.Lock, contentDescription = null,
                                    tint = Color(0xFF3F51B5)
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Filled.VisibilityOff
                                        else Icons.Filled.Visibility,
                                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                        tint = Color(0xFF3F51B5)
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None
                            else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            isError = passwordError != null,
                            errorMessage = passwordError,
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isLoading
                        )

                        // Forgot password + Remember me row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Remember Me
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = rememberMe,
                                    onCheckedChange = { rememberMe = it },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = Color(0xFF3F51B5)
                                    ),
                                    enabled = !isLoading
                                )
                                Text(
                                    text = "Remember me",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF555555),
                                    modifier = Modifier.clickable { rememberMe = !rememberMe }
                                )
                            }
                            // Forgot password
                            Text(
                                text = "Forgot Password?",
                                color = Color(0xFF3F51B5),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier
                                    .padding(vertical = 8.dp)
                                    .clickable {
                                        if (email.isNotBlank()) {
                                            viewModel.sendPasswordResetEmail(email)
                                            scope.launch {
                                                snackbarHostState.showSnackbar("Password reset email sent!")
                                            }
                                        } else {
                                            scope.launch {
                                                snackbarHostState.showSnackbar("Enter your email first")
                                            }
                                        }
                                    }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Login button
                        MediButton(
                            text = "Login",
                            isLoading = isLoading,
                            onClick = {
                                var valid = true
                                if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                                        .matches()
                                ) {
                                    emailError = "Enter a valid email address"
                                    valid = false
                                }
                                if (password.length < 6) {
                                    passwordError = "Password must be at least 6 characters"
                                    valid = false
                                }
                                if (valid) viewModel.signInWithEmail(email, password)
                            }
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // OR Divider
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

                        Spacer(modifier = Modifier.height(16.dp))

                        // Google Sign-In button
                        OutlinedButton(
                            onClick = {
                                val gso =
                                    GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                        .requestIdToken("756288246651-cnac4h8rl396sagurqitcb84048q97ub.apps.googleusercontent.com")
                                        .requestEmail()
                                        .requestProfile()
                                        .build()
                                val googleClient = GoogleSignIn.getClient(context, gso)
                                // Sign out first to always show account picker
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
                                text = "Continue with Google",
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF333333),
                                fontSize = 15.sp
                            )
                        }
                    }
                }

                // Sign up link
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding(bottom = 32.dp)
                        .offset(y = (-10).dp)
                ) {
                    Text(
                        text = "Don't have an account? ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF777777)
                    )
                    Text(
                        text = "Sign Up",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF1A237E),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onNavigateToSignUp() }
                    )
                }
            }
        }
    }
}
