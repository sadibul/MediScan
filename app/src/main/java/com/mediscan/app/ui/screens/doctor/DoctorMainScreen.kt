package com.mediscan.app.ui.screens.doctor

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mediscan.app.core.theme.MediBlue
import com.mediscan.app.core.theme.TextSecondary
import com.mediscan.app.ui.screens.doctor.appointments.DoctorAppointmentsScreen
import com.mediscan.app.ui.screens.doctor.appointments.PatientRecordsScreen
import com.mediscan.app.ui.screens.notifications.NotificationsScreen
import com.mediscan.app.ui.screens.doctor.profile.DoctorChangePasswordScreen
import com.mediscan.app.ui.screens.doctor.profile.DoctorEditProfileScreen
import com.mediscan.app.ui.screens.doctor.profile.DoctorProfileScreen
import com.mediscan.app.ui.screens.doctor.records.DoctorRecordsScreen
import com.mediscan.app.ui.viewmodel.AuthViewModel
import com.mediscan.app.ui.viewmodel.DoctorViewModel
import com.mediscan.app.ui.viewmodel.NotificationViewModel

// ── Internal tab routes ──
private object DoctorTabs {
    const val APPOINTMENTS = "doctor_tab_appointments"
    const val RECORDS = "doctor_tab_records"
    const val PROFILE = "doctor_tab_profile"
    const val EDIT_PROFILE = "doctor_tab_edit_profile"
    const val CHANGE_PASSWORD = "doctor_tab_change_password"
    const val PATIENT_RECORDS = "doctor_tab_patient_records/{patientId}"
    const val NOTIFICATIONS = "doctor_tab_notifications"

    fun patientRecords(patientId: String) = "doctor_tab_patient_records/$patientId"
}

private data class BottomNavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)

private val bottomNavItems = listOf(
    BottomNavItem(DoctorTabs.APPOINTMENTS, "Appointments", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth),
    BottomNavItem(DoctorTabs.RECORDS, "Records", Icons.Filled.Analytics, Icons.Outlined.Analytics),
    BottomNavItem(DoctorTabs.PROFILE, "Profile", Icons.Filled.Person, Icons.Outlined.Person),
)

/**
 * DoctorMainScreen — 3-tab bottom navigation:
 *  1. Appointments — manage patient appointments
 *  2. Records — analytics dashboard with Vico charts
 *  3. Profile — doctor info, edit profile, change password, logout
 */
@Composable
fun DoctorMainScreen(
    onNavigateToLogin: () -> Unit,
) {
    val nestedNavController = rememberNavController()
    val doctorViewModel: DoctorViewModel = hiltViewModel()
    val notificationViewModel: NotificationViewModel = hiltViewModel()
    val authViewModel: AuthViewModel = hiltViewModel()

    Scaffold(
        bottomBar = {
            DoctorBottomBar(navController = nestedNavController)
        }
    ) { innerPadding ->
        NavHost(
            navController = nestedNavController,
            startDestination = DoctorTabs.APPOINTMENTS,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(DoctorTabs.APPOINTMENTS) {
                DoctorAppointmentsScreen(
                    viewModel = doctorViewModel,
                    notificationViewModel = notificationViewModel,
                    onNavigateToPatientRecords = { patientId ->
                        nestedNavController.navigate(DoctorTabs.patientRecords(patientId))
                    },
                    onNavigateToNotifications = {
                        nestedNavController.navigate(DoctorTabs.NOTIFICATIONS)
                    },
                )
            }

            composable(DoctorTabs.RECORDS) {
                DoctorRecordsScreen(viewModel = doctorViewModel)
            }

            composable(DoctorTabs.PROFILE) {
                DoctorProfileScreen(
                    viewModel = doctorViewModel,
                    onNavigateToEditProfile = {
                        nestedNavController.navigate(DoctorTabs.EDIT_PROFILE)
                    },
                    onNavigateToChangePassword = {
                        nestedNavController.navigate(DoctorTabs.CHANGE_PASSWORD)
                    },
                    onLogout = {
                        authViewModel.signOut()
                        onNavigateToLogin()
                    },
                )
            }

            composable(DoctorTabs.EDIT_PROFILE) {
                DoctorEditProfileScreen(
                    viewModel = doctorViewModel,
                    onNavigateBack = { nestedNavController.popBackStack() },
                )
            }

            composable(DoctorTabs.CHANGE_PASSWORD) {
                DoctorChangePasswordScreen(
                    viewModel = doctorViewModel,
                    onNavigateBack = { nestedNavController.popBackStack() },
                )
            }

            // Patient records (Cleveland dot plot + pie chart)
            composable(
                route = DoctorTabs.PATIENT_RECORDS,
                arguments = listOf(navArgument("patientId") { type = NavType.StringType }),
            ) { backStackEntry ->
                val patientId = backStackEntry.arguments?.getString("patientId") ?: return@composable
                // Trigger data load
                androidx.compose.runtime.LaunchedEffect(patientId) {
                    doctorViewModel.loadPatientPrescriptions(patientId)
                    doctorViewModel.loadPatientReminders(patientId)
                }
                PatientRecordsScreen(
                    viewModel = doctorViewModel,
                    patientId = patientId,
                    onNavigateBack = { nestedNavController.popBackStack() },
                )
            }

            composable(DoctorTabs.NOTIFICATIONS) {
                NotificationsScreen(
                    viewModel = notificationViewModel,
                    onNavigateBack = {
                        nestedNavController.popBackStack()
                    },
                )
            }
        }
    }
}

@Composable
private fun DoctorBottomBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Only show bottom bar on main tabs
    val showBottomBar = bottomNavItems.any { it.route == currentDestination?.route }
    if (!showBottomBar) return

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
    ) {
        bottomNavItems.forEach { item ->
            val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(DoctorTabs.APPOINTMENTS) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MediBlue,
                    selectedTextColor = MediBlue,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary,
                    indicatorColor = MediBlue.copy(alpha = 0.12f),
                )
            )
        }
    }
}
