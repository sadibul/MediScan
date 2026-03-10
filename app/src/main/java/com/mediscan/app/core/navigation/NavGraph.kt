package com.mediscan.app.core.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mediscan.app.ui.screens.auth.LoginScreen
import com.mediscan.app.ui.screens.auth.SignUpScreen
import com.mediscan.app.ui.screens.booking.DoctorDetailScreen
import com.mediscan.app.ui.screens.booking.DoctorSearchScreen
import com.mediscan.app.ui.screens.booking.PatientAppointmentsScreen
import com.mediscan.app.ui.screens.doctor.DoctorMainScreen
import com.mediscan.app.ui.screens.hospitals.NearbyHospitalsScreen
import com.mediscan.app.ui.screens.patient.PatientMainScreen
import com.mediscan.app.ui.screens.splash.SplashScreen
import com.mediscan.app.ui.viewmodel.BookingViewModel

// ── Shared transition specs ──────────────────────────────────
private val slideEnter  = slideInHorizontally(initialOffsetX = { it }) + fadeIn()
private val slideExit   = slideOutHorizontally(targetOffsetX = { -it / 3 }) + fadeOut()
private val slidePopEnter  = slideInHorizontally(initialOffsetX = { -it / 3 }) + fadeIn()
private val slidePopExit   = slideOutHorizontally(targetOffsetX = { it }) + fadeOut()

/**
 * Main Navigation Graph for MediScan.
 * Auth flow: Splash → Login/SignUp → PatientMain or DoctorMain
 * Phase 11: slide + fade transitions on all routes.
 */
@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH,
        enterTransition  = { slideEnter },
        exitTransition   = { slideExit },
        popEnterTransition  = { slidePopEnter },
        popExitTransition   = { slidePopExit },
    ) {
        // ── Splash ──
        composable(Routes.SPLASH) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToPatientHome = {
                    navController.navigate(Routes.PATIENT_MAIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToDoctorHome = {
                    navController.navigate(Routes.DOCTOR_MAIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        // ── Login ──
        composable(Routes.LOGIN) {
            LoginScreen(
                onNavigateToSignUp = {
                    navController.navigate(Routes.SIGN_UP)
                },
                onNavigateToPatientHome = {
                    navController.navigate(Routes.PATIENT_MAIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToDoctorHome = {
                    navController.navigate(Routes.DOCTOR_MAIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // ── Sign Up ──
        composable(Routes.SIGN_UP) {
            SignUpScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onNavigateToPatientHome = {
                    navController.navigate(Routes.PATIENT_MAIN) {
                        popUpTo(Routes.SIGN_UP) { inclusive = true }
                    }
                },
                onNavigateToDoctorHome = {
                    navController.navigate(Routes.DOCTOR_MAIN) {
                        popUpTo(Routes.SIGN_UP) { inclusive = true }
                    }
                }
            )
        }

        // ── Patient Main ──
        composable(Routes.PATIENT_MAIN) {
            PatientMainScreen(
                onNavigateToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.PATIENT_MAIN) { inclusive = true }
                    }
                },
                onNavigateToDoctorSearch = {
                    navController.navigate(Routes.DOCTOR_SEARCH)
                },
                onNavigateToNearbyHospitals = {
                    navController.navigate(Routes.NEARBY_HOSPITALS)
                },
                onNavigateToAppointments = {
                    navController.navigate(Routes.PATIENT_APPOINTMENTS)
                },
                onNavigateToDoctorDetail = { doctorId, appointmentDateTime, appointmentComplaint ->
                    if (appointmentDateTime != null && appointmentDateTime > 0L) {
                        navController.navigate(
                            Routes.doctorDetailFromAppointment(doctorId, appointmentDateTime, appointmentComplaint)
                        )
                    } else {
                        navController.navigate(Routes.doctorDetail(doctorId))
                    }
                },
            )
        }

        // ── Doctor Main ──
        composable(Routes.DOCTOR_MAIN) {
            DoctorMainScreen(
                onNavigateToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.DOCTOR_MAIN) { inclusive = true }
                    }
                }
            )
        }

        // ── Doctor Search (Booking Flow) ──
        composable(Routes.DOCTOR_SEARCH) {
            val bookingViewModel: BookingViewModel = hiltViewModel()
            DoctorSearchScreen(
                viewModel = bookingViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDoctorDetail = { doctorId ->
                    navController.navigate(Routes.doctorDetail(doctorId))
                },
            )
        }

        // ── Doctor Detail ──
        composable(
            route = Routes.DOCTOR_DETAIL,
            arguments = listOf(
                navArgument("doctorId") { type = NavType.StringType },
                navArgument("fromAppointment") { type = NavType.StringType; defaultValue = "false" },
                navArgument("appointmentDateTime") { type = NavType.StringType; defaultValue = "0" },
                navArgument("appointmentComplaint") { type = NavType.StringType; defaultValue = "" },
            )
        ) { backStackEntry ->
            val doctorId = backStackEntry.arguments?.getString("doctorId") ?: return@composable
            val fromAppointment = backStackEntry.arguments?.getString("fromAppointment") == "true"
            val appointmentDateTime = backStackEntry.arguments?.getString("appointmentDateTime")?.toLongOrNull() ?: 0L
            val appointmentComplaint = java.net.URLDecoder.decode(
                backStackEntry.arguments?.getString("appointmentComplaint") ?: "", "UTF-8"
            )
            val bookingViewModel: BookingViewModel = hiltViewModel()
            DoctorDetailScreen(
                doctorId = doctorId,
                viewModel = bookingViewModel,
                onNavigateBack = { navController.popBackStack() },
                fromAppointment = fromAppointment,
                appointmentDateTime = appointmentDateTime,
                appointmentComplaint = appointmentComplaint,
            )
        }

        // ── Patient Appointments (full list) ──
        composable(Routes.PATIENT_APPOINTMENTS) {
            val bookingViewModel: BookingViewModel = hiltViewModel()
            PatientAppointmentsScreen(
                viewModel = bookingViewModel,
                onNavigateBack = { navController.popBackStack() },
            )
        }

        // ── Nearby Hospitals (Phase 9) ──
        composable(Routes.NEARBY_HOSPITALS) {
            NearbyHospitalsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

/**
 * Temporary placeholder screen for routes not yet implemented.
 */
@Composable
private fun PlaceholderScreen(name: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}


