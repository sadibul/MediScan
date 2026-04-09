package com.mediscan.app.ui.screens.patient.scan

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.mediscan.app.core.theme.ErrorRed
import com.mediscan.app.core.theme.MediBlue
import com.mediscan.app.core.theme.TextSecondary
import com.mediscan.app.core.theme.WarningOrange
import com.mediscan.app.core.utils.NetworkResult
import com.mediscan.app.ui.components.ExtractionResultSheet
import com.mediscan.app.ui.components.common.MediButton
import com.mediscan.app.ui.components.common.MediOutlinedButton
import com.mediscan.app.ui.viewmodel.ScanState
import com.mediscan.app.ui.viewmodel.ScanViewModel

/**
 * ScanScreen — full implementation handling all ScanState transitions:
 *   Initial → Camera → CheckingQuality/Extracting → ResultReady/Rejected/Error
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScanScreen(viewModel: ScanViewModel) {
    val scanState by viewModel.scanState.collectAsState()
    val saveState by viewModel.saveState.collectAsState()
    val context = LocalContext.current

    // Camera permission
    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)

    // Gallery picker — delegates all heavy work to ViewModel on IO thread
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.processGalleryImage(it, context) }
    }

    // Reset to initial after successful save
    LaunchedEffect(saveState) {
        if (saveState is NetworkResult.Success) {
            viewModel.resetScan()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = scanState,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "scan_state"
        ) { state ->
            when (state) {
                // ── INITIAL: Landing screen with scan tips ──
                is ScanState.Initial -> {
                    ScanInitialContent(
                        onOpenCamera = {
                            if (cameraPermission.status.isGranted) {
                                viewModel.openCamera()
                            } else {
                                cameraPermission.launchPermissionRequest()
                            }
                        },
                        onChooseFromGallery = {
                            galleryLauncher.launch("image/*")
                        },
                        isCameraGranted = cameraPermission.status.isGranted,
                        shouldShowRationale = cameraPermission.status.shouldShowRationale,
                        onRequestPermission = { cameraPermission.launchPermissionRequest() }
                    )
                }

                // ── CAMERA: Full-screen CameraX preview ──
                is ScanState.Camera -> {
                    if (cameraPermission.status.isGranted) {
                        CameraPreviewScreen(
                            onImageCaptured = { imageBytes ->
                                viewModel.processImage(imageBytes)
                            },
                            onClose = { viewModel.resetScan() }
                        )
                    } else {
                        // Permission was revoked mid-session
                        PermissionNeededContent(
                            shouldShowRationale = cameraPermission.status.shouldShowRationale,
                            onRequestPermission = { cameraPermission.launchPermissionRequest() },
                            onGoBack = { viewModel.resetScan() }
                        )
                    }
                }

                // ── CHECKING QUALITY ──
                is ScanState.CheckingQuality -> {
                    ProcessingContent(
                        title = "Checking Image Quality…",
                        subtitle = "Analyzing your prescription photo"
                    )
                }

                // ── EXTRACTING ──
                is ScanState.Extracting -> {
                    ProcessingContent(
                        title = "Analyzing Prescription…",
                        subtitle = "AI is reading your prescription"
                    )
                }

                // ── RESULT READY: Show bottom sheet ──
                is ScanState.ResultReady -> {
                    // Show a dimmed background placeholder
                    ProcessingContent(
                        title = "Prescription Extracted!",
                        subtitle = "Review the details below"
                    )
                }

                // ── REJECTED: Quality too poor ──
                is ScanState.Rejected -> {
                    RejectedContent(
                        message = state.message,
                        issues = state.quality?.issues ?: emptyList(),
                        onRetake = { viewModel.retryCapture() },
                        onCancel = { viewModel.resetScan() }
                    )
                }

                // ── ERROR ──
                is ScanState.Error -> {
                    ErrorContent(
                        message = state.message,
                        onRetry = { viewModel.retryCapture() },
                        onCancel = { viewModel.resetScan() }
                    )
                }
            }
        }

        // Show ExtractionResultSheet as overlay when result is ready
        if (scanState is ScanState.ResultReady) {
            val result = (scanState as ScanState.ResultReady).result
            // key() forces Compose to recreate the sheet (and its remember blocks)
            // each time we get a new result, preventing stale state.
            // Uses a stable counter from ViewModel — incremented exactly once per scan.
            val scanCount by viewModel.scanCounter.collectAsState()
            androidx.compose.runtime.key(scanCount) {
                ExtractionResultSheet(
                    result = result,
                    isSaving = saveState is NetworkResult.Loading,
                    onSave = { doctorName, hospital, visitDate, diagnosis, diagnoses, tests, medications ->
                        viewModel.savePrescription(
                            doctorName,
                            hospital,
                            visitDate,
                            diagnosis,
                            diagnoses,
                            tests,
                            medications
                        )
                    },
                    onDismiss = { viewModel.resetScan() }
                )
            }
        }
    }

    // Auto-navigate to camera when permission is freshly granted
    LaunchedEffect(cameraPermission.status.isGranted) {
        if (cameraPermission.status.isGranted && scanState is ScanState.Initial) {
            // Permission just granted — don't auto-open, user needs to tap FAB
        }
    }
}

// ════════════════════════════════════════════════════════
// Sub-composables for each state
// ════════════════════════════════════════════════════════

@Composable
private fun ScanInitialContent(
    onOpenCamera: () -> Unit,
    onChooseFromGallery: () -> Unit,
    isCameraGranted: Boolean,
    shouldShowRationale: Boolean,
    onRequestPermission: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6FB))
            .verticalScroll(rememberScrollState())
    ) {
        // ── Gradient Header ──────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    androidx.compose.ui.graphics.Brush.linearGradient(
                        colors = listOf(Color(0xFF1A237E), Color(0xFF3F51B5), Color(0xFF5C6BC0))
                    )
                )
                .padding(horizontal = 20.dp, vertical = 28.dp)
        ) {
            Column {
                Text(
                    text = "Scan Prescription",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "AI-powered medicine recognition",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── Main scan card ───────────────────────────────────────────────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Dashed camera icon area
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(Color(0xFFE8EAF6))
                        .border(
                            width = 2.dp,
                            color = Color(0xFF9FA8DA),
                            shape = RoundedCornerShape(28.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = null,
                        tint = Color(0xFF7986CB),
                        modifier = Modifier.size(56.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Scan Your Prescription",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A2E),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Take a photo of your prescription and our AI\nwill extract medication details automatically.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(28.dp))

                if (isCameraGranted) {
                    // "Open Camera & Scan" primary button
                    Button(
                        onClick = onOpenCamera,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF3F51B5)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Open Camera & Scan",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Divider with "or"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(1.dp)
                                .background(Color(0xFFE0E0E0))
                        )
                        Text(
                            text = "  or  ",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(1.dp)
                                .background(Color(0xFFE0E0E0))
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // "Choose from Gallery" secondary button
                    OutlinedButton(
                        onClick = onChooseFromGallery,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.5.dp, Color(0xFF3F51B5)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF3F51B5)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = null,
                            tint = Color(0xFF3F51B5),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Choose from Gallery",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = Color(0xFF3F51B5)
                        )
                    }
                } else {
                    Text(
                        text = if (shouldShowRationale)
                            "Camera permission is needed to scan prescriptions"
                        else
                            "Tap below to grant camera permission",
                        style = MaterialTheme.typography.bodySmall,
                        color = WarningOrange,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    MediButton(
                        text = "Grant Camera Permission",
                        onClick = onRequestPermission
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Divider with "or"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(1.dp)
                                .background(Color(0xFFE0E0E0))
                        )
                        Text(
                            text = "  or  ",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(1.dp)
                                .background(Color(0xFFE0E0E0))
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Gallery always available — no camera permission needed
                    OutlinedButton(
                        onClick = onChooseFromGallery,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.5.dp, Color(0xFF3F51B5)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF3F51B5)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = null,
                            tint = Color(0xFF3F51B5),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Choose from Gallery",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = Color(0xFF3F51B5)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── Tips card ────────────────────────────────────────────────────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFF9C4)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "💡", fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Tips for best results",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A2E)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                val tips = listOf(
                    "📄" to "Place prescription on a flat, clean surface",
                    "✨" to "Ensure good lighting and avoid shadows",
                    "📱" to "Keep the camera steady while scanning",
                    "📐" to "Capture the full prescription in frame",
                )
                tips.forEach { (emoji, tip) ->
                    Row(
                        modifier = Modifier.padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF4F6FB)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = emoji, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = tip,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF424242),
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun ProcessingContent(title: String, subtitle: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            color = MediBlue,
            modifier = Modifier.size(64.dp),
            strokeWidth = 5.dp,
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun RejectedContent(
    message: String,
    issues: List<String>,
    onRetake: () -> Unit,
    onCancel: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = WarningOrange,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Image Quality Issue",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        if (issues.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        WarningOrange.copy(alpha = 0.08f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp)
            ) {
                issues.forEach { issue ->
                    Text(
                        text = "• $issue",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        MediButton(text = "📸 Retake Photo", onClick = onRetake)
        Spacer(modifier = Modifier.height(12.dp))
        MediOutlinedButton(text = "Cancel", onClick = onCancel)
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    onCancel: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = null,
            tint = ErrorRed,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Something Went Wrong",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Make sure the backend server is running\nand the device is on the same network.",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        MediButton(text = "🔄 Try Again", onClick = onRetry)
        Spacer(modifier = Modifier.height(12.dp))
        MediOutlinedButton(text = "Cancel", onClick = onCancel)
    }
}

@Composable
private fun PermissionNeededContent(
    shouldShowRationale: Boolean,
    onRequestPermission: () -> Unit,
    onGoBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.CameraAlt,
            contentDescription = null,
            tint = WarningOrange,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Camera Permission Needed",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (shouldShowRationale)
                "MediScan needs camera access to scan your prescriptions. Please grant the permission."
            else
                "Camera permission was denied. You may need to enable it in your device Settings.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))
        MediButton(text = "Grant Permission", onClick = onRequestPermission)
        Spacer(modifier = Modifier.height(12.dp))
        MediOutlinedButton(text = "Go Back", onClick = onGoBack)
    }
}
