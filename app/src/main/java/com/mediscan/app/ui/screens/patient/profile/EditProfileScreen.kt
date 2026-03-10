package com.mediscan.app.ui.screens.patient.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
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
import com.mediscan.app.ui.viewmodel.PatientViewModel

/**
 * EditProfileScreen — form for editing patient profile.
 * Fields: Full Name, Phone, Date of Birth, Blood Group, Height, Weight, Address, Emergency Contact.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    viewModel: PatientViewModel,
    onNavigateBack: () -> Unit,
) {
    val userProfileState by viewModel.userProfile.collectAsState()
    val updateState by viewModel.updateProfileState.collectAsState()
    val uploadImageState by viewModel.uploadImageState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Pre-fill fields from current profile
    val currentUser = (userProfileState as? NetworkResult.Success)?.data
    var fullName by rememberSaveable { mutableStateOf(currentUser?.fullName ?: "") }
    var phone by rememberSaveable { mutableStateOf(currentUser?.phone ?: "") }
    var dateOfBirth by rememberSaveable { mutableStateOf(currentUser?.dateOfBirth ?: "") }
    var bloodGroup by rememberSaveable { mutableStateOf(currentUser?.bloodGroup ?: "") }
    var address by rememberSaveable { mutableStateOf(currentUser?.address ?: "") }
    var emergencyContact by rememberSaveable { mutableStateOf(currentUser?.emergencyContact ?: "") }
    var height by rememberSaveable { mutableStateOf(currentUser?.height ?: "") }
    var weight by rememberSaveable { mutableStateOf(currentUser?.weight ?: "") }
    var bloodGroupExpanded by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val bloodGroupOptions = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            viewModel.uploadProfileImage(it)
        }
    }

    // Handle update result
    LaunchedEffect(updateState) {
        when (updateState) {
            is NetworkResult.Success -> {
                snackbarHostState.showSnackbar("Profile updated successfully!")
                viewModel.resetUpdateState()
                onNavigateBack()
            }
            is NetworkResult.Error -> {
                snackbarHostState.showSnackbar(
                    (updateState as NetworkResult.Error).message
                )
                viewModel.resetUpdateState()
            }
            else -> {}
        }
    }

    // Handle image upload result
    LaunchedEffect(uploadImageState) {
        when (uploadImageState) {
            is NetworkResult.Success -> {
                snackbarHostState.showSnackbar("Profile photo updated!")
                viewModel.resetUploadImageState()
            }
            is NetworkResult.Error -> {
                snackbarHostState.showSnackbar(
                    "Photo upload failed: ${(uploadImageState as NetworkResult.Error).message}"
                )
                viewModel.resetUploadImageState()
            }
            else -> {}
        }
    }

    Scaffold(
        containerColor = Color(0xFFF4F6FB),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Edit Profile",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                modifier = Modifier.background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF1A237E),
                            Color(0xFF3F51B5),
                            Color(0xFF5C6BC0)
                        )
                    )
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // ═══════════════════════════════════════════
            // Profile Photo Card
            // ═══════════════════════════════════════════
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
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
                            val imageUrl = selectedImageUri?.toString()
                                ?: currentUser?.profileImageUrl

                            if (!imageUrl.isNullOrBlank()) {
                                AsyncImage(
                                    model = imageUrl,
                                    contentDescription = "Profile photo",
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop,
                                )
                            } else {
                                val initials = (currentUser?.fullName ?: "U")
                                    .split(" ")
                                    .take(2)
                                    .mapNotNull { it.firstOrNull()?.uppercase() }
                                    .joinToString("")
                                    .ifEmpty { "U" }
                                Text(
                                    initials,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            // Upload progress overlay
                            if (uploadImageState is NetworkResult.Loading) {
                                Box(
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black.copy(alpha = 0.4f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        modifier = Modifier.size(28.dp),
                                        strokeWidth = 2.dp
                                    )
                                }
                            }
                        }

                        // Camera badge
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .offset(x = (-2).dp, y = (-2).dp)
                                .clip(CircleShape)
                                .background(Color(0xFF3F51B5))
                                .border(2.dp, Color.White, CircleShape)
                                .clickable { imagePickerLauncher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.CameraAlt,
                                contentDescription = "Change photo",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Tap to change photo",
                        fontSize = 12.sp,
                        color = Color(0xFF9E9E9E)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ═══════════════════════════════════════════
            // Personal Information Card
            // ═══════════════════════════════════════════
            EditSectionCard(
                title = "Personal Information",
                icon = Icons.Default.Person,
                accentColor = Color(0xFF3F51B5)
            ) {
                MediTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = "Full Name",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                MediTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = "Phone Number",
                    keyboardType = KeyboardType.Phone,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                MediTextField(
                    value = dateOfBirth,
                    onValueChange = { dateOfBirth = it },
                    label = "Date of Birth (DD/MM/YYYY)",
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ═══════════════════════════════════════════
            // Health Information Card
            // ═══════════════════════════════════════════
            EditSectionCard(
                title = "Health Information",
                icon = Icons.Default.Bloodtype,
                accentColor = Color(0xFF43A047)
            ) {
                // Blood Group dropdown
                ExposedDropdownMenuBox(
                    expanded = bloodGroupExpanded,
                    onExpandedChange = { bloodGroupExpanded = it }
                ) {
                    MediTextField(
                        value = bloodGroup,
                        onValueChange = {},
                        label = "Blood Group",
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = bloodGroupExpanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = bloodGroupExpanded,
                        onDismissRequest = { bloodGroupExpanded = false }
                    ) {
                        bloodGroupOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    bloodGroup = option
                                    bloodGroupExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Height & Weight row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MediTextField(
                        value = height,
                        onValueChange = { height = it },
                        label = "Height (ft)",
                        keyboardType = KeyboardType.Decimal,
                        modifier = Modifier.weight(1f)
                    )
                    MediTextField(
                        value = weight,
                        onValueChange = { weight = it },
                        label = "Weight (kg)",
                        keyboardType = KeyboardType.Decimal,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ═══════════════════════════════════════════
            // Contact & Address Card
            // ═══════════════════════════════════════════
            EditSectionCard(
                title = "Contact & Address",
                icon = Icons.Default.Home,
                accentColor = Color(0xFFFF9800)
            ) {
                MediTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = "Address",
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(12.dp))

                MediTextField(
                    value = emergencyContact,
                    onValueChange = { emergencyContact = it },
                    label = "Emergency Contact",
                    keyboardType = KeyboardType.Phone,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ═══════════════════════════════════════════
            // Save Button
            // ═══════════════════════════════════════════
            MediButton(
                text = "Save Changes",
                onClick = {
                    if (currentUser != null) {
                        val updatedUser = currentUser.copy(
                            fullName = fullName.trim(),
                            phone = phone.trim(),
                            dateOfBirth = dateOfBirth.trim().ifBlank { null },
                            bloodGroup = bloodGroup.ifBlank { null },
                            address = address.trim().ifBlank { null },
                            emergencyContact = emergencyContact.trim().ifBlank { null },
                            height = height.trim().ifBlank { null },
                            weight = weight.trim().ifBlank { null },
                        )
                        viewModel.updateUserProfile(updatedUser)
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
// Edit Section Card — reusable card with colored left accent
// ═══════════════════════════════════════════════════════════
@Composable
private fun EditSectionCard(
    title: String,
    icon: ImageVector,
    accentColor: Color = MediBlue,
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
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(accentColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = accentColor, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A2E)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}
