package com.mediscan.app.ui.screens.doctor.appointments

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mediscan.app.core.theme.ErrorRed
import com.mediscan.app.core.theme.HealthGreen
import com.mediscan.app.core.theme.MediBlue
import com.mediscan.app.core.theme.TextSecondary
import com.mediscan.app.core.theme.WarningOrange
import com.mediscan.app.core.utils.NetworkResult
import com.mediscan.app.data.model.Appointment
import com.mediscan.app.data.model.DoctorOrder
import com.mediscan.app.data.model.User
import com.mediscan.app.ui.components.common.MediButton
import com.mediscan.app.ui.viewmodel.DoctorViewModel
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * A single mutable order row for the doctor to fill out.
 */
data class MutableOrderRow(
    var medicine: String = "",
    var test: String = "",
    var doseStrength: String = "",
    var doseSchedule: String = "",
    var notes: String = "",
)

/**
 * PatientDetailSheet — shown when doctor taps a confirmed appointment.
 * Contains: patient info, "View Records" button, prescription form, Submit.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientDetailSheet(
    appointment: Appointment,
    viewModel: DoctorViewModel,
    onDismiss: () -> Unit,
    onViewRecords: (patientId: String) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val patientProfileState by viewModel.patientProfile.collectAsState()
    val completeState by viewModel.completeWithOrdersState.collectAsState()

    // Order rows state
    val orders = remember { mutableStateListOf(MutableOrderRow()) }
    var showForm by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = {
            viewModel.resetPatientProfile()
            viewModel.resetCompleteWithOrdersState()
            onDismiss()
        },
        sheetState = sheetState,
        containerColor = Color(0xFFF4F6FB),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            // ═══════════════════════════════════════
            // Title
            // ═══════════════════════════════════════
            Text(
                "Patient Details",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A2E),
            )
            Spacer(modifier = Modifier.height(16.dp))

            // ═══════════════════════════════════════
            // Patient Info Card
            // ═══════════════════════════════════════
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Name + avatar row
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Profile image or initials fallback
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(Color(0xFF3F51B5), Color(0xFF5C6BC0))
                                    )
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (!appointment.patientProfileImageUrl.isNullOrBlank()) {
                                coil.compose.AsyncImage(
                                    model = appointment.patientProfileImageUrl,
                                    contentDescription = "Patient photo",
                                    modifier = Modifier.size(52.dp).clip(CircleShape),
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                )
                            } else {
                                val initials = appointment.patientName
                                    .split(" ").take(2)
                                    .mapNotNull { it.firstOrNull()?.uppercase() }
                                    .joinToString("").ifEmpty { "P" }
                                Text(initials, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                appointment.patientName.ifBlank { "Patient" },
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A1A2E),
                            )
                            if (!appointment.complaint.isNullOrBlank()) {
                                Text(
                                    "Disease / Reason: ${appointment.complaint}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF9E9E9E),
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color(0xFFEEEEEE))
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    // Health stats from Firestore
                    when (patientProfileState) {
                        is NetworkResult.Loading -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color(0xFF3F51B5))
                            }
                        }
                        is NetworkResult.Success -> {
                            val patient = (patientProfileState as NetworkResult.Success<User>).data
                            val age = patient.dateOfBirth?.let { calculateAge(it) }

                            // 2x2 grid — simple label + value, no icons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                if (age != null) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Age", fontSize = 11.sp, color = Color(0xFF9E9E9E))
                                        Text("$age yrs", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF424242))
                                    }
                                }
                                if (!patient.bloodGroup.isNullOrBlank()) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Blood", fontSize = 11.sp, color = Color(0xFF9E9E9E))
                                        Text(patient.bloodGroup, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF424242))
                                    }
                                }
                            }

                            // Height & Weight row
                            val hasHeight = !patient.height.isNullOrBlank()
                            val hasWeight = !patient.weight.isNullOrBlank()
                            if (hasHeight || hasWeight) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    if (hasHeight) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("Height", fontSize = 11.sp, color = Color(0xFF9E9E9E))
                                            Text("${patient.height} ft", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF424242))
                                        }
                                    }
                                    if (hasWeight) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("Weight", fontSize = 11.sp, color = Color(0xFF9E9E9E))
                                            Text("${patient.weight} kg", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF424242))
                                        }
                                    }
                                }
                            }
                        }
                        is NetworkResult.Error -> {
                            Text(
                                "Could not load profile details",
                                fontSize = 12.sp,
                                color = ErrorRed,
                            )
                        }
                        else -> {}
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ═══════════════════════════════════════
            // View Records Button
            // ═══════════════════════════════════════
            MediButton(
                text = "View Records",
                onClick = { onViewRecords(appointment.patientId) },
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFFE0E0E0))
            )
            Spacer(modifier = Modifier.height(16.dp))

            // ═══════════════════════════════════════
            // Write Prescription Section
            // ═══════════════════════════════════════
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "Write Prescription",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A2E),
                )
                TextButton(onClick = { showForm = !showForm }) {
                    Text(
                        if (showForm) "Hide" else "Show",
                        color = Color(0xFF3F51B5),
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            AnimatedVisibility(visible = showForm) {
                Column {
                    orders.forEachIndexed { index, order ->
                        OrderRow(
                            index = index + 1,
                            order = order,
                            onUpdate = { updated -> orders[index] = updated },
                            onRemove = if (orders.size > 1) {
                                { orders.removeAt(index) }
                            } else null,
                        )
                        if (index < orders.lastIndex) {
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Add row button
                    TextButton(
                        onClick = { orders.add(MutableOrderRow()) },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp), tint = Color(0xFF3F51B5))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add Medicine / Test", color = Color(0xFF3F51B5), fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Submit
                    when (completeState) {
                        is NetworkResult.Loading -> {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center,
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(32.dp), color = Color(0xFF3F51B5))
                            }
                        }
                        is NetworkResult.Success -> {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = HealthGreen.copy(alpha = 0.1f)),
                                shape = RoundedCornerShape(14.dp),
                            ) {
                                Text(
                                    "✅  Prescription submitted & appointment completed!",
                                    modifier = Modifier.padding(16.dp),
                                    color = HealthGreen,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                )
                            }
                        }
                        is NetworkResult.Error -> {
                            Text(
                                "Error: ${(completeState as NetworkResult.Error).message}",
                                color = ErrorRed,
                                fontSize = 12.sp,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            SubmitPrescriptionButton(orders, appointment, viewModel)
                        }
                        else -> {
                            SubmitPrescriptionButton(orders, appointment, viewModel)
                        }
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════
// Submit button extracted for reuse
// ═══════════════════════════════════════════════════

@Composable
private fun SubmitPrescriptionButton(
    orders: List<MutableOrderRow>,
    appointment: Appointment,
    viewModel: DoctorViewModel,
) {
    MediButton(
        text = "Submit Prescription & Complete",
        onClick = {
            val doctorOrders = orders
                .filter { it.medicine.isNotBlank() || it.test.isNotBlank() }
                .map {
                    DoctorOrder(
                        medicine = it.medicine,
                        test = it.test.ifBlank { null },
                        doseStrength = it.doseStrength.ifBlank { null },
                        doseSchedule = it.doseSchedule.ifBlank { null },
                        notes = it.notes.ifBlank { null },
                    )
                }
            if (doctorOrders.isNotEmpty()) {
                viewModel.completeAppointmentWithOrders(appointment.id, doctorOrders, appointment)
            }
        },
        modifier = Modifier.fillMaxWidth(),
    )
}

// ═══════════════════════════════════════════════════
// Sub-composables
// ═══════════════════════════════════════════════════

@Composable
private fun InfoChip(icon: ImageVector, label: String, value: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(16.dp))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(label, fontSize = 11.sp, color = Color(0xFF9E9E9E), fontWeight = FontWeight.Medium)
            Text(value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF424242))
        }
    }
}

@Composable
private fun OrderRow(
    index: Int,
    order: MutableOrderRow,
    onUpdate: (MutableOrderRow) -> Unit,
    onRemove: (() -> Unit)?,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            // Orange accent bar
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(Color(0xFFFF9800), RoundedCornerShape(topStart = 14.dp, bottomStart = 14.dp))
            )

            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFFF9800).copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("$index", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF9800))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Order #$index",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF3F51B5),
                        )
                    }
                    if (onRemove != null) {
                        IconButton(onClick = onRemove, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.RemoveCircleOutline, "Remove", tint = ErrorRed, modifier = Modifier.size(20.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ShortTextField(
                        value = order.medicine,
                        onValueChange = { onUpdate(order.copy(medicine = it)) },
                        label = "Medicine",
                        icon = Icons.Default.MedicalServices,
                        modifier = Modifier.weight(1f),
                    )
                    ShortTextField(
                        value = order.test,
                        onValueChange = { onUpdate(order.copy(test = it)) },
                        label = "Test",
                        icon = Icons.Default.Science,
                        modifier = Modifier.weight(1f),
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ShortTextField(
                        value = order.doseStrength,
                        onValueChange = { onUpdate(order.copy(doseStrength = it)) },
                        label = "Dose Strength",
                        modifier = Modifier.weight(1f),
                    )
                    ShortTextField(
                        value = order.doseSchedule,
                        onValueChange = { onUpdate(order.copy(doseSchedule = it)) },
                        label = "Schedule",
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun ShortTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector? = null,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
        leadingIcon = icon?.let {
            { Icon(it, null, modifier = Modifier.size(18.dp)) }
        },
        singleLine = true,
        textStyle = MaterialTheme.typography.bodySmall,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF3F51B5),
            unfocusedBorderColor = Color(0xFFE0E0E0),
            unfocusedContainerColor = Color(0xFFFAFAFA),
            focusedContainerColor = Color.White,
        ),
    )
}

/**
 * Calculate age from a date-of-birth string (expected format: "dd/MM/yyyy" or timestamp Long).
 */
private fun calculateAge(dob: String): Int? {
    return try {
        // Try parsing as Long (timestamp)
        val timestamp = dob.toLongOrNull()
        if (timestamp != null) {
            val birthYear = java.util.Calendar.getInstance().apply { timeInMillis = timestamp }
                .get(java.util.Calendar.YEAR)
            val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
            return currentYear - birthYear
        }
        // Try parsing as date string
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val birthDate = sdf.parse(dob) ?: return null
        val birthCal = java.util.Calendar.getInstance().apply { time = birthDate }
        val today = java.util.Calendar.getInstance()
        var age = today.get(java.util.Calendar.YEAR) - birthCal.get(java.util.Calendar.YEAR)
        if (today.get(java.util.Calendar.DAY_OF_YEAR) < birthCal.get(java.util.Calendar.DAY_OF_YEAR)) {
            age--
        }
        age
    } catch (_: Exception) {
        null
    }
}
