package com.mediscan.app.ui.screens.booking

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mediscan.app.core.theme.ErrorRed
import com.mediscan.app.core.theme.HealthGreen
import com.mediscan.app.core.theme.MediBlue
import com.mediscan.app.core.theme.TextSecondary
import com.mediscan.app.core.theme.WarningOrange
import com.mediscan.app.core.utils.NetworkResult
import com.mediscan.app.data.model.User
import com.mediscan.app.ui.components.common.MediButton
import com.mediscan.app.ui.viewmodel.BookingViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private val HeaderGradient
    @Composable get() = Brush.horizontalGradient(
        listOf(Color(0xFF1A237E), Color(0xFF3F51B5), Color(0xFF5C6BC0))
    )

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DoctorDetailScreen(
    doctorId: String,
    viewModel: BookingViewModel,
    onNavigateBack: () -> Unit,
    fromAppointment: Boolean = false,
    appointmentDateTime: Long = 0L,
    appointmentComplaint: String = "",
) {
    val doctorState by viewModel.doctorDetail.collectAsState()
    val bookingState by viewModel.bookingState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showBookingDialog by remember { mutableStateOf(false) }

    LaunchedEffect(doctorId) { viewModel.loadDoctorDetail(doctorId) }

    LaunchedEffect(bookingState) {
        when (bookingState) {
            is NetworkResult.Success -> {
                showBookingDialog = false
                snackbarHostState.showSnackbar("Appointment booked successfully!")
                viewModel.resetBookingState()
                onNavigateBack()
            }
            is NetworkResult.Error -> {
                snackbarHostState.showSnackbar((bookingState as NetworkResult.Error).message)
                viewModel.resetBookingState()
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFFF4F6FB),
    ) { innerPadding ->
        when (doctorState) {
            is NetworkResult.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = Color(0xFF1A237E))
                }
            }

            is NetworkResult.Success -> {
                val doctor = (doctorState as NetworkResult.Success<User>).data

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                ) {
                    // ── Gradient Header with profile ──
                    Box(modifier = Modifier.fillMaxWidth()) {
                        // Gradient background
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .background(HeaderGradient)
                        ) {
                            // Back button
                            IconButton(
                                onClick = onNavigateBack,
                                modifier = Modifier.padding(top = 8.dp, start = 4.dp),
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                            }

                            // Title
                            Text(
                                "Doctor Profile",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(top = 16.dp),
                            )
                        }

                        // ── Floating Profile Card ──
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                                .offset(y = 120.dp),
                            shape = RoundedCornerShape(20.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                // Avatar
                                if (!doctor.profileImageUrl.isNullOrBlank()) {
                                    AsyncImage(
                                        model = doctor.profileImageUrl,
                                        contentDescription = "Doctor photo",
                                        modifier = Modifier.size(90.dp).clip(CircleShape),
                                        contentScale = ContentScale.Crop,
                                    )
                                } else {
                                    val initials = doctor.fullName
                                        .split(" ").take(2)
                                        .mapNotNull { it.firstOrNull()?.uppercase() }
                                        .joinToString("").ifEmpty { "D" }
                                    Box(
                                        modifier = Modifier
                                            .size(90.dp)
                                            .clip(CircleShape)
                                            .background(Brush.horizontalGradient(listOf(Color(0xFF1A237E), Color(0xFF3F51B5)))),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(initials, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 32.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    "Dr. ${doctor.fullName}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1A237E),
                                )
                                if (!doctor.specialization.isNullOrBlank()) {
                                    Box(
                                        modifier = Modifier
                                            .padding(top = 6.dp)
                                            .background(Color(0xFF3F51B5).copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                                            .padding(horizontal = 14.dp, vertical = 4.dp),
                                    ) {
                                        Text(
                                            doctor.specialization,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = Color(0xFF3F51B5),
                                            fontWeight = FontWeight.SemiBold,
                                        )
                                    }
                                }
                                if (!doctor.hospital.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        doctor.hospital,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextSecondary,
                                    )
                                }
                            }
                        }
                    }

                    // spacer for the offset card
                    Spacer(modifier = Modifier.height(140.dp))

                    // ── Info Cards ──
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        // Professional Information
                        SectionCard(title = "Professional Information", accentColor = Color(0xFF3F51B5)) {
                            InfoRowStyled(
                                icon = Icons.Default.MedicalServices,
                                label = "Specialization",
                                value = doctor.specialization ?: "Not specified",
                                iconBg = MediBlue,
                            )
                            InfoRowStyled(
                                icon = Icons.Default.LocalHospital,
                                label = "Hospital",
                                value = doctor.hospital ?: "Not specified",
                                iconBg = HealthGreen,
                            )
                            InfoRowStyled(
                                icon = Icons.Default.VerifiedUser,
                                label = "License No.",
                                value = doctor.licenseNumber ?: "—",
                                iconBg = TextSecondary,
                            )
                            InfoRowStyled(
                                icon = Icons.Default.AttachMoney,
                                label = "Consultation Fee",
                                value = if (!doctor.consultationFee.isNullOrBlank()) "৳${doctor.consultationFee}" else "—",
                                iconBg = WarningOrange,
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Availability
                        SectionCard(title = "Availability", accentColor = HealthGreen) {
                            if (!doctor.availableDays.isNullOrEmpty()) {
                                Text("Available Days", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                                Spacer(modifier = Modifier.height(8.dp))
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp),
                                ) {
                                    doctor.availableDays.forEach { day ->
                                        Box(
                                            modifier = Modifier
                                                .background(Color(0xFF3F51B5).copy(alpha = 0.1f), RoundedCornerShape(10.dp))
                                                .padding(horizontal = 14.dp, vertical = 6.dp),
                                        ) {
                                            Text(day, style = MaterialTheme.typography.labelSmall,
                                                color = Color(0xFF3F51B5), fontWeight = FontWeight.SemiBold)
                                        }
                                    }
                                }
                            } else {
                                Text("No availability set", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                            }

                            if (!doctor.availableTimeRange.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .background(WarningOrange.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Icon(Icons.Default.AccessTime, null, modifier = Modifier.size(18.dp), tint = WarningOrange)
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        "Time: ${doctor.availableTimeRange}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium,
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Contact Info
                        if (doctor.phone.isNotBlank()) {
                            SectionCard(title = "Contact", accentColor = WarningOrange) {
                                InfoRowStyled(
                                    icon = Icons.Default.Phone,
                                    label = "Phone",
                                    value = doctor.phone,
                                    iconBg = MediBlue,
                                )
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                        }
                    }

                    // ── Appointment Details (when viewing from existing appointment) ──
                    if (fromAppointment) {
                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                            SectionCard(title = "Appointment Details", accentColor = Color(0xFF1A237E)) {
                                // Date
                                if (appointmentDateTime > 0L) {
                                    val dateFmt = SimpleDateFormat("EEEE, dd MMM yyyy", Locale.getDefault())
                                    val timeFmt = SimpleDateFormat("hh:mm a", Locale.getDefault())
                                    InfoRowStyled(
                                        icon = Icons.Default.CalendarMonth,
                                        label = "Date",
                                        value = dateFmt.format(java.util.Date(appointmentDateTime)),
                                        iconBg = Color(0xFF3F51B5),
                                    )
                                    InfoRowStyled(
                                        icon = Icons.Default.Schedule,
                                        label = "Time",
                                        value = timeFmt.format(java.util.Date(appointmentDateTime)),
                                        iconBg = WarningOrange,
                                    )
                                }
                                // Complaint / Reason
                                if (appointmentComplaint.isNotBlank()) {
                                    InfoRowStyled(
                                        icon = Icons.Default.MedicalServices,
                                        label = "Reason / Complaint",
                                        value = appointmentComplaint,
                                        iconBg = ErrorRed,
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(32.dp))
                    }

                    // ── Book Appointment Button (only for new bookings) ──
                    if (!fromAppointment) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .padding(bottom = 32.dp)
                                .background(
                                    Brush.horizontalGradient(listOf(Color(0xFF1A237E), Color(0xFF3F51B5))),
                                    RoundedCornerShape(16.dp),
                                )
                                .clip(RoundedCornerShape(16.dp))
                                .height(52.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            TextButton(
                                onClick = { showBookingDialog = true },
                                modifier = Modifier.fillMaxSize(),
                            ) {
                                Text(
                                    "Book Appointment",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                )
                            }
                        }
                    }
                }

                // Booking Dialog (only for new bookings)
                if (!fromAppointment && showBookingDialog) {
                    BookAppointmentDialog(
                        doctor = doctor,
                        isLoading = bookingState is NetworkResult.Loading,
                        onDismiss = { showBookingDialog = false },
                        onBook = { dateTime, complaint ->
                            viewModel.bookAppointment(doctor, dateTime, complaint)
                        },
                    )
                }
            }

            is NetworkResult.Error -> {
                val msg = (doctorState as NetworkResult.Error).message
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(msg, color = MaterialTheme.colorScheme.error)
                }
            }

            else -> {}
        }
    }
}

// ═══════════════════════════════════════════════════════════
// Section Card — with colored top accent bar
// ═══════════════════════════════════════════════════════════
@Composable
private fun SectionCard(
    title: String,
    accentColor: Color,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column {
            // Top accent bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(accentColor, RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A237E),
                )
                Spacer(modifier = Modifier.height(12.dp))
                content()
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════
// Styled Info Row with colored icon box
// ═══════════════════════════════════════════════════════════
@Composable
private fun InfoRowStyled(
    icon: ImageVector,
    label: String,
    value: String,
    iconBg: Color,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .background(iconBg.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, modifier = Modifier.size(18.dp), tint = iconBg)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
        }
    }
}

// ═══════════════════════════════════════════════════════════
// Book Appointment Dialog — BottomSheet Style
// ═══════════════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookAppointmentDialog(
    doctor: User,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onBook: (dateTimeMillis: Long, complaint: String) -> Unit,
) {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }

    var selectedDateMillis by remember { mutableLongStateOf(0L) }
    var selectedHour by remember { mutableStateOf(-1) }
    var selectedMinute by remember { mutableStateOf(0) }
    var complaint by remember { mutableStateOf("") }
    var complaintError by remember { mutableStateOf(false) }
    var dateError by remember { mutableStateOf<String?>(null) }
    var timeError by remember { mutableStateOf<String?>(null) }

    val dateFormat = remember { SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault()) }
    val dateText = if (selectedDateMillis > 0) dateFormat.format(selectedDateMillis) else "Select Date"
    val timeText = if (selectedHour >= 0) {
        val amPm = if (selectedHour < 12) "AM" else "PM"
        val h12 = if (selectedHour % 12 == 0) 12 else selectedHour % 12
        String.format(Locale.getDefault(), "%d:%02d %s", h12, selectedMinute, amPm)
    } else "Select Time"

    val availableDayNames = doctor.availableDays ?: emptyList()
    val dayNameToCalendar = mapOf(
        "Sun" to Calendar.SUNDAY, "Sunday" to Calendar.SUNDAY,
        "Mon" to Calendar.MONDAY, "Monday" to Calendar.MONDAY,
        "Tue" to Calendar.TUESDAY, "Tuesday" to Calendar.TUESDAY,
        "Wed" to Calendar.WEDNESDAY, "Wednesday" to Calendar.WEDNESDAY,
        "Thu" to Calendar.THURSDAY, "Thursday" to Calendar.THURSDAY,
        "Fri" to Calendar.FRIDAY, "Friday" to Calendar.FRIDAY,
        "Sat" to Calendar.SATURDAY, "Saturday" to Calendar.SATURDAY,
    )
    val allowedDaysOfWeek = remember(availableDayNames) {
        availableDayNames.mapNotNull { dayNameToCalendar[it] }.toSet()
    }

    data class TimeSlot(val startHour: Int, val startMinute: Int, val endHour: Int, val endMinute: Int)

    val timeSlot = remember(doctor.availableTimeRange) {
        val raw = doctor.availableTimeRange ?: return@remember null
        try {
            val parts = raw.split("-", "\u2013").map { it.trim() }
            if (parts.size != 2) return@remember null
            fun parseTime(s: String): Pair<Int, Int> {
                val cleaned = s.uppercase(Locale.getDefault()).replace("\\s+".toRegex(), " ")
                val isPm = cleaned.contains("PM")
                val digits = cleaned.replace("[^0-9:]".toRegex(), "")
                val timeParts = digits.split(":")
                var hour = timeParts[0].toInt()
                val minute = if (timeParts.size > 1) timeParts[1].toInt() else 0
                if (isPm && hour != 12) hour += 12
                if (!isPm && hour == 12) hour = 0
                return hour to minute
            }
            val (startH, startM) = parseTime(parts[0])
            val (endH, endM) = parseTime(parts[1])
            TimeSlot(startH, startM, endH, endM)
        } catch (_: Exception) { null }
    }

    val isFormValid = selectedDateMillis > 0 && selectedHour >= 0 && dateError == null && timeError == null && complaint.trim().isNotBlank()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = { if (!isLoading) onDismiss() },
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = {
            Box(
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .background(Color.LightGray, RoundedCornerShape(2.dp))
                )
            }
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp),
        ) {
            // ── Gradient header card ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 8.dp)
                    .background(
                        Brush.horizontalGradient(listOf(Color(0xFF1A237E), Color(0xFF3F51B5), Color(0xFF5C6BC0))),
                        RoundedCornerShape(20.dp),
                    )
                    .padding(20.dp),
            ) {
                Column {
                    Text(
                        "Book Appointment",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        "with Dr. ${doctor.fullName}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.85f),
                    )

                    // Fee badge
                    if (!doctor.consultationFee.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.18f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 12.dp, vertical = 5.dp),
                        ) {
                            Text(
                                "💳 Fee: ৳${doctor.consultationFee}",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }

                    // Availability info
                    if (availableDayNames.isNotEmpty() || timeSlot != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                                .padding(12.dp),
                        ) {
                            Row(verticalAlignment = Alignment.Top) {
                                Text("📅", fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        "Availability",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White,
                                    )
                                    if (availableDayNames.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            "Days: ${availableDayNames.joinToString(", ")}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.White.copy(alpha = 0.8f),
                                        )
                                    }
                                    if (timeSlot != null) {
                                        Spacer(modifier = Modifier.height(1.dp))
                                        Text(
                                            "Time: ${doctor.availableTimeRange}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.White.copy(alpha = 0.8f),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Date & Time row ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // Date picker button
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FC)),
                    onClick = {
                        if (!isLoading) {
                            val c = Calendar.getInstance()
                            DatePickerDialog(
                                context,
                                { _, year, month, day ->
                                    val picked = Calendar.getInstance().apply {
                                        set(year, month, day, 0, 0, 0)
                                        set(Calendar.MILLISECOND, 0)
                                    }
                                    if (allowedDaysOfWeek.isNotEmpty() &&
                                        picked.get(Calendar.DAY_OF_WEEK) !in allowedDaysOfWeek
                                    ) {
                                        dateError = "Dr. ${doctor.fullName} is not available on this day. Available: ${availableDayNames.joinToString(", ")}"
                                        selectedDateMillis = 0L
                                    } else {
                                        dateError = null
                                        selectedDateMillis = picked.timeInMillis
                                    }
                                },
                                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH),
                            ).apply { datePicker.minDate = System.currentTimeMillis() }.show()
                        }
                    },
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            "DATE",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                            letterSpacing = 1.sp,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            dateText,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = if (selectedDateMillis > 0) Color(0xFF1A237E) else TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }

                // Time picker button
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FC)),
                    onClick = {
                        if (!isLoading) {
                            val initHour = timeSlot?.startHour ?: calendar.get(Calendar.HOUR_OF_DAY)
                            val initMin = timeSlot?.startMinute ?: calendar.get(Calendar.MINUTE)
                            TimePickerDialog(
                                context,
                                { _, hour, minute ->
                                    if (timeSlot != null) {
                                        val selectedTotal = hour * 60 + minute
                                        val startTotal = timeSlot.startHour * 60 + timeSlot.startMinute
                                        val endTotal = timeSlot.endHour * 60 + timeSlot.endMinute
                                        if (selectedTotal < startTotal || selectedTotal > endTotal) {
                                            timeError = "Please select a time within ${doctor.availableTimeRange}"
                                            selectedHour = -1
                                        } else {
                                            timeError = null
                                            selectedHour = hour
                                            selectedMinute = minute
                                        }
                                    } else {
                                        timeError = null
                                        selectedHour = hour
                                        selectedMinute = minute
                                    }
                                },
                                initHour, initMin, false,
                            ).show()
                        }
                    },
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            "TIME",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                            letterSpacing = 1.sp,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            timeText,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = if (selectedHour >= 0) Color(0xFF1A237E) else TextSecondary,
                            maxLines = 1,
                        )
                    }
                }
            }

            // Error messages
            if (dateError != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    dateError!!,
                    style = MaterialTheme.typography.bodySmall,
                    color = WarningOrange,
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
            }
            if (timeError != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    timeError!!,
                    style = MaterialTheme.typography.bodySmall,
                    color = WarningOrange,
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Disease / Reason field ──
            Text(
                "DISEASE / REASON *",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(horizontal = 20.dp),
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = complaint,
                onValueChange = { complaint = it; complaintError = it.trim().isBlank() },
                placeholder = { Text("e.g. Chest pain, follow-up...", color = TextSecondary.copy(alpha = 0.6f)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(14.dp),
                maxLines = 3,
                enabled = !isLoading,
                isError = complaintError,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF3F51B5),
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    unfocusedContainerColor = Color(0xFFF8F9FC),
                    focusedContainerColor = Color.White,
                ),
                supportingText = if (complaintError) {
                    { Text("Please describe your disease or reason for visit", color = ErrorRed) }
                } else null,
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── Action buttons ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // Cancel
                Button(
                    onClick = { if (!isLoading) onDismiss() },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF0F0F5),
                        contentColor = TextSecondary,
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                    enabled = !isLoading,
                ) {
                    Text("Cancel", fontWeight = FontWeight.SemiBold)
                }

                // Book Now
                Button(
                    onClick = {
                        if (complaint.trim().isBlank()) { complaintError = true; return@Button }
                        if (isFormValid) {
                            val combined = Calendar.getInstance().apply {
                                timeInMillis = selectedDateMillis
                                set(Calendar.HOUR_OF_DAY, selectedHour)
                                set(Calendar.MINUTE, selectedMinute)
                                set(Calendar.SECOND, 0)
                            }.timeInMillis
                            onBook(combined, complaint)
                        }
                    },
                    modifier = Modifier
                        .weight(1.5f)
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1A237E),
                        contentColor = Color.White,
                        disabledContainerColor = Color(0xFF1A237E).copy(alpha = 0.4f),
                        disabledContentColor = Color.White.copy(alpha = 0.6f),
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                    enabled = isFormValid && !isLoading,
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = Color.White,
                        )
                    } else {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Book Now", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
