package com.mediscan.app.ui.screens.splash

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mediscan.app.core.utils.NetworkResult
import com.mediscan.app.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

/**
 * SplashScreen — shown on app launch.
 * Displays logo + tagline for 2 seconds, then checks auth state:
 *   - If logged in → fetch user type → navigate to Patient or Doctor main
 *   - If not logged in → navigate to Login
 */
@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToPatientHome: () -> Unit,
    onNavigateToDoctorHome: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val userProfileState by viewModel.userProfileState.collectAsState()
    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 800),
        label = "splash_alpha"
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(2000L)

        if (viewModel.currentUser != null) {
            // User is logged in — fetch their profile to determine role
            viewModel.fetchUserProfile(viewModel.currentUser!!.uid)
        } else {
            onNavigateToLogin()
        }
    }

    // React to user profile fetch result
    LaunchedEffect(userProfileState) {
        when (val state = userProfileState) {
            is NetworkResult.Success -> {
                if (state.data.userType == "doctor") {
                    onNavigateToDoctorHome()
                } else {
                    onNavigateToPatientHome()
                }
            }
            is NetworkResult.Error -> {
                // If profile fetch fails, go to login
                onNavigateToLogin()
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.alpha(alphaAnim)
        ) {
            // App Name — two-tone branding
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Medi",
                    color = Color.White,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Scan",
                    color = Color(0xFF90CAF9),
                    fontSize = 42.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tagline
            Text(
                text = "Your Personal Health Companion",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                letterSpacing = 0.5.sp
            )
        }

        // Loading indicator at bottom
        CircularProgressIndicator(
            color = Color.White.copy(alpha = 0.7f),
            strokeWidth = 2.dp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 64.dp)
                .alpha(alphaAnim)
        )
    }
}
