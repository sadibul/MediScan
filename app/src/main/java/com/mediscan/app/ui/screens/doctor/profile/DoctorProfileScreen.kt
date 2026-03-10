package com.mediscan.app.ui.screens.doctor.profile

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mediscan.app.core.theme.ErrorRed
import com.mediscan.app.core.theme.HealthGreen
import com.mediscan.app.core.theme.MediBlue
import com.mediscan.app.core.theme.TextSecondary
import com.mediscan.app.core.theme.WarningOrange
import com.mediscan.app.core.utils.NetworkResult
import com.mediscan.app.data.model.User
import com.mediscan.app.ui.viewmodel.DoctorViewModel

/**
 * DoctorProfileScreen — shows doctor info including specialization,
 * hospital, fee, availability + menu items for edit/change password/logout.
 */
@Composable
fun DoctorProfileScreen(
    viewModel: DoctorViewModel,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToChangePassword: () -> Unit,
    onLogout: () -> Unit,
) {
    val profileState by viewModel.doctorProfile.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6FB))
    ) {
        when (profileState) {
            is NetworkResult.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MediBlue)
                }
            }
            is NetworkResult.Success -> {
                val user = (profileState as NetworkResult.Success<User>).data
                DoctorProfileContent(
                    user = user,
                    onNavigateToEditProfile = onNavigateToEditProfile,
                    onNavigateToChangePassword = onNavigateToChangePassword,
                    onLogout = onLogout
                )
            }
            else -> {
                DoctorProfileContent(
                    user = User(fullName = "Doctor", email = ""),
                    onNavigateToEditProfile = onNavigateToEditProfile,
                    onNavigateToChangePassword = onNavigateToChangePassword,
                    onLogout = onLogout
                )
            }
        }
    }
}

@Composable
private fun DoctorProfileContent(
    user: User,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToChangePassword: () -> Unit,
    onLogout: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // ═══════════════════════════════════════════
        // Gradient Header with Avatar
        // ═══════════════════════════════════════════
        Box(modifier = Modifier.fillMaxWidth()) {
            // Gradient background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF1A237E),
                                Color(0xFF3F51B5),
                                Color(0xFF5C6BC0)
                            )
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("My Profile", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Doctor Dashboard", fontSize = 13.sp, color = Color.White.copy(alpha = 0.7f))
                }
            }

            // Floating profile card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .offset(y = 140.dp),
                shape = RoundedCornerShape(22.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFF3F51B5), Color(0xFF5C6BC0))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!user.profileImageUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = user.profileImageUrl,
                                contentDescription = "Profile Photo",
                                modifier = Modifier.size(88.dp).clip(CircleShape),
                                contentScale = ContentScale.Crop,
                            )
                        } else {
                            val initials = user.fullName
                                .split(" ").take(2)
                                .mapNotNull { it.firstOrNull()?.uppercase() }
                                .joinToString("").ifEmpty { "D" }
                            Text(initials, fontSize = 30.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        "Dr. ${user.fullName.ifBlank { "Doctor" }}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A2E)
                    )

                    if (!user.specialization.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Verified, null,
                                tint = Color(0xFF3F51B5),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                user.specialization,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF3F51B5)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(user.email, fontSize = 13.sp, color = Color(0xFF9E9E9E))

                    if (!user.phone.isNullOrBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Phone, null, modifier = Modifier.size(13.dp), tint = Color(0xFF9E9E9E))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(user.phone, fontSize = 12.sp, color = Color(0xFF9E9E9E))
                        }
                    }
                }
            }
        }

        // Spacer for floating card offset
        Spacer(modifier = Modifier.height(170.dp))

        // ═══════════════════════════════════════════
        // Professional Info Card
        // ═══════════════════════════════════════════
        if (user.specialization != null || user.hospital != null || user.consultationFee != null) {
            SectionLabel("PROFESSIONAL INFO")

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (!user.specialization.isNullOrBlank()) {
                        InfoItem(Icons.Default.MedicalServices, "Specialization", user.specialization, Color(0xFF3F51B5))
                    }
                    if (!user.hospital.isNullOrBlank()) {
                        InfoItem(Icons.Default.LocalHospital, "Hospital", user.hospital, HealthGreen)
                    }
                    if (!user.consultationFee.isNullOrBlank()) {
                        InfoItem(Icons.Default.AttachMoney, "Consultation Fee", "৳${user.consultationFee}", WarningOrange)
                    }
                    if (!user.licenseNumber.isNullOrBlank()) {
                        InfoItem(Icons.Default.Policy, "License No.", user.licenseNumber, Color(0xFF607D8B))
                    }
                    if (!user.availableDays.isNullOrEmpty()) {
                        InfoItem(Icons.Default.Schedule, "Available Days", user.availableDays.joinToString(", "), Color(0xFF00BCD4))
                    }
                    if (!user.availableTimeRange.isNullOrBlank()) {
                        InfoItem(Icons.Default.Schedule, "Timings", user.availableTimeRange, Color(0xFF00BCD4))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // ═══════════════════════════════════════════
        // Account Section
        // ═══════════════════════════════════════════
        SectionLabel("ACCOUNT")

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column {
                DoctorMenuItem(
                    icon = Icons.Default.Edit,
                    iconTint = Color(0xFF3F51B5),
                    title = "Edit Profile",
                    subtitle = "Update your professional information",
                    onClick = onNavigateToEditProfile
                )
                MenuDivider()
                DoctorMenuItem(
                    icon = Icons.Default.Lock,
                    iconTint = Color(0xFF9C27B0),
                    title = "Change Password",
                    subtitle = "Update your password",
                    onClick = onNavigateToChangePassword
                )
                MenuDivider()
                DoctorMenuItem(
                    icon = Icons.Default.Notifications,
                    iconTint = Color(0xFFFF9800),
                    title = "Notification Settings",
                    subtitle = "Manage your notifications",
                    onClick = { /* Future */ }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ═══════════════════════════════════════════
        // General Section
        // ═══════════════════════════════════════════
        SectionLabel("GENERAL")

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column {
                DoctorMenuItem(
                    icon = Icons.AutoMirrored.Filled.Help,
                    iconTint = Color(0xFF607D8B),
                    title = "Help & Support",
                    subtitle = "Get help and FAQs",
                    onClick = { /* Future */ }
                )
                MenuDivider()
                DoctorMenuItem(
                    icon = Icons.Default.Policy,
                    iconTint = Color(0xFF795548),
                    title = "Terms & Privacy Policy",
                    subtitle = "Read our policies",
                    onClick = { /* Future */ }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ═══════════════════════════════════════════
        // Logout
        // ═══════════════════════════════════════════
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            DoctorMenuItem(
                icon = Icons.AutoMirrored.Filled.Logout,
                iconTint = ErrorRed,
                title = "Logout",
                subtitle = "Sign out of your account",
                titleColor = ErrorRed,
                onClick = onLogout
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// ═══════════════════════════════════════════════════════════
// Shared composables
// ═══════════════════════════════════════════════════════════

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF9E9E9E),
        letterSpacing = 1.sp,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp)
    )
}

@Composable
private fun MenuDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 68.dp)
            .height(0.5.dp)
            .background(Color(0xFFEEEEEE))
    )
}

@Composable
private fun InfoItem(icon: ImageVector, label: String, value: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(label, fontSize = 11.sp, color = Color(0xFF9E9E9E), fontWeight = FontWeight.Medium)
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF424242))
        }
    }
}

@Composable
private fun DoctorMenuItem(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    titleColor: Color = Color(0xFF212121),
    onClick: () -> Unit,
) {
    androidx.compose.material3.Surface(
        onClick = onClick,
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconTint.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = iconTint, modifier = Modifier.size(22.dp))
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold, color = titleColor)
                Text(subtitle, fontSize = 12.sp, color = Color(0xFF9E9E9E))
            }

            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF5F5F5)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.ChevronRight, null, tint = Color(0xFFBDBDBD), modifier = Modifier.size(18.dp))
            }
        }
    }
}
