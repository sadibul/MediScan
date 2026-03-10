package com.mediscan.app.ui.screens.patient

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Home
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
import com.mediscan.app.ui.screens.patient.docs.DocsScreen
import com.mediscan.app.ui.screens.patient.docs.PrescriptionDetailScreen
import com.mediscan.app.ui.screens.patient.docs.PrescriptionDetailViewModel
import com.mediscan.app.ui.screens.patient.home.PatientHomeScreen
import com.mediscan.app.ui.screens.patient.medicine.BuyMedicineScreen
import com.mediscan.app.ui.screens.notifications.NotificationsScreen
import com.mediscan.app.ui.screens.patient.orders.DoctorOrdersScreen
import com.mediscan.app.ui.screens.patient.profile.ChangePasswordScreen
import com.mediscan.app.ui.screens.patient.profile.EditProfileScreen
import com.mediscan.app.ui.screens.patient.profile.PatientProfileScreen
import com.mediscan.app.ui.screens.patient.scan.ScanScreen
import com.mediscan.app.ui.viewmodel.AuthViewModel
import com.mediscan.app.ui.viewmodel.DocsViewModel
import com.mediscan.app.ui.viewmodel.NotificationViewModel
import com.mediscan.app.ui.viewmodel.PatientViewModel
import com.mediscan.app.ui.viewmodel.ScanViewModel

// Bottom nav tab routes (internal to patient flow)
private object PatientTabs {
    const val HOME = "patient_tab_home"
    const val SCAN = "patient_tab_scan"
    const val DOCS = "patient_tab_docs"
    const val PROFILE = "patient_tab_profile"
    const val EDIT_PROFILE = "patient_tab_edit_profile"
    const val CHANGE_PASSWORD = "patient_tab_change_password"
    const val PRESCRIPTION_DETAIL = "patient_tab_prescription_detail/{prescriptionId}"
    const val DOCTOR_ORDERS = "patient_tab_doctor_orders"
    const val BUY_MEDICINE = "patient_tab_buy_medicine"
    const val NOTIFICATIONS = "patient_tab_notifications"

    fun prescriptionDetail(id: String) = "patient_tab_prescription_detail/$id"
}

// Bottom nav item definition
private data class BottomNavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)

private val bottomNavItems = listOf(
    BottomNavItem(PatientTabs.HOME, "Home", Icons.Filled.Home, Icons.Outlined.Home),
    BottomNavItem(PatientTabs.SCAN, "Scan", Icons.Filled.CameraAlt, Icons.Outlined.CameraAlt),
    BottomNavItem(PatientTabs.DOCS, "Docs", Icons.Filled.Description, Icons.Outlined.Description),
    BottomNavItem(PatientTabs.PROFILE, "Profile", Icons.Filled.Person, Icons.Outlined.Person),
)

/**
 * PatientMainScreen — contains a bottom navigation bar with 4 tabs
 * and a nested NavHost for tab content.
 */
@Composable
fun PatientMainScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToDoctorSearch: () -> Unit = {},
    onNavigateToNearbyHospitals: () -> Unit = {},
    onNavigateToAppointments: () -> Unit = {},
    onNavigateToDoctorDetail: (doctorId: String, appointmentDateTime: Long?, appointmentComplaint: String?) -> Unit = { _, _, _ -> },
) {
    val nestedNavController = rememberNavController()
    val patientViewModel: PatientViewModel = hiltViewModel()
    val notificationViewModel: NotificationViewModel = hiltViewModel()
    val authViewModel: AuthViewModel = hiltViewModel()
    val scanViewModel: ScanViewModel = hiltViewModel()
    val docsViewModel: DocsViewModel = hiltViewModel()

    Scaffold(
        bottomBar = {
            PatientBottomBar(navController = nestedNavController)
        }
    ) { innerPadding ->
        NavHost(
            navController = nestedNavController,
            startDestination = PatientTabs.HOME,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(PatientTabs.HOME) {
                PatientHomeScreen(
                    viewModel = patientViewModel,
                    notificationViewModel = notificationViewModel,
                    onNavigateToDoctorSearch = onNavigateToDoctorSearch,
                    onNavigateToNearbyHospitals = onNavigateToNearbyHospitals,
                    onNavigateToAppointments = onNavigateToAppointments,
                    onNavigateToDoctorOrders = {
                        nestedNavController.navigate(PatientTabs.DOCTOR_ORDERS)
                    },
                    onNavigateToBuyMedicine = {
                        nestedNavController.navigate(PatientTabs.BUY_MEDICINE)
                    },
                    onNavigateToDoctorDetail = onNavigateToDoctorDetail,
                    onNavigateToNotifications = {
                        nestedNavController.navigate(PatientTabs.NOTIFICATIONS)
                    },
                )
            }

            composable(PatientTabs.SCAN) {
                ScanScreen(viewModel = scanViewModel)
            }

            composable(PatientTabs.DOCS) {
                DocsScreen(
                    viewModel = docsViewModel,
                    onNavigateToDetail = { prescriptionId ->
                        nestedNavController.navigate(PatientTabs.prescriptionDetail(prescriptionId))
                    },
                    onNavigateToScan = {
                        nestedNavController.navigate(PatientTabs.SCAN) {
                            popUpTo(PatientTabs.HOME) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }

            composable(
                route = PatientTabs.PRESCRIPTION_DETAIL,
                arguments = listOf(navArgument("prescriptionId") { type = NavType.StringType })
            ) { backStackEntry ->
                val prescriptionId = backStackEntry.arguments?.getString("prescriptionId") ?: return@composable
                val detailViewModel: PrescriptionDetailViewModel = hiltViewModel()
                PrescriptionDetailScreen(
                    viewModel = detailViewModel,
                    prescriptionId = prescriptionId,
                    onNavigateBack = {
                        nestedNavController.popBackStack()
                    },
                )
            }

            composable(PatientTabs.PROFILE) {
                PatientProfileScreen(
                    viewModel = patientViewModel,
                    onNavigateToEditProfile = {
                        nestedNavController.navigate(PatientTabs.EDIT_PROFILE)
                    },
                    onNavigateToChangePassword = {
                        nestedNavController.navigate(PatientTabs.CHANGE_PASSWORD)
                    },
                    onLogout = {
                        authViewModel.signOut()
                        onNavigateToLogin()
                    },
                )
            }

            composable(PatientTabs.EDIT_PROFILE) {
                EditProfileScreen(
                    viewModel = patientViewModel,
                    onNavigateBack = {
                        nestedNavController.popBackStack()
                    },
                )
            }

            composable(PatientTabs.CHANGE_PASSWORD) {
                ChangePasswordScreen(
                    viewModel = patientViewModel,
                    onNavigateBack = {
                        nestedNavController.popBackStack()
                    },
                )
            }

            composable(PatientTabs.DOCTOR_ORDERS) {
                DoctorOrdersScreen(
                    viewModel = patientViewModel,
                    onNavigateBack = {
                        nestedNavController.popBackStack()
                    },
                )
            }

            composable(PatientTabs.BUY_MEDICINE) {
                BuyMedicineScreen(
                    onNavigateBack = {
                        nestedNavController.popBackStack()
                    },
                )
            }

            composable(PatientTabs.NOTIFICATIONS) {
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
private fun PatientBottomBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Hide bottom bar on sub-screens like EditProfile
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
                        // Pop up to start destination to avoid building up large back stack
                        popUpTo(PatientTabs.HOME) {
                            saveState = true
                        }
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
                    indicatorColor = MediBlue.copy(alpha = 0.12f)
                )
            )
        }
    }
}
