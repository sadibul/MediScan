package com.mediscan.app.ui.screens.patient.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.mediscan.app.core.theme.MediBlue
import com.mediscan.app.core.theme.TextSecondary
import com.mediscan.app.core.utils.NetworkResult
import com.mediscan.app.data.model.Appointment
import com.mediscan.app.data.model.User
import com.mediscan.app.ui.components.AppointmentCard
import com.mediscan.app.ui.viewmodel.NotificationViewModel
import com.mediscan.app.ui.viewmodel.PatientViewModel
import kotlinx.coroutines.delay
import java.util.Calendar

// ── Daily Health Tips ──────────────────────────────────────────────────────────
private val dailyTips = listOf(
    "\uD83D\uDCA7 Drink 8 glasses of water today to stay hydrated & healthy",
    "\uD83C\uDFC3 A 30-minute walk a day keeps the doctor away",
    "\uD83E\uDDD8 Practice deep breathing to reduce stress and anxiety",
    "\uD83E\uDD66 Eat plenty of vegetables and fruits for a balanced diet",
    "\uD83D\uDCA4 Get 7–8 hours of quality sleep every night for optimal recovery",
    "\u2600\uFE0F  10 minutes of morning sunlight boosts your mood and vitamin D",
    "\uD83E\uDEB5 Wash your hands regularly to prevent infections",
    "\uD83D\uDE4F Take short breaks every hour if you sit for long periods",
    "\uD83C\uDF4E An apple a day keeps the doctor away — eat more whole fruits",
    "\uD83E\uDDD0 Regular health check-ups help catch issues early",
)

/**
 * PatientHomeScreen — redesigned dashboard matching the MediScan UI mockup.
 * Features: gradient header, floating health stats card, quick action 2×2 grid,
 * upcoming appointments, and a rotating daily health tips card.
 */
@Composable
fun PatientHomeScreen(
    viewModel: PatientViewModel,
    notificationViewModel: NotificationViewModel = hiltViewModel(),
    onNavigateToDoctorSearch: () -> Unit,
    onNavigateToNearbyHospitals: () -> Unit,
    onNavigateToAppointments: () -> Unit = {},
    onNavigateToDoctorOrders: () -> Unit = {},
    onNavigateToBuyMedicine: () -> Unit = {},
    onNavigateToDoctorDetail: (doctorId: String, appointmentDateTime: Long?, appointmentComplaint: String?) -> Unit = { _, _, _ -> },
    onNavigateToNotifications: () -> Unit = {},
    onReminderClick: () -> Unit = {},
) {
    val unreadCount by notificationViewModel.unreadCount.collectAsState()
    val userProfileState by viewModel.userProfile.collectAsState()
    val appointmentsState by viewModel.appointments.collectAsState()

    // Ensure notification observer is running
    LaunchedEffect(Unit) {
        notificationViewModel.startObserving()
    }

    // Refresh appointments every time this screen becomes visible
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    androidx.compose.runtime.DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                viewModel.loadUpcomingAppointments()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // Rotating tip index — changes every 4 seconds
    var tipIndex by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(4000L)
            tipIndex = (tipIndex + 1) % dailyTips.size
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6FB))
            .verticalScroll(rememberScrollState())
    ) {
        // ── Gradient Header + floating stats card overlay ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        ) {
            GreetingHeader(
                userProfileState = userProfileState,
                unreadNotificationCount = unreadCount,
                onNotificationsClick = onNavigateToNotifications,
            )

            // Floating health stats card that overlaps the header bottom
            HealthStatsCard(
                userProfileState = userProfileState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 48.dp)
                    .padding(horizontal = 20.dp)
            )
        }

        // Extra space to accommodate the floating card overhang
        Spacer(modifier = Modifier.height(68.dp))

        // ── Upcoming Appointments ──
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            SectionHeader(
                title = "Upcoming Appointment",
                onViewAll = onNavigateToAppointments
            )
            Spacer(modifier = Modifier.height(8.dp))
            when (val state = appointmentsState) {
                is NetworkResult.Success -> {
                    val list = state.data
                    if (list.isEmpty()) {
                        EmptyAppointmentsCard(onBookNow = onNavigateToDoctorSearch)
                    } else {
                        LazyRow(
                            contentPadding = PaddingValues(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(list.take(5)) { appt ->
                                AppointmentCard(
                                    appointment = appt,
                                    onView = { onNavigateToDoctorDetail(appt.doctorId, appt.dateTime, appt.complaint) },
                                    modifier = Modifier.width(300.dp)
                                )
                            }
                        }
                    }
                }
                is NetworkResult.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MediBlue,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
                else -> EmptyAppointmentsCard(onBookNow = onNavigateToDoctorSearch)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Quick Actions 2×2 Grid ──
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A2E)
            )
            Spacer(modifier = Modifier.height(14.dp))
            QuickActionsGrid(
                onNavigateToDoctorSearch = onNavigateToDoctorSearch,
                onNavigateToNearbyHospitals = onNavigateToNearbyHospitals,
                onNavigateToDoctorOrders = onNavigateToDoctorOrders,
                onNavigateToBuyMedicine = onNavigateToBuyMedicine,
                onReminderClick = onReminderClick,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Rotating Daily Tip Card ──
        DailyTipCard(
            tipText = dailyTips[tipIndex],
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// ── Gradient Greeting Header ──────────────────────────────────────────────────

@Composable
private fun GreetingHeader(
    userProfileState: NetworkResult<User>,
    unreadNotificationCount: Int = 0,
    onNotificationsClick: () -> Unit = {},
) {
    val greeting = getGreeting()
    val userName = when (userProfileState) {
        is NetworkResult.Success -> userProfileState.data.fullName.ifBlank { "User" }
        else -> "User"
    }
    val profileImageUrl = when (userProfileState) {
        is NetworkResult.Success -> userProfileState.data.profileImageUrl
        else -> null
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(185.dp)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF1A237E), Color(0xFF3F51B5), Color(0xFF5C6BC0))
                )
            )
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column {
                Text(
                    text = "$greeting 👋",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = userName,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 22.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "How are you feeling today?",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.75f)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Notification bell with red dot
                Box {
                    IconButton(onClick = onNotificationsClick) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    if (unreadNotificationCount > 0) {
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

                // Avatar circle
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (!profileImageUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = profileImageUrl,
                            contentDescription = "Profile",
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop,
                        )
                    } else {
                        Text(
                            text = userName.take(2).uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}

// ── Floating Health Stats Card ────────────────────────────────────────────────

@Composable
private fun HealthStatsCard(
    userProfileState: NetworkResult<User>,
    modifier: Modifier = Modifier,
) {
    val user = (userProfileState as? NetworkResult.Success)?.data

    val heightVal = user?.height?.takeIf { it.isNotBlank() } ?: "00"
    val weightVal = user?.weight?.takeIf { it.isNotBlank() } ?: "00"
    val bloodGroupVal = user?.bloodGroup?.takeIf { it.isNotBlank() } ?: "--"

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            HealthStatItem(value = heightVal, unit = "ft", label = "Height")
            StatDivider()
            HealthStatItem(value = bloodGroupVal, unit = "", label = "Blood Group")
            StatDivider()
            HealthStatItem(value = weightVal, unit = "kg", label = "Weight")
        }
    }
}

@Composable
private fun HealthStatItem(value: String, unit: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A237E),
                fontSize = 22.sp
            )
            if (unit.isNotEmpty()) {
                Text(
                    text = " $unit",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun StatDivider() {
    Box(
        modifier = Modifier
            .height(40.dp)
            .width(1.dp)
            .background(Color(0xFFE0E0E0))
    )
}

// ── Quick Actions 2×2 Grid ────────────────────────────────────────────────────

@Composable
private fun QuickActionsGrid(
    onNavigateToDoctorSearch: () -> Unit,
    onNavigateToNearbyHospitals: () -> Unit,
    onNavigateToDoctorOrders: () -> Unit,
    onNavigateToBuyMedicine: () -> Unit,
    onReminderClick: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickActionGridCard(
                title = "Book Appointment",
                subtitle = "Top doctors near you",
                icon = Icons.Default.CalendarMonth,
                accentColor = Color(0xFF1A237E),
                accentColorLight = Color(0xFFE8EAF6),
                topBarColor = Color(0xFF3F51B5),
                modifier = Modifier.weight(1f),
                onClick = onNavigateToDoctorSearch
            )
            QuickActionGridCard(
                title = "Buy Medicines",
                subtitle = "Delivered to your door",
                icon = Icons.Default.LocalPharmacy,
                accentColor = Color(0xFF2E7D32),
                accentColorLight = Color(0xFFE8F5E9),
                topBarColor = Color(0xFF43A047),
                modifier = Modifier.weight(1f),
                onClick = onNavigateToBuyMedicine
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickActionGridCard(
                title = "Nearby Hospitals",
                subtitle = "Clinics around you",
                icon = Icons.Default.LocalHospital,
                accentColor = Color(0xFFE65100),
                accentColorLight = Color(0xFFFFF3E0),
                topBarColor = Color(0xFFFF7043),
                modifier = Modifier.weight(1f),
                onClick = onNavigateToNearbyHospitals
            )
            QuickActionGridCard(
                title = "Doctor Orders",
                subtitle = "Your prescriptions",
                icon = Icons.Default.MedicalServices,
                accentColor = Color(0xFF6A1B9A),
                accentColorLight = Color(0xFFF3E5F5),
                topBarColor = Color(0xFF9C27B0),
                modifier = Modifier.weight(1f),
                onClick = onNavigateToDoctorOrders
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickActionGridCard(
                title = "Reminder",
                subtitle = "Medicine schedule",
                icon = Icons.Default.Alarm,
                accentColor = Color(0xFF00796B),
                accentColorLight = Color(0xFFE0F2F1),
                topBarColor = Color(0xFF009688),
                modifier = Modifier.weight(1f),
                onClick = onReminderClick
            )
            QuickActionGridCard(
                title = "Upcoming Features",
                subtitle = "Stay tuned!",
                icon = Icons.Default.Info,
                accentColor = Color(0xFF546E7A),
                accentColorLight = Color(0xFFECEFF1),
                topBarColor = Color(0xFF78909C),
                modifier = Modifier.weight(1f),
                onClick = { /* Coming soon */ }
            )
        }
    }
}

@Composable
private fun QuickActionGridCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    accentColorLight: Color,
    topBarColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier
            .height(140.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Colored accent bar on top
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .background(topBarColor)
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Icon inside a soft rounded box
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(accentColorLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A2E),
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

// ── Rotating Daily Tip Card ───────────────────────────────────────────────────

@Composable
private fun DailyTipCard(tipText: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A237E)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.WaterDrop,
                    contentDescription = null,
                    tint = Color(0xFF64B5F6),
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "DAILY TIP",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.65f),
                    letterSpacing = 1.5.sp,
                    fontSize = 10.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                AnimatedContent(
                    targetState = tipText,
                    transitionSpec = {
                        (slideInVertically { h -> h } + fadeIn()) togetherWith
                                (slideOutVertically { h -> -h } + fadeOut())
                    },
                    label = "tipAnimation"
                ) { tip ->
                    Text(
                        text = tip,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

// ── Section Header ────────────────────────────────────────────────────────────

@Composable
private fun SectionHeader(title: String, onViewAll: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A2E)
        )
        TextButton(onClick = onViewAll) {
            Text(
                text = "View All",
                color = MediBlue,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// ── Empty Appointments Placeholder ───────────────────────────────────────────

@Composable
private fun EmptyAppointmentsCard(onBookNow: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8EAF6)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = Color(0xFF3F51B5),
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "No Upcoming Appointments",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A2E)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Book your first appointment today",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(10.dp))
            TextButton(onClick = onBookNow) {
                Text("Book Now", color = MediBlue, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

/** Returns a time-based greeting string. */
private fun getGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when {
        hour < 12 -> "Good Morning"
        hour < 17 -> "Good Afternoon"
        else -> "Good Evening"
    }
}
