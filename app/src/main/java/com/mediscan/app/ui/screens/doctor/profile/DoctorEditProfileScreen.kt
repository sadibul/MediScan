package com.mediscan.app.ui.screens.doctor.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mediscan.app.core.theme.MediBlue
import com.mediscan.app.core.utils.NetworkResult
import com.mediscan.app.data.model.User
import com.mediscan.app.ui.components.common.MediButton
import com.mediscan.app.ui.components.common.MediTextField
import com.mediscan.app.ui.viewmodel.DoctorViewModel

/**
 * DoctorEditProfileScreen — edit doctor-specific fields:
 * Full Name, Phone, Specialization, Hospital, License Number,
 * Consultation Fee, Available Days, Available Time Range, Profile Photo.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DoctorEditProfileScreen(
    viewModel: DoctorViewModel,
    onNavigateBack: () -> Unit,
) {
    val profileState by viewModel.doctorProfile.collectAsState()
    val updateState by viewModel.updateProfileState.collectAsState()
    val uploadImageState by viewModel.uploadImageState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val currentUser = (profileState as? NetworkResult.Success)?.data
    var fullName by rememberSaveable { mutableStateOf(currentUser?.fullName ?: "") }
    var phone by rememberSaveable { mutableStateOf(currentUser?.phone ?: "") }
    var specialization by rememberSaveable { mutableStateOf(currentUser?.specialization ?: "") }
    var hospital by rememberSaveable { mutableStateOf(currentUser?.hospital ?: "") }
    var licenseNumber by rememberSaveable { mutableStateOf(currentUser?.licenseNumber ?: "") }
    var consultationFee by rememberSaveable { mutableStateOf(currentUser?.consultationFee ?: "") }
    var availableTimeRange by rememberSaveable { mutableStateOf(currentUser?.availableTimeRange ?: "") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val allDays = listOf("Sat", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri")
    val selectedDays = remember {
        (currentUser?.availableDays ?: emptyList()).toMutableStateList()
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            viewModel.uploadProfileImage(it)
        }
    }

    LaunchedEffect(updateState) {
        when (updateState) {
            is NetworkResult.Success -> {
                snackbarHostState.showSnackbar("Profile updated successfully!")
                viewModel.resetUpdateProfileState()
                onNavigateBack()
            }
            is NetworkResult.Error -> {
                snackbarHostState.showSnackbar((updateState as NetworkResult.Error).message)
                viewModel.resetUpdateProfileState()
            }
            else -> {}
        }
    }

    LaunchedEffect(uploadImageState) {
        when (uploadImageState) {
            is NetworkResult.Success -> {
                snackbarHostState.showSnackbar("Profile photo updated!")
                viewModel.resetUploadImageState()
            }
            is NetworkResult.Error -> {
                snackbarHostState.showSnackbar("Photo upload failed")
                viewModel.resetUploadImageState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                modifier = Modifier.background(
                    Brush.horizontalGradient(
                        colors = listOf(Color(0xFF1A237E), Color(0xFF3F51B5), Color(0xFF5C6BC0))
                    )
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFFF4F6FB)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // ═══════════════════════════════════════
            // Profile Photo Card
            // ═══════════════════════════════════════
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(contentAlignment = Alignment.BottomEnd) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(Color(0xFF3F51B5), Color(0xFF5C6BC0))
                                    )
                                )
                                .clickable { imagePickerLauncher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            val imageUrl = selectedImageUri?.toString() ?: currentUser?.profileImageUrl
                            if (!imageUrl.isNullOrBlank()) {
                                AsyncImage(
                                    model = imageUrl, contentDescription = "Profile photo",
                                    modifier = Modifier.size(100.dp).clip(CircleShape),
                                    contentScale = ContentScale.Crop,
                                )
                            } else {
                                val initials = (currentUser?.fullName ?: "D")
                                    .split(" ").take(2)
                                    .mapNotNull { it.firstOrNull()?.uppercase() }
                                    .joinToString("").ifEmpty { "D" }
                                Text(initials, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            if (uploadImageState is NetworkResult.Loading) {
                                Box(
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black.copy(alpha = 0.4f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(28.dp), strokeWidth = 2.dp)
                                }
                            }
                        }
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF3F51B5))
                                .border(2.dp, Color.White, CircleShape)
                                .clickable { imagePickerLauncher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CameraAlt, "Change photo", tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Tap to change photo", fontSize = 12.sp, color = Color(0xFF9E9E9E))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ═══════════════════════════════════════
            // Basic Info Section
            // ═══════════════════════════════════════
            EditSectionCard(
                title = "Basic Information",
                icon = Icons.Default.Person,
                accentColor = Color(0xFF3F51B5)
            ) {
                MediTextField(value = fullName, onValueChange = { fullName = it }, label = "Full Name", modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(12.dp))
                MediTextField(value = phone, onValueChange = { phone = it }, label = "Phone Number", keyboardType = KeyboardType.Phone, modifier = Modifier.fillMaxWidth())
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ═══════════════════════════════════════
            // Professional Info Section
            // ═══════════════════════════════════════
            EditSectionCard(
                title = "Professional Information",
                icon = Icons.Default.MedicalServices,
                accentColor = Color(0xFF43A047)
            ) {
                MediTextField(value = specialization, onValueChange = { specialization = it }, label = "Specialization", modifier = Modifier.fillMaxWidth(), placeholder = "e.g. Cardiologist")
                Spacer(modifier = Modifier.height(12.dp))
                MediTextField(value = hospital, onValueChange = { hospital = it }, label = "Hospital / Clinic", modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(12.dp))
                MediTextField(value = licenseNumber, onValueChange = { licenseNumber = it }, label = "License Number (BMDC)", modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(12.dp))
                MediTextField(value = consultationFee, onValueChange = { consultationFee = it }, label = "Consultation Fee (৳)", keyboardType = KeyboardType.Number, modifier = Modifier.fillMaxWidth())
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ═══════════════════════════════════════
            // Availability Section
            // ═══════════════════════════════════════
            EditSectionCard(
                title = "Availability",
                icon = Icons.Default.Schedule,
                accentColor = Color(0xFFFF9800)
            ) {
                Text("Available Days", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF616161))
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    allDays.forEach { day ->
                        val isSelected = day in selectedDays
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                if (isSelected) selectedDays.remove(day) else selectedDays.add(day)
                            },
                            label = {
                                Text(day, fontSize = 13.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFFF9800).copy(alpha = 0.15f),
                                selectedLabelColor = Color(0xFFFF9800),
                                containerColor = Color(0xFFF5F5F5),
                                labelColor = Color(0xFF757575),
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
                MediTextField(value = availableTimeRange, onValueChange = { availableTimeRange = it }, label = "Available Time Range", modifier = Modifier.fillMaxWidth(), placeholder = "e.g. 9:00 AM – 5:00 PM")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ═══════════════════════════════════════
            // Save Button
            // ═══════════════════════════════════════
            MediButton(
                text = "Save Changes",
                onClick = {
                    if (currentUser != null) {
                        val updated = currentUser.copy(
                            fullName = fullName.trim(),
                            phone = phone.trim(),
                            specialization = specialization.trim().ifBlank { null },
                            hospital = hospital.trim().ifBlank { null },
                            licenseNumber = licenseNumber.trim().ifBlank { null },
                            consultationFee = consultationFee.trim().ifBlank { null },
                            availableDays = selectedDays.toList().ifEmpty { null },
                            availableTimeRange = availableTimeRange.trim().ifBlank { null },
                        )
                        viewModel.updateDoctorProfile(updated)
                    }
                },
                isLoading = updateState is NetworkResult.Loading,
                enabled = fullName.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// ═══════════════════════════════════════════════════════════
// Edit Section Card — white card with colored accent bar
// ═══════════════════════════════════════════════════════════
@Composable
private fun EditSectionCard(
    title: String,
    icon: ImageVector,
    accentColor: Color,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            // Accent bar
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .fillMaxHeight()
                    .background(accentColor, RoundedCornerShape(topStart = 18.dp, bottomStart = 18.dp))
            )

            Column(modifier = Modifier.padding(16.dp)) {
                // Section header
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(accentColor.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, null, tint = accentColor, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF424242)
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
                content()
            }
        }
    }
}
