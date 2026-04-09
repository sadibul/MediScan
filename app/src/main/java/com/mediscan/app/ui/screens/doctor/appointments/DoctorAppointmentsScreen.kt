package com.mediscan.app.ui.screens.doctor.appointments

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.mediscan.app.ui.components.common.ShimmerAppointmentList
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.mediscan.app.core.utils.DateUtils
import com.mediscan.app.core.utils.NetworkResult
import com.mediscan.app.data.model.Appointment
import com.mediscan.app.ui.components.common.MediButton
import com.mediscan.app.ui.viewmodel.DoctorViewModel
import com.mediscan.app.ui.viewmodel.NotificationViewModel

/**
 * DoctorAppointmentsScreen — shows appointment list with status filter chips
 * and accept / cancel / complete actions.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorAppointmentsScreen(
    viewModel: DoctorViewModel,
    notificationViewModel: NotificationViewModel = hiltViewModel(),
    onNavigateToPatientRecords: (patientId: String) -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
) {
    val unreadCount by notificationViewModel.unreadCount.collectAsState()
    val appointmentsState by viewModel.appointments.collectAsState()
    val scope = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }
    val pullRefreshState = rememberPullToRefreshState()
    var selectedFilter by remember { mutableStateOf("all") }

    // Ensure notification observer is running
    LaunchedEffect(Unit) {
        notificationViewModel.startObserving()
    }

    var selectedAppointment by remember { mutableStateOf<Appointment?>(null) }
    var pendingDetailAppointment by remember { mutableStateOf<Appointment?>(null) }
    var appointmentToCancel by remember { mutableStateOf<Appointment?>(null) }

    // ── Cancel Confirmation Dialog ──
    appointmentToCancel?.let { appointment ->
        AlertDialog(
            onDismissRequest = { appointmentToCancel = null },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    "Cancel Appointment?",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A237E),
                )
            },
            text = {
                Text(
                    "Are you sure you want to cancel the appointment with ${appointment.patientName.ifBlank { "this patient" }}?",
                    color = TextSecondary,
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.cancelAppointment(appointment.id)
                        appointmentToCancel = null
                    },
                ) {
                    Text("Yes, Cancel", color = ErrorRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { appointmentToCancel = null }) {
                    Text("No, Keep", color = Color(0xFF1A237E))
                }
            },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6FB))
    ) {
        // ── Pending Patient Detail Dialog (simple info only) ──
        pendingDetailAppointment?.let { appointment ->
            PendingPatientDetailDialog(
                appointment = appointment,
                viewModel = viewModel,
                onDismiss = { pendingDetailAppointment = null },
            )
        }

        // ── PatientDetailSheet (confirmed — full with prescription) ──
        selectedAppointment?.let { appointment ->
            PatientDetailSheet(
                appointment = appointment,
                viewModel = viewModel,
                onDismiss = { selectedAppointment = null },
                onViewRecords = { patientId ->
                    selectedAppointment = null
                    onNavigateToPatientRecords(patientId)
                },
            )
        }

        // ═══════════════════════════════════════════
        // Gradient Header
        // ═══════════════════════════════════════════
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF1A237E),
                            Color(0xFF3F51B5),
                            Color(0xFF5C6BC0)
                        )
                    )
                )
                .padding(start = 20.dp, end = 20.dp, top = 48.dp, bottom = 20.dp)
        ) {
            Column {
                Text(
                    "Appointments",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Manage your patient appointments",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }

            // Notification bell
            Box(
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                IconButton(onClick = onNavigateToNotifications) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
                if (unreadCount > 0) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF44336))
                            .align(Alignment.TopEnd)
                            .offset(x = (-6).dp, y = 6.dp)
                    )
                }
            }
        }

        // ═══════════════════════════════════════════
        // Filter Chips
        // ═══════════════════════════════════════════
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                listOf(
                    "all" to "All",
                    "scheduled" to "Pending",
                    "confirmed" to "Confirmed",
                    "completed" to "Done",
                    "cancelled" to "Cancelled"
                )
            ) { (key, label) ->
                FilterChip(
                    selected = selectedFilter == key,
                    onClick = { selectedFilter = key },
                    label = { Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF3F51B5).copy(alpha = 0.15f),
                        selectedLabelColor = Color(0xFF3F51B5),
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
            }
        }

        // ═══════════════════════════════════════════
        // Content with pull-to-refresh
        // ═══════════════════════════════════════════
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                scope.launch {
                    isRefreshing = true
                    viewModel.loadAppointments()
                    delay(600)
                    isRefreshing = false
                }
            },
            state = pullRefreshState,
            modifier = Modifier.fillMaxSize(),
        ) {
            when (val state = appointmentsState) {
                is NetworkResult.Loading -> {
                    ShimmerAppointmentList(count = 4)
                }

                is NetworkResult.Success -> {
                    val filtered = if (selectedFilter == "all") state.data
                    else state.data.filter { it.status == selectedFilter }

                    if (filtered.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF3F51B5).copy(alpha = 0.08f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.EventAvailable, null,
                                        tint = Color(0xFF3F51B5), modifier = Modifier.size(40.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("No appointments found",
                                    fontSize = 16.sp, fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF424242))
                                if (selectedFilter != "all") {
                                    Text(
                                        "Try changing the filter",
                                        fontSize = 13.sp,
                                        color = Color(0xFF9E9E9E)
                                    )
                                }
                            }
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filtered, key = { it.id }) { appointment ->
                                AppointmentCard(
                                    appointment = appointment,
                                    onAccept = { viewModel.acceptAppointment(appointment.id) },
                                    onCancel = { appointmentToCancel = appointment },
                                    onComplete = { viewModel.completeAppointment(appointment.id) },
                                    onViewPatient = {
                                        viewModel.loadPatientProfile(appointment.patientId)
                                        if (appointment.status == "scheduled") {
                                            pendingDetailAppointment = appointment
                                        } else {
                                            selectedAppointment = appointment
                                        }
                                    },
                                )
                            }
                            item { Spacer(modifier = Modifier.height(16.dp)) }
                        }
                    }
                }

                is NetworkResult.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(32.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .background(ErrorRed.copy(alpha = 0.08f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.ErrorOutline, null,
                                    tint = ErrorRed, modifier = Modifier.size(40.dp),
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Failed to Load",
                                fontSize = 16.sp, fontWeight = FontWeight.Bold,
                                color = Color(0xFF424242)
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            MediButton(text = "🔄  Retry", onClick = { viewModel.loadAppointments() })
                        }
                    }
                }

                else -> {}
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════
// Appointment Card — colored left accent bar + polished layout
// ═══════════════════════════════════════════════════════════
@Composable
private fun AppointmentCard(
    appointment: Appointment,
    onAccept: () -> Unit,
    onCancel: () -> Unit,
    onComplete: () -> Unit,
    onViewPatient: () -> Unit = {},
) {
    val statusColor = when (appointment.status) {
        "scheduled" -> WarningOrange
        "confirmed" -> Color(0xFF3F51B5)
        "completed" -> HealthGreen
        "cancelled" -> ErrorRed
        else -> TextSecondary
    }
    val statusLabel = when (appointment.status) {
        "scheduled" -> "Pending"
        "confirmed" -> "Confirmed"
        "completed" -> "Completed"
        "cancelled" -> "Cancelled"
        else -> appointment.status
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (appointment.status in listOf("confirmed", "scheduled")) {
                    Modifier.clickable { onViewPatient() }
                } else Modifier
            ),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            // Left accent bar
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .fillMaxHeight()
                    .background(statusColor)
            )

            Column(modifier = Modifier.padding(16.dp)) {
                // Header: Patient name + status badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        if (!appointment.patientProfileImageUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = appointment.patientProfileImageUrl,
                                contentDescription = "Patient photo",
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop,
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(0xFF3F51B5), Color(0xFF5C6BC0))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                val initials = appointment.patientName
                                    .split(" ").take(2)
                                    .mapNotNull { it.firstOrNull()?.uppercase() }
                                    .joinToString("").ifEmpty { "P" }
                                Text(initials, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                appointment.patientName.ifBlank { "Patient" },
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1A1A2E)
                            )
                            if (!appointment.complaint.isNullOrBlank()) {
                                Text(
                                    appointment.complaint,
                                    fontSize = 12.sp,
                                    color = Color(0xFF9E9E9E),
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    // Status badge
                    Box(
                        modifier = Modifier
                            .background(statusColor.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            statusLabel,
                            fontSize = 11.sp,
                            color = statusColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Date & time row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF3F51B5).copy(alpha = 0.08f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.CalendarToday, null, tint = Color(0xFF3F51B5), modifier = Modifier.size(14.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        DateUtils.formatDate(appointment.dateTime),
                        fontSize = 12.sp,
                        color = Color(0xFF616161),
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF43A047).copy(alpha = 0.08f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Schedule, null, tint = Color(0xFF43A047), modifier = Modifier.size(14.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        DateUtils.formatTime(appointment.dateTime),
                        fontSize = 12.sp,
                        color = Color(0xFF616161),
                        fontWeight = FontWeight.Medium
                    )
                }

                // Action buttons based on status
                when (appointment.status) {
                    "scheduled" -> {
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                        ) {
                            TextButton(onClick = onAccept) {
                                Icon(Icons.Default.CheckCircle, null, tint = HealthGreen, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Accept", color = HealthGreen, fontWeight = FontWeight.SemiBold)
                            }
                            TextButton(onClick = onCancel) {
                                Icon(Icons.Default.Cancel, null, tint = ErrorRed, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Cancel", color = ErrorRed, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                    "confirmed" -> {
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                        ) {
                            TextButton(onClick = onViewPatient) {
                                Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF3F51B5), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Complete", color = Color(0xFF3F51B5), fontWeight = FontWeight.SemiBold)
                            }
                            TextButton(onClick = onCancel) {
                                Icon(Icons.Default.Cancel, null, tint = ErrorRed, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Cancel", color = ErrorRed, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════
// Pending Patient Detail Dialog — simple info modal for scheduled appointments
// ═══════════════════════════════════════════════════════════
@Composable
private fun PendingPatientDetailDialog(
    appointment: Appointment,
    viewModel: DoctorViewModel,
    onDismiss: () -> Unit,
) {
    val patientProfileState by viewModel.patientProfile.collectAsState()

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // ── Dark gradient header ──
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF1A237E), Color(0xFF303F9F), Color(0xFF3F51B5))
                            )
                        )
                        .padding(horizontal = 20.dp, vertical = 18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            "Patient Details",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                        )
                        IconButton(onClick = onDismiss) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    Icons.Default.Cancel, null,
                                    tint = Color.White, modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                // ── Content ──
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // Patient avatar + name
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(Color(0xFF3F51B5), Color(0xFF5C6BC0)))
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (!appointment.patientProfileImageUrl.isNullOrBlank()) {
                                AsyncImage(
                                    model = appointment.patientProfileImageUrl,
                                    contentDescription = "Patient photo",
                                    modifier = Modifier.size(52.dp).clip(CircleShape),
                                    contentScale = ContentScale.Crop,
                                )
                            } else {
                                val initials = appointment.patientName
                                    .split(" ").take(2)
                                    .mapNotNull { it.firstOrNull()?.uppercase() }
                                    .joinToString("").ifEmpty { "P" }
                                Text(initials, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                appointment.patientName.ifBlank { "Patient" },
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A1A2E),
                            )
                            if (!appointment.complaint.isNullOrBlank()) {
                                Text(
                                    "Reason: ${appointment.complaint}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF9E9E9E),
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color(0xFFEEEEEE))
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Health stats
                    when (patientProfileState) {
                        is NetworkResult.Loading -> {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                androidx.compose.material3.CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color(0xFF3F51B5),
                                )
                            }
                        }
                        is NetworkResult.Success -> {
                            val patient = (patientProfileState as NetworkResult.Success<com.mediscan.app.data.model.User>).data
                            val age = patient.dateOfBirth?.let {
                                try {
                                    val formats = listOf("yyyy-MM-dd", "dd/MM/yyyy", "MM/dd/yyyy", "dd-MM-yyyy")
                                    var birth: java.util.Date? = null
                                    for (fmt in formats) {
                                        try {
                                            birth = java.text.SimpleDateFormat(fmt, java.util.Locale.getDefault()).parse(it)
                                            if (birth != null) break
                                        } catch (_: Exception) {}
                                    }
                                    if (birth == null) return@let null
                                    val diff = System.currentTimeMillis() - birth.time
                                    (diff / (1000L * 60 * 60 * 24 * 365.25)).toInt()
                                } catch (_: Exception) { null }
                            }

                            // 2x2 grid — centered label + value
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                            ) {
                                if (age != null) {
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                    ) {
                                        Text("Age", fontSize = 11.sp, color = Color(0xFF9E9E9E))
                                        Text("$age yrs", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF424242))
                                    }
                                }
                                if (!patient.bloodGroup.isNullOrBlank()) {
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                    ) {
                                        Text("Blood", fontSize = 11.sp, color = Color(0xFF9E9E9E))
                                        Text(patient.bloodGroup, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF424242))
                                    }
                                }
                            }
                            val hasHeight = !patient.height.isNullOrBlank()
                            val hasWeight = !patient.weight.isNullOrBlank()
                            if (hasHeight || hasWeight) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                ) {
                                    if (hasHeight) {
                                        Column(
                                            modifier = Modifier.weight(1f),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                        ) {
                                            Text("Height", fontSize = 11.sp, color = Color(0xFF9E9E9E))
                                            Text("${patient.height} ft", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF424242))
                                        }
                                    }
                                    if (hasWeight) {
                                        Column(
                                            modifier = Modifier.weight(1f),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                        ) {
                                            Text("Weight", fontSize = 11.sp, color = Color(0xFF9E9E9E))
                                            Text("${patient.weight} kg", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF424242))
                                        }
                                    }
                                }
                            }
                        }
                        is NetworkResult.Error -> {
                            Text("Could not load details", fontSize = 12.sp, color = ErrorRed)
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}
