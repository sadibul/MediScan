package com.mediscan.app.ui.screens.patient.home

import android.app.TimePickerDialog
import android.widget.Toast
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mediscan.app.core.theme.ErrorRed
import com.mediscan.app.core.theme.MediBlue
import com.mediscan.app.core.theme.TextSecondary
import com.mediscan.app.core.utils.NetworkResult
import com.mediscan.app.data.model.Reminder
import com.mediscan.app.ui.components.common.MediButton
import java.util.Calendar
import java.util.Locale

private val allDays = listOf("Sat", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri")

// Indigo gradient — matches "View Your Reminders" accent and Edit Reminder header
private val ViewRemindersGradient
    @Composable get() = Brush.horizontalGradient(
        listOf(Color(0xFF303F9F), Color(0xFF3F51B5), Color(0xFF5C6BC0))
    )
// Teal gradient — "Add New Reminder" (fresh distinct color, not clashing green)
private val AddReminderGradient
    @Composable get() = Brush.horizontalGradient(
        listOf(Color(0xFF006064), Color(0xFF00838F), Color(0xFF0097A7))
    )

// ═══════════════════════════════════════════════════
// 1. Reminder Choice Dialog
// ═══════════════════════════════════════════════════

@Composable
fun ReminderChoiceDialog(
    onDismiss: () -> Unit,
    onViewReminders: () -> Unit,
    onAddNew: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                // Simple text header
                Text(
                    "Medicine Reminder",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A2E),
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    "Manage your medicine schedule",
                    fontSize = 13.sp,
                    color = TextSecondary,
                )

                Spacer(modifier = Modifier.height(24.dp))

                // View Your Reminders — accent card
                ChoiceOptionCard(
                    title = "View Your Reminders",
                    subtitle = "See & edit your saved reminders",
                    accentColor = Color(0xFF5C6BC0),
                    bgColor = Color(0xFFF0F0FF),
                    onClick = onViewReminders,
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Add New Reminder — accent card
                ChoiceOptionCard(
                    title = "Add New Reminder",
                    subtitle = "Create a new medicine schedule",
                    accentColor = Color(0xFF00838F),
                    bgColor = Color(0xFFE0F7FA),
                    onClick = onAddNew,
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Cancel centered
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                ) {
                    Text("Cancel", color = TextSecondary, fontSize = 15.sp)
                }
            }
        }
    }
}

@Composable
private fun ChoiceOptionCard(
    title: String,
    subtitle: String,
    accentColor: Color,
    bgColor: Color,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(modifier = Modifier.height(androidx.compose.foundation.layout.IntrinsicSize.Min)) {
            // Left accent bar
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .fillMaxSize()
                    .background(accentColor, RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
            )
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        subtitle,
                        fontSize = 12.sp,
                        color = TextSecondary,
                    )
                }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════
// 2. View Reminders Screen
// ═══════════════════════════════════════════════════

@Composable
fun ViewRemindersDialog(
    remindersState: NetworkResult<List<Reminder>>,
    onDismiss: () -> Unit,
    onEdit: (Reminder) -> Unit,
    onDelete: (Reminder) -> Unit,
    onAddNew: () -> Unit = {},
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFFF5F6FA),
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Header
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(ViewRemindersGradient)
                            .padding(horizontal = 20.dp, vertical = 22.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Column {
                                Text(
                                    "Your Reminders",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    "Tap to edit, swipe to delete",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.7f),
                                )
                            }
                            IconButton(onClick = onDismiss) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(Icons.Default.Close, "Close", tint = Color.White, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }

                    // Body
                    when (remindersState) {
                        is NetworkResult.Loading -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = MediBlue)
                            }
                        }
                        is NetworkResult.Success -> {
                            val reminders = remindersState.data
                            if (reminders.isEmpty()) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.Medication, null, tint = TextSecondary.copy(alpha = 0.4f), modifier = Modifier.size(72.dp))
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text("No reminders yet", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A2E))
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("Add your first medicine reminder", fontSize = 13.sp, color = TextSecondary)
                                    }
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 16.dp, vertical = 14.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                ) {
                                    items(reminders, key = { it.id }) { reminder ->
                                        ReminderListCard(
                                            reminder = reminder,
                                            onEdit = { onEdit(reminder) },
                                            onDelete = { onDelete(reminder) },
                                        )
                                    }
                                    // Bottom spacer for FAB
                                    item { Spacer(modifier = Modifier.height(72.dp)) }
                                }
                            }
                        }
                        is NetworkResult.Error -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Failed to load reminders", color = ErrorRed)
                            }
                        }
                        else -> {}
                    }
                }

                // FAB for quick-add (visible when not loading)
                if (remindersState !is NetworkResult.Loading) {
                    FloatingActionButton(
                        onClick = onAddNew,
                        containerColor = Color(0xFF43A047),
                        contentColor = Color.White,
                        shape = CircleShape,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 20.dp, bottom = 24.dp)
                            .size(56.dp),
                    ) {
                        Icon(Icons.Default.Add, "Add Reminder", modifier = Modifier.size(28.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ReminderListCard(
    reminder: Reminder,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Reminder", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete \"${reminder.medicineName}\"?") },
            confirmButton = {
                TextButton(onClick = { showDeleteConfirm = false; onDelete() }) {
                    Text("Delete", color = ErrorRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            },
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column {
            // Top accent bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(
                        Brush.horizontalGradient(listOf(Color(0xFF5C6BC0), Color(0xFF3F51B5))),
                        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                    )
            )

            Column(modifier = Modifier.padding(16.dp)) {
                // Name + Edit/Delete row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            reminder.medicineName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A2E),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        if (reminder.description.isNotBlank()) {
                            Text(
                                reminder.description,
                                fontSize = 12.sp,
                                color = TextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        // Edit icon button
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF3F51B5).copy(alpha = 0.08f))
                                .clickable(onClick = onEdit),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Default.Edit, "Edit", tint = Color(0xFF3F51B5), modifier = Modifier.size(17.dp))
                        }
                        // Delete icon button
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(ErrorRed.copy(alpha = 0.08f))
                                .clickable { showDeleteConfirm = true },
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Default.Delete, "Delete", tint = ErrorRed, modifier = Modifier.size(17.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Times
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        reminder.medicineTimes.joinToString("  •  ") { formatTime12Hour(it) },
                        fontSize = 14.sp,
                        color = Color(0xFFD84315),
                        fontWeight = FontWeight.Bold,
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Days + duration
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    reminder.daysOfWeek.forEach { day ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF3F51B5).copy(alpha = 0.08f))
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                        ) {
                            Text(day, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF3F51B5))
                        }
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "${reminder.timeDurationDays} days",
                        fontSize = 12.sp,
                        color = TextSecondary,
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════
// 3. Add / Edit Reminder Dialog
// ═══════════════════════════════════════════════════

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddReminderDialog(
    onDismiss: () -> Unit,
    onSave: (Reminder) -> Unit,
    existingReminder: Reminder? = null,
    onBack: (() -> Unit)? = null,
) {
    val context = LocalContext.current
    val isEditMode = existingReminder != null

    var medicineName by remember { mutableStateOf(existingReminder?.medicineName ?: "") }
    var description by remember { mutableStateOf(existingReminder?.description ?: "") }
    var timeDurationDays by remember { mutableStateOf(existingReminder?.timeDurationDays?.toString() ?: "1") }
    val medicineTimes = remember { mutableStateListOf<String>().also { list -> existingReminder?.medicineTimes?.let { list.addAll(it) } } }
    val selectedDays = remember { mutableStateListOf<String>().also { list -> existingReminder?.daysOfWeek?.let { list.addAll(it) } } }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFFF5F6FA),
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // ── Gradient Header ──
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (isEditMode) ViewRemindersGradient else AddReminderGradient)
                        .padding(horizontal = 20.dp, vertical = 22.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Back arrow (only in edit mode, navigates back to Your Reminders)
                            if (onBack != null) {
                                IconButton(onClick = onBack) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(Color.White.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Icon(
                                            Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "Back",
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp),
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            Column {
                                Text(
                                    if (isEditMode) "Edit Reminder" else "Add Reminder",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    "Set your medicine schedule",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.7f),
                                )
                            }
                        }
                        IconButton(onClick = onDismiss) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(Icons.Default.Close, "Close", tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }

                // ── Scrollable form body ──
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    // Medicine Name
                    FormCard(title = "Medicine Name") {
                        OutlinedTextField(
                            value = medicineName,
                            onValueChange = { medicineName = it },
                            placeholder = { Text("e.g. Paracetamol 500mg", color = TextSecondary.copy(alpha = 0.5f)) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = formFieldColors(),
                            singleLine = true,
                        )
                    }

                    // Description
                    FormCard(title = "Description") {
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            placeholder = { Text("e.g. Take after meal", color = TextSecondary.copy(alpha = 0.5f)) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = formFieldColors(),
                            maxLines = 3,
                        )
                    }

                    // Time Duration
                    FormCard(title = "Time Duration") {
                        OutlinedTextField(
                            value = timeDurationDays,
                            onValueChange = { v -> if (v.all { it.isDigit() }) timeDurationDays = v },
                            placeholder = { Text("e.g. 7") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = formFieldColors(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            suffix = { Text("Days", color = TextSecondary, fontWeight = FontWeight.Medium) },
                        )
                    }

                    // Medicine Time
                    FormCard(title = "Medicine Time") {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            medicineTimes.forEachIndexed { index, time ->
                                TimeChip(time = time, onRemove = { medicineTimes.removeAt(index) })
                            }
                            // + Add Time button
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color(0xFF009688).copy(alpha = 0.08f))
                                    .clickable {
                                        val cal = Calendar.getInstance()
                                        TimePickerDialog(context, { _, h, m ->
                                            val formatted = String.format(Locale.US, "%02d:%02d", h, m)
                                            if (formatted !in medicineTimes) medicineTimes.add(formatted)
                                            else Toast.makeText(context, "Time already added", Toast.LENGTH_SHORT).show()
                                        }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show()
                                    }
                                    .padding(horizontal = 16.dp, vertical = 10.dp),
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Add, "Add time", tint = Color(0xFF009688), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Add Time", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF009688))
                                }
                            }
                        }
                        if (medicineTimes.isEmpty()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Tap + to add medicine times", fontSize = 12.sp, color = TextSecondary.copy(alpha = 0.6f))
                        }
                    }

                    // Days of Week
                    FormCard(title = "Days of Week") {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            allDays.forEach { day ->
                                val isSelected = day in selectedDays
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { if (isSelected) selectedDays.remove(day) else selectedDays.add(day) },
                                    label = {
                                        Text(
                                            day,
                                            fontSize = 13.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        )
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFF00838F),
                                        selectedLabelColor = Color.White,
                                        containerColor = Color.White,
                                        labelColor = Color(0xFF424242),
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        borderColor = Color(0xFFE0E0E0),
                                        selectedBorderColor = Color(0xFF00838F),
                                        enabled = true,
                                        selected = isSelected,
                                    ),
                                )
                            }
                        }
                    }
                }

                // ── Save Button ──
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                ) {
                    MediButton(
                        text = if (isEditMode) "Update Reminder" else "Save Reminder",
                        onClick = {
                            when {
                                medicineName.isBlank() ->
                                    Toast.makeText(context, "Please enter medicine name", Toast.LENGTH_SHORT).show()
                                timeDurationDays.isBlank() || (timeDurationDays.toIntOrNull() ?: 0) <= 0 ->
                                    Toast.makeText(context, "Please enter a valid time duration", Toast.LENGTH_SHORT).show()
                                medicineTimes.isEmpty() ->
                                    Toast.makeText(context, "Please add at least one time", Toast.LENGTH_SHORT).show()
                                selectedDays.isEmpty() ->
                                    Toast.makeText(context, "Please select at least one day", Toast.LENGTH_SHORT).show()
                                else -> {
                                    val reminder = if (isEditMode) {
                                        existingReminder!!.copy(
                                            medicineName = medicineName.trim(),
                                            description = description.trim(),
                                            timeDurationDays = timeDurationDays.toInt(),
                                            medicineTimes = medicineTimes.toList(),
                                            daysOfWeek = selectedDays.toList(),
                                        )
                                    } else {
                                        Reminder(
                                            medicineName = medicineName.trim(),
                                            description = description.trim(),
                                            timeDurationDays = timeDurationDays.toInt(),
                                            medicineTimes = medicineTimes.toList(),
                                            daysOfWeek = selectedDays.toList(),
                                            startDate = System.currentTimeMillis(),
                                        )
                                    }
                                    onSave(reminder)
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════
// Shared composables
// ═══════════════════════════════════════════════════

@Composable
private fun FormCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}

@Composable
private fun TimeChip(time: String, onRemove: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF009688).copy(alpha = 0.1f))
            .padding(start = 12.dp, end = 4.dp, top = 6.dp, bottom = 6.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.AccessTime, null, tint = Color(0xFF009688), modifier = Modifier.size(15.dp))
            Spacer(modifier = Modifier.width(5.dp))
            Text(formatTime12Hour(time), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF00695C))
            Spacer(modifier = Modifier.width(4.dp))
            IconButton(onClick = onRemove, modifier = Modifier.size(22.dp)) {
                Icon(Icons.Default.Close, "Remove", tint = Color(0xFF009688), modifier = Modifier.size(13.dp))
            }
        }
    }
}

/** Convert "HH:mm" to "h:mm AM/PM" display */
internal fun formatTime12Hour(time24: String): String {
    val parts = time24.split(":")
    if (parts.size != 2) return time24
    val hour = parts[0].toIntOrNull() ?: return time24
    val minute = parts[1].toIntOrNull() ?: return time24
    val amPm = if (hour < 12) "AM" else "PM"
    val hour12 = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }
    return String.format(Locale.US, "%d:%02d %s", hour12, minute, amPm)
}

@Composable
private fun formFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Color(0xFF00838F),
    unfocusedBorderColor = Color(0xFFE0E0E0),
    focusedLabelColor = Color(0xFF00838F),
    cursorColor = Color(0xFF00838F),
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
)
