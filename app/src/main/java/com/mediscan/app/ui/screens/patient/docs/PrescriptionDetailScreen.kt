package com.mediscan.app.ui.screens.patient.docs

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mediscan.app.core.theme.ErrorRed
import com.mediscan.app.core.theme.HealthGreen
import com.mediscan.app.core.theme.MediBlue
import com.mediscan.app.core.theme.TextSecondary
import com.mediscan.app.core.theme.WarningOrange
import com.mediscan.app.core.utils.DateUtils
import com.mediscan.app.core.utils.NetworkResult
import com.mediscan.app.data.model.Medication
import com.mediscan.app.data.model.Prescription
import com.mediscan.app.ui.components.common.MediButton
import kotlinx.coroutines.tasks.await

/**
 * PrescriptionDetailScreen — view + edit a single prescription.
 * Everything is editable EXCEPT the scanned image.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PrescriptionDetailScreen(
    viewModel: PrescriptionDetailViewModel,
    prescriptionId: String,
    onNavigateBack: () -> Unit,
) {
    val prescriptionState by viewModel.prescription.collectAsState()
    val deleteState by viewModel.deleteState.collectAsState()
    val updateState by viewModel.updateState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Load prescription
    LaunchedEffect(prescriptionId) {
        viewModel.loadPrescription(prescriptionId)
    }

    // Navigate back after successful delete
    LaunchedEffect(deleteState) {
        if (deleteState is NetworkResult.Success) {
            onNavigateBack()
        }
    }

    // Handle update result
    LaunchedEffect(updateState) {
        when (updateState) {
            is NetworkResult.Success -> {
                snackbarHostState.showSnackbar("Prescription updated!")
                isEditing = false
                viewModel.resetUpdateState()
            }
            is NetworkResult.Error -> {
                snackbarHostState.showSnackbar("Update failed: ${(updateState as NetworkResult.Error).message}")
                viewModel.resetUpdateState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditing) "Edit Prescription" else "Prescription Details",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (isEditing) isEditing = false else onNavigateBack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                },
                actions = {
                    if (!isEditing) {
                        IconButton(onClick = { isEditing = true }) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Edit, "Edit", tint = Color.White,
                                    modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Delete, "Delete", tint = Color.White,
                                modifier = Modifier.size(18.dp))
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                modifier = Modifier.background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF1A237E), Color(0xFF3F51B5), Color(0xFF5C6BC0))
                    )
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFFF4F6FB)
    ) { innerPadding ->
        when (val state = prescriptionState) {
            is NetworkResult.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator(color = MediBlue) }
            }

            is NetworkResult.Success -> {
                val rx = state.data
                if (isEditing) {
                    EditPrescriptionContent(
                        prescription = rx,
                        isSaving = updateState is NetworkResult.Loading,
                        onSave = { updated -> viewModel.updatePrescription(updated) },
                        onCancel = { isEditing = false },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(horizontal = 16.dp)
                    )
                } else {
                    PrescriptionDetailContent(
                        prescription = rx,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(horizontal = 16.dp)
                    )
                }
            }

            is NetworkResult.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Failed to load prescription")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(state.message, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        Spacer(modifier = Modifier.height(16.dp))
                        MediButton(text = "Retry", onClick = { viewModel.loadPrescription(prescriptionId) })
                    }
                }
            }

            else -> {}
        }
    }

    // Delete dialog
    if (showDeleteDialog) {
        val rx = (prescriptionState as? NetworkResult.Success)?.data
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Prescription?") },
            text = { Text("This will permanently delete this prescription and its scanned image. This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    rx?.let { viewModel.deletePrescription(it) }
                }) { Text("Delete", color = ErrorRed) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }
}

// ═══════════════════════════════════════════════════════════
// VIEW MODE — Detail Content (read-only)
// ═══════════════════════════════════════════════════════════
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PrescriptionDetailContent(
    prescription: Prescription,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        Spacer(modifier = Modifier.height(12.dp))

        SectionCard(title = "Visit Information", icon = Icons.Default.CalendarToday,
            accentColor = Color(0xFF3F51B5)) {
            InfoRow(label = "Date", value = DateUtils.formatDate(prescription.visitDate))
            if (!prescription.doctorName.isNullOrBlank()) {
                InfoRow(label = "Doctor", value = "Dr. ${prescription.doctorName}")
            }
            if (!prescription.hospital.isNullOrBlank()) {
                InfoRow(label = "Hospital", value = prescription.hospital)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (!prescription.diagnosis.isNullOrBlank() || prescription.diagnoses.isNotEmpty()) {
            SectionCard(title = "Diagnosis", icon = Icons.Default.MedicalServices,
                accentColor = Color(0xFF43A047)) {
                val diagText = prescription.diagnosis
                    ?: prescription.diagnoses.joinToString(", ")
                if (diagText.isNotBlank()) {
                    Text(diagText, style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF424242),
                        modifier = Modifier.padding(bottom = 4.dp))
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        if (prescription.medications.isNotEmpty()) {
            SectionCard(title = "Medications (${prescription.medications.size})",
                icon = Icons.Default.Medication, accentColor = Color(0xFFFF9800)) {
                prescription.medications.forEachIndexed { index, med ->
                    MedicationDetailRow(medication = med, index = index + 1)
                    if (index < prescription.medications.lastIndex) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp),
                            color = Color(0xFFEEEEEE))
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        if (prescription.tests.isNotEmpty()) {
            SectionCard(title = "Tests", icon = Icons.Default.Science,
                accentColor = Color(0xFFF44336)) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    prescription.tests.forEach { test -> ChipLabel(text = test, color = Color(0xFF3F51B5)) }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        if (!prescription.imageUrl.isNullOrBlank()) {
            SectionCard(title = "Scanned Image", icon = Icons.Default.Person,
                accentColor = Color(0xFF9C27B0)) {
                PrescriptionImageLoader(imageUrl = prescription.imageUrl!!)
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// ═══════════════════════════════════════════════════════════
// EDIT MODE — Editable form (image stays read-only)
// ═══════════════════════════════════════════════════════════
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EditPrescriptionContent(
    prescription: Prescription,
    isSaving: Boolean,
    onSave: (Prescription) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // ── Mutable states initialized from prescription ──
    var dateText by remember { mutableStateOf(DateUtils.formatDate(prescription.visitDate)) }
    var doctorName by remember { mutableStateOf(prescription.doctorName ?: "") }
    var hospital by remember { mutableStateOf(prescription.hospital ?: "") }
    var diagnosis by remember {
        mutableStateOf(
            if (!prescription.diagnosis.isNullOrBlank()) prescription.diagnosis
            else prescription.diagnoses.joinToString(",")
        )
    }
    val testsList = remember { mutableStateListOf<String>().apply { addAll(prescription.tests) } }
    val medicationsList = remember {
        mutableStateListOf<MutableMedication>().apply {
            addAll(prescription.medications.map { it.toMutable() })
        }
    }
    var newTest by remember { mutableStateOf("") }

    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        Spacer(modifier = Modifier.height(8.dp))

        // ── Visit Info ──
        SectionCard(title = "Visit Information", icon = Icons.Default.CalendarToday,
            accentColor = Color(0xFF3F51B5)) {
            EditField(label = "Date", value = dateText, onValueChange = { dateText = it },
                placeholder = "21 Feb 2026")
            EditField(label = "Doctor Name", value = doctorName, onValueChange = { doctorName = it })
            EditField(label = "Hospital", value = hospital, onValueChange = { hospital = it })
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ── Diagnosis ──
        SectionCard(title = "Diagnosis", icon = Icons.Default.MedicalServices,
            accentColor = Color(0xFF43A047)) {
            Text(
                text = "Separate with commas (e.g. fever,cold,headache)",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
            )
            Spacer(modifier = Modifier.height(4.dp))
            EditField(
                label = "Diagnosis",
                value = diagnosis,
                onValueChange = { diagnosis = it },
                placeholder = "e.g. fever,cold,headache"
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ── Medications ──
        SectionCard(title = "Medications (${medicationsList.size})",
            icon = Icons.Default.Medication, accentColor = Color(0xFFFF9800)) {
            medicationsList.forEachIndexed { index, med ->
                EditMedicationRow(
                    medication = med,
                    index = index + 1,
                    onRemove = { medicationsList.removeAt(index) }
                )
                if (index < medicationsList.lastIndex) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = {
                medicationsList.add(MutableMedication())
            }) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Medication")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ── Tests ──
        SectionCard(title = "Tests", icon = Icons.Default.Science,
            accentColor = Color(0xFFF44336)) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                testsList.forEachIndexed { i, test ->
                    EditableChip(text = test, color = MediBlue, onRemove = { testsList.removeAt(i) })
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = newTest,
                    onValueChange = { newTest = it },
                    placeholder = { Text("Add test", style = MaterialTheme.typography.bodySmall) },
                    modifier = Modifier.weight(1f).height(48.dp),
                    textStyle = MaterialTheme.typography.bodySmall,
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MediBlue,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (newTest.isNotBlank()) {
                            testsList.add(newTest.trim())
                            newTest = ""
                        }
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .background(MediBlue.copy(alpha = 0.1f), CircleShape)
                ) {
                    Icon(Icons.Default.Add, "Add", tint = MediBlue, modifier = Modifier.size(18.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ── Scanned Image (read-only even in edit mode) ──
        if (!prescription.imageUrl.isNullOrBlank()) {
            SectionCard(title = "Scanned Image (read-only)", icon = Icons.Default.Person,
                accentColor = Color(0xFF9C27B0)) {
                PrescriptionImageLoader(imageUrl = prescription.imageUrl!!)
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // ── Save / Cancel buttons ──
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MediButton(
                text = "Cancel",
                onClick = onCancel,
                modifier = Modifier.weight(1f),
            )
            MediButton(
                text = "Save Changes",
                isLoading = isSaving,
                onClick = {
                    val parsedDate = DateUtils.parseDate(dateText) ?: prescription.visitDate
                    val diagList = diagnosis.split(",").map { it.trim() }.filter { it.isNotBlank() }
                    val updated = prescription.copy(
                        visitDate = parsedDate,
                        doctorName = doctorName.ifBlank { null },
                        hospital = hospital.ifBlank { null },
                        diagnosis = diagList.joinToString(","),
                        diagnoses = diagList,
                        tests = testsList.toList(),
                        medications = medicationsList.map { it.toMedication() },
                    )
                    onSave(updated)
                },
                modifier = Modifier.weight(1f),
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// ═══════════════════════════════════════════════════════════
// Mutable Medication helper (for editing)
// ═══════════════════════════════════════════════════════════
private class MutableMedication(
    medicine: String = "",
    doseStrength: String = "",
    schedule: String = "",
    duration: String = "",
) {
    var medicine by mutableStateOf(medicine)
    var doseStrength by mutableStateOf(doseStrength)
    var schedule by mutableStateOf(schedule)
    var duration by mutableStateOf(duration)

    fun toMedication() = Medication(
        medicine = medicine,
        doseStrength = doseStrength.ifBlank { null },
        schedule = schedule.ifBlank { null },
        duration = duration.ifBlank { null },
    )
}

private fun Medication.toMutable() = MutableMedication(
    medicine = medicine,
    doseStrength = doseStrength ?: "",
    schedule = schedule ?: "",
    duration = duration ?: "",
)

// ═══════════════════════════════════════════════════════════
// Edit Medication Row
// ═══════════════════════════════════════════════════════════
@Composable
private fun EditMedicationRow(
    medication: MutableMedication,
    index: Int,
    onRemove: () -> Unit,
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Medicine $index", style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold)
            IconButton(onClick = onRemove, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Default.Close, "Remove", tint = ErrorRed, modifier = Modifier.size(16.dp))
            }
        }
        EditField(label = "Name", value = medication.medicine,
            onValueChange = { medication.medicine = it })
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            EditField(label = "Dose", value = medication.doseStrength,
                onValueChange = { medication.doseStrength = it }, modifier = Modifier.weight(1f))
            EditField(label = "Schedule", value = medication.schedule,
                onValueChange = { medication.schedule = it }, modifier = Modifier.weight(1f))
        }
        EditField(label = "Duration", value = medication.duration,
            onValueChange = { medication.duration = it })
    }
}

// ═══════════════════════════════════════════════════════════
// Shared composables
// ═══════════════════════════════════════════════════════════

@Composable
private fun EditField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
) {
    Column(modifier = modifier.padding(vertical = 3.dp)) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = if (placeholder.isNotBlank()) {{ Text(placeholder, style = MaterialTheme.typography.bodySmall) }} else null,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            textStyle = MaterialTheme.typography.bodySmall,
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MediBlue,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            )
        )
    }
}

@Composable
private fun SectionCard(
    title: String,
    icon: ImageVector,
    accentColor: Color = MediBlue,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            // Left accent bar
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .fillMaxHeight()
                    .background(accentColor)
            )
            Column(modifier = Modifier
                .weight(1f)
                .padding(16.dp)) {
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
                    Text(title, style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
                }
                Spacer(modifier = Modifier.height(14.dp))
                content()
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF9E9E9E), fontSize = 13.sp)
        Text(value, style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold, color = Color(0xFF424242))
    }
}

@Composable
private fun MedicationDetailRow(medication: Medication, index: Int) {
    Row(verticalAlignment = Alignment.Top) {
        // Numbered badge
        Box(
            modifier = Modifier
                .size(26.dp)
                .background(Color(0xFFFF9800).copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("$index", fontSize = 12.sp, fontWeight = FontWeight.Bold,
                color = Color(0xFFFF9800))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = medication.medicine.ifBlank { "Unknown" },
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold, color = Color(0xFF212121)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MedField("DOSE", medication.doseStrength, Color(0xFF3F51B5))
                MedField("SCHEDULE", medication.schedule, Color(0xFF43A047))
                MedField("DURATION", medication.duration, Color(0xFFFF9800))
            }
        }
    }
}

@Composable
private fun MedField(label: String, value: String?, accentColor: Color = TextSecondary) {
    if (!value.isNullOrBlank()) {
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall,
                color = accentColor.copy(alpha = 0.7f), fontSize = 10.sp,
                fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF424242), fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun ChipLabel(text: String, color: Color) {
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.1f), RoundedCornerShape(20.dp))
            .padding(horizontal = 14.dp, vertical = 7.dp)
    ) {
        Text(text, style = MaterialTheme.typography.labelMedium, color = color,
            fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
    }
}

@Composable
private fun EditableChip(
    text: String,
    color: Color,
    onRemove: () -> Unit,
) {
    Row(
        modifier = Modifier
            .background(color.copy(alpha = 0.12f), RoundedCornerShape(20.dp))
            .padding(start = 12.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text, style = MaterialTheme.typography.labelMedium, color = color, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.width(2.dp))
        Icon(
            Icons.Default.Close, "Remove",
            tint = color,
            modifier = Modifier
                .size(18.dp)
                .clip(CircleShape)
                .clickable { onRemove() }
                .padding(2.dp)
        )
    }
}

// ═══════════════════════════════════════════════════════════
// Reusable prescription image loader with proper states
// Uses Firebase Storage SDK to download authenticated images
// Caches images in memory for instant re-display
// ═══════════════════════════════════════════════════════════

/** Simple in-memory LRU cache for prescription images (max 8 images) */
private object PrescriptionImageCache {
    private val cache = android.util.LruCache<String, android.graphics.Bitmap>(8)

    fun get(url: String): android.graphics.Bitmap? = cache.get(url)
    fun put(url: String, bitmap: android.graphics.Bitmap) { cache.put(url, bitmap) }
}

@Composable
private fun PrescriptionImageLoader(imageUrl: String) {
    // Check cache first for instant display
    var imageBitmap by remember(imageUrl) {
        mutableStateOf(PrescriptionImageCache.get(imageUrl))
    }
    var isLoading by remember(imageUrl) { mutableStateOf(imageBitmap == null) }
    var errorMessage by remember(imageUrl) { mutableStateOf<String?>(null) }
    var retryKey by remember { mutableStateOf(0) }

    // Only fetch if not already cached
    if (imageBitmap == null) {
        LaunchedEffect(imageUrl, retryKey) {
            isLoading = true
            errorMessage = null
            try {
                val storage = com.google.firebase.storage.FirebaseStorage.getInstance()
                val ref = try {
                    storage.getReferenceFromUrl(imageUrl)
                } catch (_: Exception) {
                    null
                }

                if (ref != null) {
                    val bytes = ref.getBytes(10L * 1024 * 1024).await()
                    val bmp = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    if (bmp != null) {
                        PrescriptionImageCache.put(imageUrl, bmp)
                        imageBitmap = bmp
                        isLoading = false
                    } else {
                        errorMessage = "Could not decode image"
                        isLoading = false
                    }
                } else {
                    val bmp = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                        try {
                            val url = java.net.URL(imageUrl)
                            val conn = url.openConnection()
                            conn.connectTimeout = 15_000
                            conn.readTimeout = 15_000
                            android.graphics.BitmapFactory.decodeStream(conn.getInputStream())
                        } catch (_: Exception) {
                            null
                        }
                    }
                    if (bmp != null) {
                        PrescriptionImageCache.put(imageUrl, bmp)
                        imageBitmap = bmp
                        isLoading = false
                    } else {
                        errorMessage = "Could not load image"
                        isLoading = false
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("PrescriptionImage", "Failed to load: $imageUrl", e)
                errorMessage = e.message ?: "Unknown error"
                isLoading = false
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF5F5F5)),
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading -> {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFF9C27B0),
                        modifier = Modifier.size(28.dp),
                        strokeWidth = 2.5.dp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Loading image…",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }

            errorMessage != null -> {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.BrokenImage,
                        contentDescription = null,
                        tint = Color(0xFF9E9E9E),
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Could not load image",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        errorMessage ?: "",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFBDBDBD),
                        maxLines = 2
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    MediButton(
                        text = "Retry",
                        onClick = { retryKey++ },
                    )
                }
            }

            imageBitmap != null -> {
                androidx.compose.foundation.Image(
                    bitmap = imageBitmap!!.asImageBitmap(),
                    contentDescription = "Scanned prescription",
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.FillWidth
                )
            }
        }
    }
}
