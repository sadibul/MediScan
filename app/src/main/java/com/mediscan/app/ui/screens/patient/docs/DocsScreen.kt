package com.mediscan.app.ui.screens.patient.docs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mediscan.app.core.theme.ErrorRed
import com.mediscan.app.core.theme.HealthGreen
import com.mediscan.app.core.theme.MediBlue
import com.mediscan.app.core.theme.TextSecondary
import com.mediscan.app.core.theme.WarningOrange
import com.mediscan.app.core.utils.DateUtils
import com.mediscan.app.core.utils.NetworkResult
import com.mediscan.app.data.model.Prescription
import com.mediscan.app.ui.components.common.MediButton
import com.mediscan.app.ui.components.common.ShimmerPrescriptionList
import com.mediscan.app.ui.viewmodel.DocsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * DocsScreen — Prescription History list (redesigned).
 * Gradient header, search bar, stats row, prescription cards with accent bars.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocsScreen(
    viewModel: DocsViewModel,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToScan: () -> Unit,
) {
    val prescriptions by viewModel.prescriptions.collectAsState()
    val deleteState by viewModel.deleteState.collectAsState()
    val scope = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }
    val pullRefreshState = rememberPullToRefreshState()
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { viewModel.loadPrescriptions() }

    LaunchedEffect(deleteState) {
        if (deleteState is NetworkResult.Success) {
            viewModel.loadPrescriptions()
            viewModel.resetDeleteState()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6FB))
    ) {
        // ── Gradient Header ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF1A237E), Color(0xFF3F51B5), Color(0xFF5C6BC0))
                    )
                )
                .padding(horizontal = 20.dp, vertical = 28.dp)
        ) {
            Column {
                Text(
                    text = "My Prescriptions",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Your scanned prescription history",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        // ── Search bar ──
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = {
                Text("Search prescriptions...", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = Color(0xFF3F51B5),
                unfocusedBorderColor = Color(0xFFE0E0E0),
            )
        )

        // ── Stats row ──
        when (val state = prescriptions) {
            is NetworkResult.Success -> {
                val list = state.data
                val totalDocs = list.size
                val totalMeds = list.sumOf { it.medications.size }
                val totalDoctors = list.mapNotNull { it.doctorName?.trim()?.lowercase() }
                    .distinct().size
                StatsRow(totalDocs = totalDocs, totalMeds = totalMeds, totalDoctors = totalDoctors)
            }
            else -> StatsRow(totalDocs = 0, totalMeds = 0, totalDoctors = 0)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ── Prescription List ──
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                scope.launch {
                    isRefreshing = true
                    viewModel.loadPrescriptions()
                    delay(600)
                    isRefreshing = false
                }
            },
            state = pullRefreshState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
        ) {
            when (val state = prescriptions) {
                is NetworkResult.Loading -> {
                    ShimmerPrescriptionList(count = 5)
                }

                is NetworkResult.Success -> {
                    val list = state.data
                    // Filter by search
                    val filtered = if (searchQuery.isBlank()) list
                    else list.filter { rx ->
                        rx.doctorName?.contains(searchQuery, ignoreCase = true) == true ||
                        rx.hospital?.contains(searchQuery, ignoreCase = true) == true ||
                        rx.diagnosis?.contains(searchQuery, ignoreCase = true) == true ||
                        rx.medications.any { it.medicine.contains(searchQuery, ignoreCase = true) }
                    }

                    if (filtered.isEmpty()) {
                        if (searchQuery.isNotBlank()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("No prescriptions match your search",
                                    style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                            }
                        } else {
                            EmptyPrescriptionState(onNavigateToScan = onNavigateToScan)
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                            contentPadding = PaddingValues(bottom = 16.dp, top = 4.dp)
                        ) {
                            items(filtered, key = { it.id }) { prescription ->
                                PrescriptionCard(
                                    prescription = prescription,
                                    onViewDetails = { onNavigateToDetail(prescription.id) },
                                    onDelete = { viewModel.deletePrescription(prescription) },
                                )
                            }
                        }
                    }
                }

                is NetworkResult.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(32.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(ErrorRed.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.ErrorOutline, null,
                                    tint = ErrorRed.copy(alpha = 0.7f),
                                    modifier = Modifier.size(32.dp),
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Failed to Load",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(state.message,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary, textAlign = TextAlign.Center)
                            Spacer(modifier = Modifier.height(20.dp))
                            MediButton(text = "🔄  Retry",
                                onClick = { viewModel.loadPrescriptions() },
                                modifier = Modifier.width(160.dp))
                        }
                    }
                }

                else -> {}
            }
        }
    }
}

// ── Stats Row ─────────────────────────────────────────────────────────────────

@Composable
private fun StatsRow(totalDocs: Int, totalMeds: Int, totalDoctors: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatChip(value = "$totalDocs", label = "TOTAL DOCS", modifier = Modifier.weight(1f))
        StatChip(value = "$totalMeds", label = "MEDICINES", modifier = Modifier.weight(1f))
        StatChip(value = "$totalDoctors", label = "DOCTORS", modifier = Modifier.weight(1f))
    }
}

@Composable
private fun StatChip(value: String, label: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A237E),
                fontSize = 22.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                fontSize = 10.sp,
                letterSpacing = 0.5.sp
            )
        }
    }
}

// ── Prescription Card with Colored Accent Bar ─────────────────────────────────

private val accentColors = listOf(
    Color(0xFF3F51B5), // Indigo
    Color(0xFF43A047), // Green
    Color(0xFFFF7043), // Orange
    Color(0xFF9C27B0), // Purple
    Color(0xFF00ACC1), // Teal
)

@Composable
private fun PrescriptionCard(
    prescription: Prescription,
    onViewDetails: () -> Unit,
    onDelete: () -> Unit,
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val isDoctorRx = prescription.isDoctorPrescription
    val accentColor = accentColors[prescription.id.hashCode().and(0x7FFFFFFF) % accentColors.size]

    Card(
        onClick = if (!isDoctorRx) onViewDetails else ({}),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row {
            // Left colored accent bar
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .height(if (isDoctorRx) 180.dp else 160.dp)
                    .background(accentColor)
            )
            Column(modifier = Modifier
                .weight(1f)
                .padding(16.dp)) {

                // Doctor Prescription badge
                if (isDoctorRx) {
                    Box(
                        modifier = Modifier
                            .background(WarningOrange.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MedicalServices, null,
                                tint = WarningOrange, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Doctor Prescription",
                                style = MaterialTheme.typography.labelSmall,
                                color = WarningOrange, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Date row + delete
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CalendarToday, null,
                            tint = accentColor, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = DateUtils.formatDate(prescription.visitDate),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = accentColor
                        )
                    }
                    IconButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.size(34.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(ErrorRed.copy(alpha = 0.08f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Delete, "Delete",
                                tint = ErrorRed.copy(alpha = 0.7f),
                                modifier = Modifier.size(16.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Doctor + Hospital
                if (!prescription.doctorName.isNullOrBlank()) {
                    Text(
                        text = "Dr. ${prescription.doctorName}" +
                                if (!prescription.hospital.isNullOrBlank()) " — ${prescription.hospital}" else "",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1A1A2E),
                        maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                }

                // Diagnosis
                if (!isDoctorRx && !prescription.diagnosis.isNullOrBlank()) {
                    Text(
                        text = "Diagnosis: ${prescription.diagnosis}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                // Tests (doctor prescriptions)
                if (isDoctorRx && prescription.tests.isNotEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Description, null,
                            tint = WarningOrange, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Tests: ${prescription.tests.joinToString(", ")}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }

                // Medication summary
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Medication, null,
                        tint = HealthGreen, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    val medNames = prescription.medications.take(3).mapNotNull { it.medicine.ifBlank { null } }
                    val medText = if (medNames.isEmpty()) "No medications"
                    else medNames.joinToString(", ") +
                            if (prescription.medications.size > 3) " …" else ""
                    Text(medText, style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }

                // Bottom: med count badge + View Details
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(accentColor.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text("${prescription.medications.size} medication(s)",
                            style = MaterialTheme.typography.labelSmall,
                            color = accentColor, fontWeight = FontWeight.SemiBold)
                    }
                    if (!isDoctorRx) {
                        Text("View Details ›",
                            style = MaterialTheme.typography.labelSmall,
                            color = accentColor, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(if (isDoctorRx) "Delete Doctor Prescription?" else "Delete Prescription?") },
            text = {
                Text(
                    if (isDoctorRx) "This will permanently delete this doctor prescription. This action cannot be undone."
                    else "This will permanently delete this prescription and its scanned image. This action cannot be undone."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    onDelete()
                }) { Text("Delete", color = ErrorRed) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }
}

// ── Empty State ───────────────────────────────────────────────────────────────

@Composable
private fun EmptyPrescriptionState(onNavigateToScan: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8EAF6)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Description, null,
                    tint = Color(0xFF3F51B5),
                    modifier = Modifier.size(40.dp),
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text("No Prescriptions Yet",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
            Spacer(modifier = Modifier.height(8.dp))
            Text("Scan a prescription with your camera\nand it will appear here.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(28.dp))
            MediButton(text = "📸  Scan Prescription",
                onClick = onNavigateToScan, modifier = Modifier.width(220.dp))
        }
    }
}
