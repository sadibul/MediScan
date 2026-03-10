package com.mediscan.app.ui.screens.hospitals

import android.Manifest
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.mediscan.app.core.theme.HealthGreen
import com.mediscan.app.core.theme.MediBlue
import com.mediscan.app.core.theme.TextSecondary
import com.mediscan.app.core.theme.WarningOrange
import kotlinx.coroutines.launch

data class NearbyHospital(
    val id: String,
    val name: String,
    val address: String,
    val type: String,            // "Hospital", "Clinic", "Pharmacy"
    val latLng: LatLng,
    val distance: String,        // e.g. "1.2 km"
    val openNow: Boolean = true,
)

/**
 * NearbyHospitalsScreen — Google Maps showing nearby hospitals with bottom sheet list.
 * Uses Maps Compose for map rendering + Accompanist for location permission.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun NearbyHospitalsScreen(
    onNavigateBack: () -> Unit,
) {
    val locationPermission = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState()

    // Default center — Dhaka, Bangladesh (adjust if needed)
    val defaultLocation = LatLng(23.8103, 90.4125)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 13f)
    }

    var selectedHospital by remember { mutableStateOf<NearbyHospital?>(null) }

    // Sample nearby hospitals around Dhaka
    val hospitals = remember {
        listOf(
            NearbyHospital(
                id = "1",
                name = "Dhaka Medical College Hospital",
                address = "Bakshibazar, Dhaka 1000",
                type = "Hospital",
                latLng = LatLng(23.7253, 90.3981),
                distance = "3.5 km",
                openNow = true,
            ),
            NearbyHospital(
                id = "2",
                name = "Square Hospital",
                address = "18/F, Bir Uttam Qazi Nuruzzaman Sarak, Dhaka 1205",
                type = "Hospital",
                latLng = LatLng(23.7539, 90.3762),
                distance = "4.1 km",
                openNow = true,
            ),
            NearbyHospital(
                id = "3",
                name = "United Hospital",
                address = "Plot 15, Road 71, Gulshan, Dhaka 1212",
                type = "Hospital",
                latLng = LatLng(23.7989, 90.4143),
                distance = "1.8 km",
                openNow = true,
            ),
            NearbyHospital(
                id = "4",
                name = "Ibn Sina Hospital",
                address = "House 48, Road 9/A, Dhanmondi, Dhaka 1209",
                type = "Clinic",
                latLng = LatLng(23.7461, 90.3742),
                distance = "5.2 km",
                openNow = false,
            ),
            NearbyHospital(
                id = "5",
                name = "Evercare Hospital",
                address = "Plot 81, Block E, Bashundhara R/A, Dhaka",
                type = "Hospital",
                latLng = LatLng(23.8219, 90.4289),
                distance = "2.6 km",
                openNow = true,
            ),
            NearbyHospital(
                id = "6",
                name = "Popular Medical Centre",
                address = "House 16, Road 2, Dhanmondi, Dhaka 1205",
                type = "Clinic",
                latLng = LatLng(23.7420, 90.3756),
                distance = "5.8 km",
                openNow = true,
            ),
        )
    }

    LaunchedEffect(Unit) {
        if (!locationPermission.status.isGranted) {
            locationPermission.launchPermissionRequest()
        }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 200.dp,
        sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        sheetContent = {
            HospitalListSheet(
                hospitals = hospitals,
                selectedHospital = selectedHospital,
                onHospitalClick = { hospital ->
                    selectedHospital = hospital
                    scope.launch {
                        cameraPositionState.animate(
                            CameraUpdateFactory.newLatLngZoom(hospital.latLng, 16f)
                        )
                    }
                },
            )
        },
        topBar = {
            TopAppBar(
                title = { Text("Nearby Hospitals", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // ── Google Map ──
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    isMyLocationEnabled = locationPermission.status.isGranted,
                ),
                uiSettings = MapUiSettings(
                    myLocationButtonEnabled = false,
                    zoomControlsEnabled = false,
                ),
            ) {
                hospitals.forEach { hospital ->
                    val isSelected = selectedHospital?.id == hospital.id
                    Marker(
                        state = MarkerState(position = hospital.latLng),
                        title = hospital.name,
                        snippet = hospital.address,
                        icon = BitmapDescriptorFactory.defaultMarker(
                            if (isSelected) BitmapDescriptorFactory.HUE_AZURE
                            else BitmapDescriptorFactory.HUE_RED
                        ),
                        onClick = {
                            selectedHospital = hospital
                            false
                        },
                    )
                }
            }

            // ── My Location FAB ──
            if (locationPermission.status.isGranted) {
                FloatingActionButton(
                    onClick = {
                        scope.launch {
                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLngZoom(defaultLocation, 13f)
                            )
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp),
                    containerColor = MaterialTheme.colorScheme.surface,
                ) {
                    Icon(Icons.Default.MyLocation, "My location", tint = MediBlue)
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════
// Bottom Sheet — Hospital List
// ═══════════════════════════════════════════════════════════
@Composable
private fun HospitalListSheet(
    hospitals: List<NearbyHospital>,
    selectedHospital: NearbyHospital?,
    onHospitalClick: (NearbyHospital) -> Unit,
) {
    Column {
        // Drag handle indicator
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 8.dp, bottom = 12.dp)
                .width(40.dp)
                .height(4.dp)
                .background(
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                    CircleShape,
                )
        )

        Text(
            "${hospitals.size} hospitals nearby",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(hospitals, key = { it.id }) { hospital ->
                HospitalCard(
                    hospital = hospital,
                    isSelected = selectedHospital?.id == hospital.id,
                    onClick = { onHospitalClick(hospital) },
                )
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

// ═══════════════════════════════════════════════════════════
// Hospital Card
// ═══════════════════════════════════════════════════════════
@Composable
private fun HospitalCard(
    hospital: NearbyHospital,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val typeColor = when (hospital.type) {
        "Hospital" -> MediBlue
        "Clinic" -> HealthGreen
        else -> WarningOrange
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 6.dp else 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MediBlue.copy(alpha = 0.05f)
            else
                MaterialTheme.colorScheme.surface,
        ),
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(typeColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Default.LocalHospital,
                    null,
                    tint = typeColor,
                    modifier = Modifier.size(24.dp),
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    hospital.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        null,
                        modifier = Modifier.size(12.dp),
                        tint = TextSecondary,
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        hospital.address,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    hospital.distance,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = typeColor,
                )
                Text(
                    if (hospital.openNow) "Open" else "Closed",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (hospital.openNow) HealthGreen else TextSecondary,
                )
            }
        }
    }
}
