package com.mediscan.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.zIndex
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mediscan.app.core.theme.ErrorRed
import com.mediscan.app.core.theme.HealthGreen
import com.mediscan.app.core.theme.MediBlue
import com.mediscan.app.core.theme.TextSecondary
import com.mediscan.app.core.theme.WarningOrange
import com.mediscan.app.core.utils.DateUtils
import com.mediscan.app.core.constants.MedicalSuggestions
import com.mediscan.app.data.model.ExtractionResult
import com.mediscan.app.data.model.Medication
import com.mediscan.app.ui.components.common.MediButton
import com.mediscan.app.ui.components.common.MediOutlinedButton

/**
 * Editable medication state for the bottom sheet.
 */
data class EditableMedication(
    var medicine: String = "",
    var doseStrength: String = "",
    var schedule: String = "",
    var duration: String = "",
)

/**
 * ExtractionResultSheet — modal bottom sheet matching the wireframe:
 *
 *   "Prescription Information" header
 *   ┌─────────────────────────────────────────────────┐
 *   │ Medicine Name 1 │ Doses 1 │ Schedule 1 │ Dur 1  │ ⊕
 *   │ Medicine Name 2 │ Doses 2 │ Schedule 2 │ Dur 2  │ ⊕
 *   │ ...                                              │
 *   ├─────────────────────────────────────────────────┤
 *   │ Diagnosis 1  │  Diagnosis 2  │ ⊕                 │
 *   ├─────────────────────────────────────────────────┤
 *   │ Date                                             │
 *   ├─────────────────────────────────────────────────┤
 *   │ Test 1 │ Test 2 │ Test 3 │ ⊕                    │
 *   ├─────────────────────────────────────────────────┤
 *   │ Hospital Name                                    │
 *   │ Doctor Name                                      │
 *   ├─────────────────────────────────────────────────┤
 *   │                    Cancel  │  Save               │
 *   └─────────────────────────────────────────────────┘
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ExtractionResultSheet(
    result: ExtractionResult,
    isSaving: Boolean,
    onSave: (
        doctorName: String,
        hospital: String,
        visitDate: Long,
        diagnosis: String,
        diagnoses: List<String>,
        tests: List<String>,
        medications: List<Medication>,
    ) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // ── Editable medication rows ──
    val medications = remember {
        mutableStateListOf<EditableMedication>().apply {
            addAll(result.medications.map { extracted ->
                EditableMedication(
                    medicine = extracted.medicine ?: "",
                    doseStrength = extracted.doseStrength ?: "",
                    schedule = extracted.schedule ?: "",
                    duration = extracted.duration ?: "",
                )
            })
            if (isEmpty()) add(EditableMedication())
        }
    }

    // ── Editable diagnosis (comma-separated) ──
    val diagnosisText = rememberSaveable {
        mutableStateOf(
            result.prescriptionInfo?.diagnoses
                ?.filter { it.isNotBlank() }
                ?.joinToString(",") ?: ""
        )
    }

    // ── Editable test chips ──
    val tests = remember {
        mutableStateListOf<String>().apply {
            val extracted = result.prescriptionInfo?.tests
            if (!extracted.isNullOrEmpty()) addAll(extracted)
            else add("") // one blank chip
        }
    }

    // ── Single fields ──
    val visitDate = rememberSaveable { mutableStateOf(result.prescriptionInfo?.date ?: "") }
    val hospital = rememberSaveable { mutableStateOf(result.doctor?.hospital ?: "") }
    val doctorName = rememberSaveable { mutableStateOf(result.doctor?.name ?: "") }

    // ── Validation state ──
    val showErrors = rememberSaveable { mutableStateOf(false) }
    val hasDiagnosis = diagnosisText.value.trim().isNotBlank()
    val hasValidDate = DateUtils.isValidDate(visitDate.value)
    val hasDateText = visitDate.value.trim().isNotBlank()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFFF4F6FB),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    ) {
        val focusManager = LocalFocusManager.current

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { focusManager.clearFocus() }
                .verticalScroll(rememberScrollState())
                .padding(bottom = 16.dp)
        ) {
            // ══════════════ GRADIENT HEADER ══════════════
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF1A237E), Color(0xFF3F51B5), Color(0xFF5C6BC0))
                        ),
                        RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                    )
                    .padding(vertical = 20.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.MedicalServices,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.85f),
                        modifier = Modifier.size(28.dp),
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Prescription Information",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                    Text(
                        text = "Review & edit extracted details",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f),
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ══════════════ MEDICATIONS SECTION ══════════════
            SheetSectionCard(
                title = "Medications",
                icon = Icons.Default.Medication,
                accentColor = Color(0xFF3F51B5),
                modifier = Modifier.padding(horizontal = 16.dp),
            ) {
                medications.forEachIndexed { index, med ->
                    if (index > 0) Spacer(modifier = Modifier.height(10.dp))
                    MedicationRow(
                        index = index + 1,
                        medication = med,
                        onMedicineChange = { medications[index] = med.copy(medicine = it) },
                        onDoseChange = { medications[index] = med.copy(doseStrength = it) },
                        onScheduleChange = { medications[index] = med.copy(schedule = it) },
                        onDurationChange = { medications[index] = med.copy(duration = it) },
                        onAdd = { medications.add(index + 1, EditableMedication()) },
                        onRemove = if (medications.size > 1) {{ medications.removeAt(index) }} else null,
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ══════════════ DIAGNOSIS SECTION ══════════════
            SheetSectionCard(
                title = "Diagnosis",
                icon = Icons.Default.Science,
                accentColor = ErrorRed,
                isRequired = true,
                modifier = Modifier.padding(horizontal = 16.dp),
            ) {
                Text(
                    text = "Type to search — suggestions will appear. Separate multiple with commas.",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                AutocompleteTextField(
                    value = diagnosisText.value,
                    onValueChange = { diagnosisText.value = it },
                    placeholder = "e.g. fever, cold, headache",
                    suggestions = MedicalSuggestions.diagnoses,
                    modifier = Modifier.fillMaxWidth(),
                )
                if (showErrors.value && !hasDiagnosis) {
                    Text(
                        text = "⚠ Please enter at least one diagnosis",
                        style = MaterialTheme.typography.bodySmall,
                        color = ErrorRed,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ══════════════ DATE SECTION ══════════════
            SheetSectionCard(
                title = "Visit Date",
                icon = Icons.Default.CalendarMonth,
                accentColor = WarningOrange,
                isRequired = true,
                modifier = Modifier.padding(horizontal = 16.dp),
            ) {
                Text(
                    text = "Format: 21 FEB 2026  or  22-04-2025  or  2025-04-22",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                StyledTextField(
                    value = visitDate.value,
                    onValueChange = { visitDate.value = it },
                    placeholder = "e.g. 21 FEB 2026",
                    modifier = Modifier.fillMaxWidth()
                )
                if (showErrors.value && !hasDateText) {
                    Text(
                        text = "⚠ Please enter the visit date",
                        style = MaterialTheme.typography.bodySmall,
                        color = ErrorRed,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                } else if (showErrors.value && hasDateText && !hasValidDate) {
                    Text(
                        text = "⚠ Could not recognize date format. Use: 21 FEB 2026 or 22-04-2025",
                        style = MaterialTheme.typography.bodySmall,
                        color = ErrorRed,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ══════════════ TESTS SECTION ══════════════
            SheetSectionCard(
                title = "Tests",
                icon = Icons.Default.Science,
                accentColor = HealthGreen,
                modifier = Modifier.padding(horizontal = 16.dp),
            ) {
                Text(
                    text = "Type to search — suggestions will appear",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                AutocompleteChipSection(
                    label = "Test",
                    items = tests,
                    suggestions = MedicalSuggestions.medicalTests,
                    onItemChange = { i, value -> tests[i] = value },
                    onAdd = { tests.add("") },
                    onRemove = { i -> if (tests.size > 1) tests.removeAt(i) },
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ══════════════ HOSPITAL & DOCTOR SECTION ══════════════
            SheetSectionCard(
                title = "Doctor & Hospital",
                icon = Icons.Default.LocalHospital,
                accentColor = MediBlue,
                modifier = Modifier.padding(horizontal = 16.dp),
            ) {
                FieldWithIcon(
                    icon = Icons.Default.LocalHospital,
                    iconBg = HealthGreen,
                    label = "Hospital Name",
                )
                Spacer(modifier = Modifier.height(4.dp))
                StyledTextField(
                    value = hospital.value,
                    onValueChange = { hospital.value = it },
                    placeholder = "Hospital / Clinic",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                FieldWithIcon(
                    icon = Icons.Default.Person,
                    iconBg = Color(0xFF3F51B5),
                    label = "Doctor Name",
                )
                Spacer(modifier = Modifier.height(4.dp))
                StyledTextField(
                    value = doctorName.value,
                    onValueChange = { doctorName.value = it },
                    placeholder = "Dr.",
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ══════════════ ACTION BUTTONS ══════════════
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End),
            ) {
                MediOutlinedButton(
                    text = "Cancel",
                    onClick = onDismiss,
                    modifier = Modifier.width(130.dp)
                )
                // Save button with gradient
                Box(
                    modifier = Modifier
                        .width(130.dp)
                        .height(44.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF1A237E), Color(0xFF3F51B5))
                            ),
                            RoundedCornerShape(12.dp),
                        )
                        .clip(RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    TextButton(
                        onClick = {
                            val diagList = diagnosisText.value
                                .split(",")
                                .map { it.trim() }
                                .filter { it.isNotBlank() }
                            val dateValid = DateUtils.isValidDate(visitDate.value)

                            if (diagList.isEmpty() || !dateValid) {
                                showErrors.value = true
                                return@TextButton
                            }

                            val medList = medications.filter { it.medicine.isNotBlank() }.map {
                                Medication(
                                    medicine = it.medicine.trim(),
                                    doseStrength = it.doseStrength.trim().ifBlank { null },
                                    schedule = it.schedule.trim().ifBlank { null },
                                    duration = it.duration.trim().ifBlank { null },
                                )
                            }
                            val testList = tests.map { it.trim() }.filter { it.isNotBlank() }
                            val parsedDate = DateUtils.parseDate(visitDate.value) ?: System.currentTimeMillis()
                            onSave(
                                doctorName.value.trim(),
                                hospital.value.trim(),
                                parsedDate,
                                diagList.joinToString(","),
                                diagList,
                                testList,
                                medList,
                            )
                        },
                        modifier = Modifier.fillMaxSize(),
                        enabled = !isSaving,
                    ) {
                        if (isSaving) {
                            androidx.compose.material3.CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp,
                            )
                        } else {
                            Text(
                                "Save",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ════════════════════════════════════════════════════════
// SheetSectionCard — styled section container with accent bar
// ════════════════════════════════════════════════════════
@Composable
private fun SheetSectionCard(
    title: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    isRequired: Boolean = false,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            // Accent bar
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(accentColor, RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
            )
            Column(modifier = Modifier.padding(14.dp)) {
                // Section header
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .background(accentColor.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(icon, null, modifier = Modifier.size(16.dp), tint = accentColor)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A237E),
                    )
                    if (isRequired) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "*required",
                            style = MaterialTheme.typography.labelSmall,
                            color = ErrorRed,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                content()
            }
        }
    }
}

// ════════════════════════════════════════════════════════
// FieldWithIcon — label row with colored icon
// ════════════════════════════════════════════════════════
@Composable
private fun FieldWithIcon(
    icon: ImageVector,
    iconBg: Color,
    label: String,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .background(iconBg.copy(alpha = 0.12f), RoundedCornerShape(7.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, modifier = Modifier.size(14.dp), tint = iconBg)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF37474F),
        )
    }
}

// ════════════════════════════════════════════════════════
// Medication Row — enhanced styling
// ════════════════════════════════════════════════════════
@Composable
private fun MedicationRow(
    index: Int,
    medication: EditableMedication,
    onMedicineChange: (String) -> Unit,
    onDoseChange: (String) -> Unit,
    onScheduleChange: (String) -> Unit,
    onDurationChange: (String) -> Unit,
    onAdd: () -> Unit,
    onRemove: (() -> Unit)?,
) {
    val scrollState = rememberScrollState()

    // Subtle background for each medication row
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF8F9FC), RoundedCornerShape(10.dp))
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Medicine Name
            StyledLabeledField(
                label = "Medicine $index",
                value = medication.medicine,
                onValueChange = onMedicineChange,
                fieldWidth = 150.dp,
                accentColor = Color(0xFF3F51B5),
            )
            // Doses
            StyledLabeledField(
                label = "Dose $index",
                value = medication.doseStrength,
                onValueChange = onDoseChange,
                fieldWidth = 80.dp,
                accentColor = WarningOrange,
            )
            // Schedule (with dose suggestions)
            DoseScheduleField(
                index = index,
                value = medication.schedule,
                onValueChange = onScheduleChange,
                fieldWidth = 120.dp,
            )
            // Duration
            StyledLabeledField(
                label = "Duration $index",
                value = medication.duration,
                onValueChange = onDurationChange,
                fieldWidth = 90.dp,
                accentColor = MediBlue,
            )

            // Add / Remove buttons
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                OutlinedIconButton(
                    onClick = onAdd,
                    modifier = Modifier.size(30.dp),
                    shape = CircleShape,
                    border = BorderStroke(1.5.dp, Color(0xFF3F51B5))
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(15.dp), tint = Color(0xFF3F51B5))
                }
                if (onRemove != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    IconButton(
                        onClick = onRemove,
                        modifier = Modifier.size(22.dp),
                        colors = IconButtonDefaults.iconButtonColors(contentColor = ErrorRed)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Remove", modifier = Modifier.size(13.dp))
                    }
                }
            }
        }
    }
}

/**
 * Styled label above a text field with a tiny colored accent dot.
 */
@Composable
private fun StyledLabeledField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    fieldWidth: Dp,
    accentColor: Color,
) {
    Column(modifier = Modifier.width(fieldWidth)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(accentColor, CircleShape)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF37474F),
                maxLines = 1,
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        StyledTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = "",
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// ════════════════════════════════════════════════════════
// AutocompleteTextField — Google-style suggestion dropdown
// Shows filtered matches from a suggestion list as user types.
// For comma-separated fields (diagnosis), it matches the
// last segment being typed.
// ════════════════════════════════════════════════════════
@Composable
private fun AutocompleteTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    suggestions: List<String>,
    modifier: Modifier = Modifier,
) {
    var isFocused by remember { mutableStateOf(false) }
    // showSuggestions is true only while the field is actively focused
    var showSuggestions by remember { mutableStateOf(false) }

    // Get the last segment after the last comma for matching
    val currentSegment = remember(value) {
        val parts = value.split(",")
        parts.lastOrNull()?.trim() ?: ""
    }

    val filtered by remember(currentSegment, showSuggestions) {
        derivedStateOf {
            if (!showSuggestions || currentSegment.length < 1) emptyList()
            else suggestions.filter {
                it.contains(currentSegment, ignoreCase = true)
            }.take(6)
        }
    }

    Box(modifier = modifier) {
        Column {
            // The text field
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = TextStyle(
                    fontSize = 14.sp,
                    color = Color(0xFF1A237E),
                    fontWeight = FontWeight.Medium,
                ),
                cursorBrush = SolidColor(Color(0xFF3F51B5)),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .background(Color(0xFFF5F6FA), RoundedCornerShape(10.dp))
                    .border(
                        1.dp,
                        if (isFocused) Color(0xFF3F51B5) else Color(0xFFCFD8DC),
                        RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 4.dp)
                    .onFocusChanged {
                        isFocused = it.isFocused
                        showSuggestions = it.isFocused
                    },
                decorationBox = { innerTextField ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = TextSecondary,
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(modifier = Modifier.weight(1f)) {
                            if (value.isEmpty()) {
                                Text(
                                    text = placeholder,
                                    style = TextStyle(fontSize = 13.sp, color = TextSecondary)
                                )
                            }
                            innerTextField()
                        }
                    }
                }
            )

            // Suggestion dropdown — only visible while field is focused
            if (filtered.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp)
                        .shadow(4.dp, RoundedCornerShape(10.dp))
                        .zIndex(10f),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 180.dp)
                    ) {
                        filtered.forEach { suggestion ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        // Replace the last segment with the selected suggestion
                                        val parts = value.split(",").toMutableList()
                                        if (parts.isNotEmpty()) {
                                            parts[parts.lastIndex] =
                                                (if (parts.lastIndex > 0) " " else "") + suggestion
                                        }
                                        onValueChange(parts.joinToString(","))
                                        showSuggestions = false
                                    }
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(Color(0xFF3F51B5), CircleShape)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = suggestion,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF37474F),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════════
// DoseScheduleField — tap to show schedule presets as a
// scrollable stack, user picks one or types custom value.
// ════════════════════════════════════════════════════════
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DoseScheduleField(
    index: Int,
    value: String,
    onValueChange: (String) -> Unit,
    fieldWidth: Dp,
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.width(fieldWidth)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(HealthGreen, CircleShape)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Schedule $index",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF37474F),
                maxLines = 1,
            )
        }
        Spacer(modifier = Modifier.height(4.dp))

        Box {
            // Editable text field with dropdown arrow
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = TextStyle(
                    fontSize = 14.sp,
                    color = Color(0xFF1A237E),
                    fontWeight = FontWeight.Medium,
                ),
                cursorBrush = SolidColor(Color(0xFF3F51B5)),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .background(Color(0xFFF5F6FA), RoundedCornerShape(10.dp))
                    .border(1.dp, Color(0xFFCFD8DC), RoundedCornerShape(10.dp))
                    .padding(start = 10.dp, end = 4.dp, top = 4.dp, bottom = 4.dp)
                    .onFocusChanged { expanded = it.isFocused },
                decorationBox = { innerTextField ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            if (value.isEmpty()) {
                                Text(
                                    text = "e.g. 1+0+1",
                                    style = TextStyle(fontSize = 12.sp, color = TextSecondary)
                                )
                            }
                            innerTextField()
                        }
                        IconButton(
                            onClick = { expanded = !expanded },
                            modifier = Modifier.size(24.dp),
                        ) {
                            Icon(
                                Icons.Default.KeyboardArrowDown,
                                contentDescription = "Show schedules",
                                modifier = Modifier.size(18.dp),
                                tint = Color(0xFF3F51B5),
                            )
                        }
                    }
                }
            )

            // Dropdown with schedule presets — chip grid
            if (expanded) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 44.dp)
                        .shadow(6.dp, RoundedCornerShape(10.dp))
                        .zIndex(20f),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                ) {
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        MedicalSuggestions.doseSchedules.forEach { (schedule, _) ->
                            val isSelected = value == schedule
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (isSelected) HealthGreen.copy(alpha = 0.18f)
                                        else Color(0xFFF0F2F8)
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) HealthGreen else Color(0xFFCFD8DC),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable {
                                        onValueChange(schedule)
                                        expanded = false
                                    }
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                            ) {
                                Text(
                                    text = schedule,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) HealthGreen else Color(0xFF37474F),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════════
// AutocompleteChipSection — Test chips with autocomplete
// Each chip has its own autocomplete dropdown for tests.
// ════════════════════════════════════════════════════════
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AutocompleteChipSection(
    label: String,
    items: List<String>,
    suggestions: List<String>,
    onItemChange: (Int, String) -> Unit,
    onAdd: () -> Unit,
    onRemove: (Int) -> Unit,
) {
    // Track which chip index has focus for showing suggestions
    var focusedIndex by remember { mutableStateOf(-1) }

    Column(modifier = Modifier.fillMaxWidth()) {
        items.forEachIndexed { index, value ->
            if (index > 0) Spacer(modifier = Modifier.height(8.dp))

            val filtered by remember(value, focusedIndex) {
                derivedStateOf {
                    if (focusedIndex != index || value.trim().isEmpty()) emptyList()
                    else suggestions.filter {
                        it.contains(value.trim(), ignoreCase = true)
                    }.take(5)
                }
            }

            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF0F2F8), RoundedCornerShape(10.dp))
                        .padding(start = 8.dp, end = 4.dp, top = 4.dp, bottom = 4.dp)
                ) {
                    // Label chip
                    Box(
                        modifier = Modifier
                            .background(HealthGreen.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "$label ${index + 1}",
                            style = MaterialTheme.typography.labelSmall,
                            color = HealthGreen,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))

                    // Text field with search icon
                    BasicTextField(
                        value = value,
                        onValueChange = { onItemChange(index, it) },
                        textStyle = TextStyle(
                            fontSize = 14.sp,
                            color = Color(0xFF1A237E),
                            fontWeight = FontWeight.Medium,
                        ),
                        cursorBrush = SolidColor(Color(0xFF3F51B5)),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .background(Color.White, RoundedCornerShape(8.dp))
                            .border(1.dp, Color(0xFFCFD8DC), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                            .onFocusChanged {
                                focusedIndex = if (it.isFocused) index else -1
                            },
                        decorationBox = { innerTextField ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = TextSecondary,
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Box(modifier = Modifier.weight(1f)) {
                                    if (value.isEmpty()) {
                                        Text(
                                            text = "Search tests...",
                                            style = TextStyle(fontSize = 12.sp, color = TextSecondary)
                                        )
                                    }
                                    innerTextField()
                                }
                            }
                        }
                    )

                    if (items.size > 1) {
                        IconButton(
                            onClick = { onRemove(index) },
                            modifier = Modifier.size(24.dp),
                            colors = IconButtonDefaults.iconButtonColors(contentColor = ErrorRed)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Remove",
                                modifier = Modifier.size(14.dp))
                        }
                    }
                }

                // Autocomplete dropdown
                if (filtered.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp, end = 8.dp, top = 2.dp)
                            .zIndex(10f),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 150.dp)
                        ) {
                            filtered.forEach { suggestion ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onItemChange(index, suggestion)
                                            focusedIndex = -1
                                        }
                                        .padding(horizontal = 14.dp, vertical = 9.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .background(HealthGreen, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = suggestion,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFF37474F),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ⊕ Add button
        OutlinedIconButton(
            onClick = onAdd,
            modifier = Modifier.size(36.dp),
            shape = CircleShape,
            border = BorderStroke(1.5.dp, HealthGreen)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add $label", modifier = Modifier.size(18.dp), tint = HealthGreen)
        }
    }
}

// ════════════════════════════════════════════════════════
// StyledTextField — modern text field with soft border
// ════════════════════════════════════════════════════════
@Composable
private fun StyledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = TextStyle(
            fontSize = 14.sp,
            color = Color(0xFF1A237E),
            fontWeight = FontWeight.Medium,
        ),
        cursorBrush = SolidColor(Color(0xFF3F51B5)),
        singleLine = true,
        modifier = modifier
            .height(42.dp)
            .background(Color(0xFFF5F6FA), RoundedCornerShape(10.dp))
            .border(1.dp, Color(0xFFCFD8DC), RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 4.dp),
        decorationBox = { innerTextField ->
            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = TextStyle(fontSize = 13.sp, color = TextSecondary)
                    )
                }
                innerTextField()
            }
        }
    )
}
