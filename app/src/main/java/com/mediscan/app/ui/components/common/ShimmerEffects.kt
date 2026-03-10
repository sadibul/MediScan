package com.mediscan.app.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.shimmer

// ══════════════════════════════════════════════════════════════
//  Reusable shimmer placeholder blocks
// ══════════════════════════════════════════════════════════════

/** Generic rounded rectangle shimmer placeholder */
@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    height: Dp = 16.dp,
    cornerRadius: Dp = 8.dp,
) {
    Box(
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(cornerRadius))
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
    )
}

// ── Prescription card shimmer ──────────────────────────────

/**
 * A shimmer placeholder that mirrors the layout of a PrescriptionCard.
 */
@Composable
fun ShimmerPrescriptionCard(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .shimmer()
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Icon placeholder
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    ShimmerBox(modifier = Modifier.fillMaxWidth(0.6f), height = 14.dp)
                    Spacer(modifier = Modifier.height(6.dp))
                    ShimmerBox(modifier = Modifier.fillMaxWidth(0.4f), height = 12.dp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                ShimmerBox(modifier = Modifier.width(52.dp), height = 22.dp, cornerRadius = 12.dp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            ShimmerBox(modifier = Modifier.fillMaxWidth(0.8f), height = 12.dp)
            Spacer(modifier = Modifier.height(6.dp))
            ShimmerBox(modifier = Modifier.fillMaxWidth(0.5f), height = 12.dp)
        }
    }
}

/**
 * Renders [count] ShimmerPrescriptionCard items in a LazyColumn.
 */
@Composable
fun ShimmerPrescriptionList(count: Int = 5) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 4.dp),
        userScrollEnabled = false,
    ) {
        items(count) {
            ShimmerPrescriptionCard()
        }
    }
}

// ── Appointment card shimmer ───────────────────────────────

/**
 * A shimmer placeholder that mirrors the layout of an AppointmentCard.
 */
@Composable
fun ShimmerAppointmentCard(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .shimmer()
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        ShimmerBox(modifier = Modifier.width(140.dp), height = 14.dp)
                        Spacer(modifier = Modifier.height(6.dp))
                        ShimmerBox(modifier = Modifier.width(100.dp), height = 12.dp)
                    }
                }
                ShimmerBox(modifier = Modifier.width(72.dp), height = 22.dp, cornerRadius = 12.dp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            ShimmerBox(modifier = Modifier.fillMaxWidth(0.55f), height = 12.dp)
            Spacer(modifier = Modifier.height(6.dp))
            ShimmerBox(modifier = Modifier.fillMaxWidth(0.7f), height = 12.dp)
        }
    }
}

/**
 * Renders [count] ShimmerAppointmentCard items in a LazyColumn.
 */
@Composable
fun ShimmerAppointmentList(count: Int = 4) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 4.dp),
        userScrollEnabled = false,
    ) {
        items(count) {
            ShimmerAppointmentCard()
        }
    }
}

// ── Doctor search card shimmer ─────────────────────────────

/**
 * A shimmer placeholder for a DoctorCard in the search screen.
 */
@Composable
fun ShimmerDoctorCard(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .shimmer()
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                ShimmerBox(modifier = Modifier.fillMaxWidth(0.65f), height = 14.dp)
                Spacer(modifier = Modifier.height(6.dp))
                ShimmerBox(modifier = Modifier.fillMaxWidth(0.45f), height = 12.dp)
                Spacer(modifier = Modifier.height(6.dp))
                ShimmerBox(modifier = Modifier.fillMaxWidth(0.3f), height = 12.dp)
            }
        }
    }
}

/**
 * Renders [count] ShimmerDoctorCard items in a LazyColumn.
 */
@Composable
fun ShimmerDoctorList(count: Int = 4) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 4.dp),
        userScrollEnabled = false,
    ) {
        items(count) {
            ShimmerDoctorCard()
        }
    }
}
