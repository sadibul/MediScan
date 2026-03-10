package com.mediscan.app.ui.screens.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mediscan.app.core.theme.HealthGreen
import com.mediscan.app.core.theme.MediBlue
import com.mediscan.app.core.theme.TextSecondary
import com.mediscan.app.core.theme.WarningOrange
import com.mediscan.app.core.utils.NetworkResult
import com.mediscan.app.data.model.User
import com.mediscan.app.ui.viewmodel.BookingViewModel

private val HeaderGradient
    @Composable get() = Brush.horizontalGradient(
        listOf(Color(0xFF1A237E), Color(0xFF3F51B5), Color(0xFF5C6BC0))
    )

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorSearchScreen(
    viewModel: BookingViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToDoctorDetail: (String) -> Unit,
) {
    val doctorsState by viewModel.doctors.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedSpec by viewModel.selectedSpecialization.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Find a Doctor", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 20.sp)
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
        containerColor = Color(0xFFF4F6FB),
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // ── Search Bar ── (filled style on white card)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
            ) {
                TextField(
                    value = searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    placeholder = {
                        Text(
                            "Search by name, specialization, or hospital",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary.copy(alpha = 0.6f),
                        )
                    },
                    leadingIcon = {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFF1A237E).copy(alpha = 0.08f), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Default.Search, null, tint = Color(0xFF1A237E), modifier = Modifier.size(20.dp))
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                )
            }

            // ── Content ──
            when (doctorsState) {
                is NetworkResult.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF1A237E))
                    }
                }

                is NetworkResult.Success -> {
                    val allDoctors = (doctorsState as NetworkResult.Success<List<User>>).data
                    val specializations = viewModel.getSpecializations(allDoctors)
                    val filteredDoctors = viewModel.getFilteredDoctors(allDoctors)

                    // ── Specialization Filter Chips ──
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(specializations) { spec ->
                            val isSelected = selectedSpec == spec
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.updateSpecializationFilter(spec) },
                                label = {
                                    Text(spec, style = MaterialTheme.typography.labelMedium,
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

                    Spacer(modifier = Modifier.height(12.dp))

                    if (filteredDoctors.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize().padding(32.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF1A237E).copy(alpha = 0.08f)),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(Icons.Default.Search, null, modifier = Modifier.size(40.dp),
                                        tint = Color(0xFF1A237E).copy(alpha = 0.4f))
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("No doctors found", style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold, color = Color(0xFF1A237E))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Try adjusting your search or filters",
                                    style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            }
                        }
                    } else {
                        // ── Results count badge ──
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFF1A237E).copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp),
                            ) {
                                Text(
                                    "${filteredDoctors.size} doctor${if (filteredDoctors.size != 1) "s" else ""} found",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color(0xFF1A237E),
                                    fontWeight = FontWeight.Medium,
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            items(filteredDoctors, key = { it.id }) { doctor ->
                                DoctorCard(doctor = doctor, onClick = { onNavigateToDoctorDetail(doctor.id) })
                            }
                            item { Spacer(modifier = Modifier.height(16.dp)) }
                        }
                    }
                }

                is NetworkResult.Error -> {
                    val msg = (doctorsState as NetworkResult.Error).message
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(msg, color = MaterialTheme.colorScheme.error)
                    }
                }

                else -> {}
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════
// Doctor Card — with left accent bar and polished layout
// ═══════════════════════════════════════════════════════════
@Composable
private fun DoctorCard(
    doctor: User,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            // ── Left accent bar (indigo) ──
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .fillMaxHeight()
                    .background(Color(0xFF3F51B5), RoundedCornerShape(topStart = 18.dp, bottomStart = 18.dp))
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Doctor avatar
                if (!doctor.profileImageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = doctor.profileImageUrl,
                        contentDescription = "Doctor photo",
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                    )
                } else {
                    val initials = doctor.fullName
                        .split(" ").take(2)
                        .mapNotNull { it.firstOrNull()?.uppercase() }
                        .joinToString("").ifEmpty { "D" }
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Brush.horizontalGradient(listOf(Color(0xFF1A237E), Color(0xFF3F51B5)))),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(initials, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Dr. ${doctor.fullName}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                    if (!doctor.specialization.isNullOrBlank()) {
                        Text(
                            text = doctor.specialization,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF3F51B5),
                            fontWeight = FontWeight.SemiBold,
                        )
                    }

                    if (!doctor.hospital.isNullOrBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .background(HealthGreen.copy(alpha = 0.1f), RoundedCornerShape(5.dp)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(12.dp), tint = HealthGreen)
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = doctor.hospital,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }

                    // Fee and availability
                    Row(
                        modifier = Modifier.padding(top = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (!doctor.consultationFee.isNullOrBlank()) {
                            Box(
                                modifier = Modifier
                                    .background(HealthGreen.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp),
                            ) {
                                Text(
                                    text = "৳${doctor.consultationFee}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = HealthGreen,
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        if (!doctor.availableDays.isNullOrEmpty()) {
                            Box(
                                modifier = Modifier
                                    .background(WarningOrange.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp),
                            ) {
                                Text(
                                    text = doctor.availableDays.take(3).joinToString(", "),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Medium,
                                    color = WarningOrange,
                                )
                            }
                        }
                    }
                }

                // Favorite star
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(WarningOrange.copy(alpha = 0.08f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Default.Star, null, tint = WarningOrange, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
