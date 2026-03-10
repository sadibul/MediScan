package com.mediscan.app.ui.screens.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import com.mediscan.app.ui.components.common.MediButton
import com.mediscan.app.ui.components.common.ShimmerAppointmentList
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
import com.mediscan.app.data.model.Appointment
import com.mediscan.app.ui.viewmodel.BookingViewModel

private val statusFilters = listOf("All", "Scheduled", "Confirmed", "Completed", "Cancelled")

private val HeaderGradient
    @Composable get() = Brush.horizontalGradient(
        listOf(Color(0xFF1A237E), Color(0xFF3F51B5), Color(0xFF5C6BC0))
    )

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientAppointmentsScreen(
    viewModel: BookingViewModel,
    onNavigateBack: () -> Unit,
) {
    val appointmentsState by viewModel.patientAppointments.collectAsState()
    val cancelState by viewModel.cancelState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }
    val pullRefreshState = rememberPullToRefreshState()
    var selectedFilter by remember { mutableStateOf("All") }
    var showCancelDialog by remember { mutableStateOf<Appointment?>(null) }

    LaunchedEffect(Unit) { viewModel.loadPatientAppointments() }

    LaunchedEffect(cancelState) {
        when (cancelState) {
            is NetworkResult.Success -> {
                snackbarHostState.showSnackbar("Appointment cancelled")
                viewModel.resetCancelState()
            }
            is NetworkResult.Error -> {
                snackbarHostState.showSnackbar((cancelState as NetworkResult.Error).message)
                viewModel.resetCancelState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("My Appointments", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 20.sp)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                modifier = Modifier.background(HeaderGradient),
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFFF4F6FB),
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // ── Filter Chips ──
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(statusFilters) { filter ->
                    val isSelected = selectedFilter == filter
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilter = filter },
                        label = {
                            Text(filter, style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF1A237E),
                            selectedLabelColor = Color.White,
                            containerColor = Color.White,
                            labelColor = TextSecondary,
                        ),
                        shape = RoundedCornerShape(20.dp),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = Color(0xFF1A237E).copy(alpha = 0.15f),
                            selectedBorderColor = Color.Transparent,
                            enabled = true, selected = isSelected,
                        ),
                    )
                }
            }

            // ── Content with pull-to-refresh ──
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = {
                    scope.launch {
                        isRefreshing = true
                        viewModel.loadPatientAppointments()
                        delay(600)
                        isRefreshing = false
                    }
                },
                state = pullRefreshState,
                modifier = Modifier.fillMaxSize(),
            ) {
                when (appointmentsState) {
                    is NetworkResult.Loading -> ShimmerAppointmentList(count = 4)

                    is NetworkResult.Success -> {
                        val allAppointments = (appointmentsState as NetworkResult.Success<List<Appointment>>).data
                        val filtered = if (selectedFilter == "All") allAppointments
                        else allAppointments.filter { it.status.equals(selectedFilter, ignoreCase = true) }

                        if (filtered.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .size(80.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF1A237E).copy(alpha = 0.08f)),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Icon(Icons.Default.CalendarMonth, null,
                                            modifier = Modifier.size(40.dp),
                                            tint = Color(0xFF1A237E).copy(alpha = 0.4f))
                                    }
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text("No appointments", style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold, color = Color(0xFF1A237E))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        if (selectedFilter == "All") "Book an appointment to get started"
                                        else "No ${selectedFilter.lowercase()} appointments",
                                        style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                }
                            }
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                items(filtered, key = { it.id }) { appointment ->
                                    PatientAppointmentCard(appointment = appointment, onCancel = { showCancelDialog = appointment })
                                }
                                item { Spacer(modifier = Modifier.height(16.dp)) }
                            }
                        }
                    }

                    is NetworkResult.Error -> {
                        val msg = (appointmentsState as NetworkResult.Error).message
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(32.dp),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(CircleShape)
                                        .background(ErrorRed.copy(alpha = 0.08f)),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(Icons.Default.ErrorOutline, null,
                                        tint = ErrorRed.copy(alpha = 0.7f), modifier = Modifier.size(36.dp))
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Failed to Load", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(msg, style = MaterialTheme.typography.bodySmall, color = TextSecondary, textAlign = TextAlign.Center)
                                Spacer(modifier = Modifier.height(20.dp))
                                MediButton(text = "🔄  Retry", onClick = { viewModel.loadPatientAppointments() })
                            }
                        }
                    }
                    else -> {}
                }
            }
        }

        // Cancel confirmation dialog
        showCancelDialog?.let { appointment ->
            AlertDialog(
                onDismissRequest = { showCancelDialog = null },
                containerColor = Color.White,
                shape = RoundedCornerShape(20.dp),
                title = { Text("Cancel Appointment?", fontWeight = FontWeight.Bold, color = Color(0xFF1A237E)) },
                text = { Text("Are you sure you want to cancel your appointment with Dr. ${appointment.doctorName}?", color = TextSecondary) },
                confirmButton = {
                    TextButton(onClick = { viewModel.cancelAppointment(appointment.id); showCancelDialog = null }) {
                        Text("Yes, Cancel", color = ErrorRed, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCancelDialog = null }) {
                        Text("No, Keep", color = Color(0xFF1A237E))
                    }
                },
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════
// Patient Appointment Card — with colored left accent bar
// ═══════════════════════════════════════════════════════════
@Composable
private fun PatientAppointmentCard(
    appointment: Appointment,
    onCancel: () -> Unit,
) {
    val statusColor = when (appointment.status.lowercase()) {
        "scheduled" -> WarningOrange
        "confirmed" -> MediBlue
        "completed" -> HealthGreen
        "cancelled" -> ErrorRed
        else -> TextSecondary
    }
    val statusIcon = when (appointment.status.lowercase()) {
        "scheduled" -> Icons.Default.Schedule
        "confirmed" -> Icons.Default.EventAvailable
        "completed" -> Icons.Default.CheckCircle
        "cancelled" -> Icons.Default.Cancel
        else -> Icons.Default.Schedule
    }
    val canCancel = appointment.status.lowercase() in listOf("scheduled", "confirmed")

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            // ── Left accent bar ──
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .fillMaxHeight()
                    .background(statusColor, RoundedCornerShape(topStart = 18.dp, bottomStart = 18.dp))
            )

            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Doctor info
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        val initials = appointment.doctorName
                            .split(" ").take(2)
                            .mapNotNull { it.firstOrNull()?.uppercase() }
                            .joinToString("").ifEmpty { "D" }
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(Brush.horizontalGradient(listOf(Color(0xFF1A237E), Color(0xFF3F51B5)))),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(initials, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Dr. ${appointment.doctorName}", style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            if (appointment.specialization.isNotBlank()) {
                                Text(appointment.specialization, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            }
                        }
                    }

                    // Status badge
                    Row(
                        modifier = Modifier
                            .background(statusColor.copy(alpha = 0.1f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(statusIcon, null, modifier = Modifier.size(14.dp), tint = statusColor)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(appointment.status.replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.labelSmall, color = statusColor, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Date/time row with icon box
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(Color(0xFF1A237E).copy(alpha = 0.08f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Default.CalendarMonth, null, modifier = Modifier.size(16.dp), tint = Color(0xFF1A237E))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(DateUtils.formatDateTime(appointment.dateTime),
                        style = MaterialTheme.typography.bodySmall, color = TextSecondary, fontWeight = FontWeight.Medium)
                }

                // Complaint
                if (!appointment.complaint.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(WarningOrange.copy(alpha = 0.08f), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center,
                        ) { Text("\uD83D\uDCAC", fontSize = 14.sp) }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Reason: ${appointment.complaint}", style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    }
                }

                // Cancel button
                if (canCancel) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = onCancel) {
                            Icon(Icons.Default.Cancel, null, modifier = Modifier.size(16.dp), tint = ErrorRed)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Cancel", color = ErrorRed, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}
