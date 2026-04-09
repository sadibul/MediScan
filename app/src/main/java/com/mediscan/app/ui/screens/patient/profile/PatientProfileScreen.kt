package com.mediscan.app.ui.screens.patient.profile

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
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Policy
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
import com.mediscan.app.core.theme.MediBlue
import com.mediscan.app.core.theme.TextSecondary
import com.mediscan.app.core.utils.NetworkResult
import com.mediscan.app.data.model.User
import com.mediscan.app.ui.viewmodel.PatientViewModel

/**
 * PatientProfileScreen — shows user info and menu items.
 * Part of the patient bottom navigation (Profile tab).
 */
@Composable
fun PatientProfileScreen(
    viewModel: PatientViewModel,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToChangePassword: () -> Unit,
    onLogout: () -> Unit,
) {
    val userProfileState by viewModel.userProfile.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6FB))
    ) {
        when (userProfileState) {
            is NetworkResult.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MediBlue)
                }
            }
            is NetworkResult.Success -> {
                val user = (userProfileState as NetworkResult.Success<User>).data
                ProfileContent(
                    user = user,
                    onNavigateToEditProfile = onNavigateToEditProfile,
                    onNavigateToChangePassword = onNavigateToChangePassword,
                    onLogout = onLogout
                )
            }
            else -> {
                ProfileContent(
                    user = User(fullName = "User", email = ""),
                    onNavigateToEditProfile = onNavigateToEditProfile,
                    onNavigateToChangePassword = onNavigateToChangePassword,
                    onLogout = onLogout
                )
            }
        }
    }
}

@Composable
private fun ProfileContent(
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
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
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
                    Text(
                        "My Profile",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Manage your account",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
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
                                modifier = Modifier
                                    .size(88.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop,
                            )
                        } else {
                            val initials = user.fullName
                                .split(" ")
                                .take(2)
                                .mapNotNull { it.firstOrNull()?.uppercase() }
                                .joinToString("")
                                .ifEmpty { "U" }
                            Text(
                                initials,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = user.fullName.ifBlank { "User" },
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A2E)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = user.email,
                        fontSize = 13.sp,
                        color = Color(0xFF9E9E9E)
                    )

                    if (!user.phone.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Phone, null,
                                modifier = Modifier.size(13.dp),
                                tint = Color(0xFF9E9E9E))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = user.phone,
                                fontSize = 12.sp,
                                color = Color(0xFF9E9E9E)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Quick health stats
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        QuickStatItem(
                            label = "Height",
                            value = user.height ?: "--",
                            unit = "ft",
                        )
                        QuickStatItem(
                            label = "Blood Group",
                            value = user.bloodGroup ?: "--",
                            unit = "",
                        )
                        QuickStatItem(
                            label = "Weight",
                            value = user.weight ?: "--",
                            unit = "kg",
                        )
                    }
                }
            }
        }

        // Spacer to account for offset card
        Spacer(modifier = Modifier.height(170.dp))

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
                ProfileMenuItem(
                    icon = Icons.Default.Edit,
                    iconTint = Color(0xFF3F51B5),
                    title = "Edit Profile",
                    onClick = onNavigateToEditProfile
                )
                MenuDivider()
                ProfileMenuItem(
                    icon = Icons.Default.Lock,
                    iconTint = Color(0xFF9C27B0),
                    title = "Change Password",
                    onClick = onNavigateToChangePassword
                )
                MenuDivider()
                ProfileMenuItem(
                    icon = Icons.Default.Notifications,
                    iconTint = Color(0xFFFF9800),
                    title = "Notification Settings",
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
                ProfileMenuItem(
                    icon = Icons.AutoMirrored.Filled.Help,
                    iconTint = Color(0xFF607D8B),
                    title = "Help & Support",
                    onClick = { /* Future */ }
                )
                MenuDivider()
                ProfileMenuItem(
                    icon = Icons.Default.Policy,
                    iconTint = Color(0xFF795548),
                    title = "Terms & Privacy Policy",
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
            ProfileMenuItem(
                icon = Icons.AutoMirrored.Filled.Logout,
                iconTint = ErrorRed,
                title = "Logout",
                titleColor = ErrorRed,
                onClick = onLogout
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// ═══════════════════════════════════════════════════════════
// Quick Stat Item (in profile card)
// ═══════════════════════════════════════════════════════════
@Composable
private fun QuickStatItem(
    label: String,
    value: String,
    unit: String,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A2E)
            )
            if (unit.isNotEmpty()) {
                Text(
                    " $unit",
                    fontSize = 12.sp,
                    color = Color(0xFF9E9E9E),
                    modifier = Modifier.padding(bottom = 1.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(label, fontSize = 11.sp, color = Color(0xFF9E9E9E))
    }
}

// ═══════════════════════════════════════════════════════════
// Section Label
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

// ═══════════════════════════════════════════════════════════
// Menu Divider
// ═══════════════════════════════════════════════════════════
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

// ═══════════════════════════════════════════════════════════
// Profile Menu Item
// ═══════════════════════════════════════════════════════════
@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String? = null,
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
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = titleColor
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = Color(0xFF9E9E9E)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF5F5F5)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Color(0xFFBDBDBD),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
