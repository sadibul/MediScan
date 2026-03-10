package com.mediscan.app.ui.screens.doctor.appointments

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.BubbleChart
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Brush
import com.mediscan.app.core.theme.ErrorRed
import com.mediscan.app.core.theme.HealthGreen
import com.mediscan.app.core.theme.MediBlue
import com.mediscan.app.core.theme.TextSecondary
import com.mediscan.app.core.theme.WarningOrange
import com.mediscan.app.core.utils.NetworkResult
import com.mediscan.app.data.model.Prescription
import com.mediscan.app.ui.components.common.MediButton
import com.mediscan.app.ui.viewmodel.DoctorViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.math.cos
import kotlin.math.sin

// ── Diagnosis colors — maximally distinct, easy to tell apart ──
private val diagnosisColors = listOf(
    Color(0xFF2196F3), // Bright Blue
    Color(0xFFE53935), // Red
    Color(0xFF4CAF50), // Green
    Color(0xFFFF9800), // Orange
    Color(0xFF9C27B0), // Purple
    Color(0xFF00BCD4), // Cyan
    Color(0xFF795548), // Brown
    Color(0xFFFFEB3B), // Yellow
    Color(0xFFE91E63), // Pink
    Color(0xFF3F51B5), // Indigo
    Color(0xFF009688), // Teal
    Color(0xFFFF5722), // Deep Orange
    Color(0xFF607D8B), // Blue Grey
    Color(0xFF8BC34A), // Lime
    Color(0xFFCDDC39), // Yellow Green
    Color(0xFF673AB7), // Deep Purple
    Color(0xFF03A9F4), // Light Blue
    Color(0xFFFF4081), // Pink Accent
    Color(0xFF00E676), // Green Accent
    Color(0xFFFFAB40), // Orange Accent
)

private val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

/**
 * PatientRecordsScreen — shows patient's AI-scanned prescriptions as:
 *  1. Cleveland Dot Plot (default) — Y-axis = months, colored dots per unique diagnosis
 *     Multiple dots in same month if same disease appears multiple times
 *  2. Pie Chart toggle — diagnosis frequency with percentage labels
 * Includes year selector, diagnosis legend, and pull-to-refresh.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PatientRecordsScreen(
    viewModel: DoctorViewModel,
    patientId: String,
    onNavigateBack: () -> Unit,
) {
    val prescriptionsState by viewModel.patientPrescriptions.collectAsState()
    var showPieChart by remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Available years from data
    val allPrescriptions = (prescriptionsState as? NetworkResult.Success)?.data ?: emptyList()
    val availableYears = remember(allPrescriptions) {
        allPrescriptions
            .map { Calendar.getInstance().apply { timeInMillis = it.visitDate }.get(Calendar.YEAR) }
            .distinct()
            .sorted()
            .reversed()
    }
    var selectedYear by remember(availableYears) {
        mutableIntStateOf(availableYears.firstOrNull() ?: Calendar.getInstance().get(Calendar.YEAR))
    }

    // Filter prescriptions by year
    val filteredPrescriptions = remember(allPrescriptions, selectedYear) {
        allPrescriptions.filter { p ->
            Calendar.getInstance().apply { timeInMillis = p.visitDate }.get(Calendar.YEAR) == selectedYear
        }
    }

    // Build diagnosis data: Map<String (unique disease), List<Int (monthIndex 0-11)>>
    // Properly splits comma-separated diagnoses into individual diseases
    val diagnosisData = remember(filteredPrescriptions) {
        buildDiagnosisData(filteredPrescriptions)
    }

    // Unique diagnoses with colors
    val diagnosisList = remember(diagnosisData) { diagnosisData.keys.toList() }
    val diagnosisColorMap = remember(diagnosisList) {
        diagnosisList.mapIndexed { idx, name -> name to diagnosisColors[idx % diagnosisColors.size] }.toMap()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Patient Records", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.ArrowBack, "Back", tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                modifier = Modifier.background(
                    Brush.horizontalGradient(
                        colors = listOf(Color(0xFF1A237E), Color(0xFF3F51B5), Color(0xFF5C6BC0))
                    )
                )
            )
        },
        containerColor = Color(0xFFF4F6FB)
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                viewModel.loadPatientPrescriptions(patientId)
                scope.launch {
                    delay(1500)
                    isRefreshing = false
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                when (prescriptionsState) {
                    is NetworkResult.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    is NetworkResult.Error -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.ErrorOutline, null, tint = ErrorRed, modifier = Modifier.size(48.dp))
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Failed to load records", fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(12.dp))
                                MediButton(text = "Retry", onClick = { viewModel.loadPatientPrescriptions(patientId) })
                            }
                        }
                    }

                    is NetworkResult.Success -> {
                        if (allPrescriptions.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.BubbleChart, null, tint = TextSecondary, modifier = Modifier.size(64.dp))
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        "No prescription records found",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = TextSecondary,
                                    )
                                    Text(
                                        "Patient hasn't scanned any prescriptions yet",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary.copy(alpha = 0.7f),
                                    )
                                }
                            }
                        } else {
                            // ── Year selector + chart toggle ──
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                YearSelector(
                                    years = availableYears,
                                    selectedYear = selectedYear,
                                    onYearSelected = { selectedYear = it },
                                )

                                Row {
                                    FilterChip(
                                        selected = !showPieChart,
                                        onClick = { showPieChart = false },
                                        label = { Text("Dot Plot", fontSize = 12.sp, fontWeight = if (!showPieChart) FontWeight.Bold else FontWeight.Normal) },
                                        leadingIcon = { Icon(Icons.Default.BubbleChart, null, modifier = Modifier.size(16.dp)) },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = Color(0xFF3F51B5).copy(alpha = 0.15f),
                                            selectedLabelColor = Color(0xFF3F51B5),
                                        ),
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    FilterChip(
                                        selected = showPieChart,
                                        onClick = { showPieChart = true },
                                        label = { Text("Pie Chart", fontSize = 12.sp, fontWeight = if (showPieChart) FontWeight.Bold else FontWeight.Normal) },
                                        leadingIcon = { Icon(Icons.Default.PieChart, null, modifier = Modifier.size(16.dp)) },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = Color(0xFF3F51B5).copy(alpha = 0.15f),
                                            selectedLabelColor = Color(0xFF3F51B5),
                                        ),
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // ── Chart card ──
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(18.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        if (showPieChart) "Diagnosis Distribution — $selectedYear"
                                        else "Diagnosis Timeline — $selectedYear",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1A1A2E),
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))

                                    AnimatedContent(targetState = showPieChart, label = "chart_toggle") { isPie ->
                                        if (isPie) {
                                            PieChartView(
                                                diagnosisData = diagnosisData,
                                                colorMap = diagnosisColorMap,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(320.dp),
                                            )
                                        } else {
                                            ClevelandDotPlot(
                                                diagnosisData = diagnosisData,
                                                colorMap = diagnosisColorMap,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(380.dp),
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // ── Legend ──
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        "Diagnoses (${diagnosisList.size} unique)",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1A1A2E),
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp),
                                    ) {
                                        diagnosisList.forEach { diagnosis ->
                                            val count = diagnosisData[diagnosis]?.size ?: 0
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(12.dp)
                                                        .clip(CircleShape)
                                                        .background(diagnosisColorMap[diagnosis] ?: Color.Gray)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    "${diagnosis.replaceFirstChar { it.uppercase() }} ($count)",
                                                    style = MaterialTheme.typography.bodySmall,
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // ── Summary stats ──
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Summary", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
                                    Spacer(modifier = Modifier.height(10.dp))
                                    SummaryItem("Unique Diagnoses", "${diagnosisList.size}", Color(0xFF3F51B5))
                                    SummaryItem("Total Occurrences", "${diagnosisData.values.sumOf { it.size }}", Color(0xFF43A047))
                                    // Show ALL diseases that share the max count
                                    val maxCount = diagnosisData.values.maxOfOrNull { it.size } ?: 0
                                    val mostCommon = if (maxCount > 0) {
                                        diagnosisData.filter { it.value.size == maxCount }
                                            .keys
                                            .joinToString(", ") { it.replaceFirstChar { c -> c.uppercase() } }
                                    } else "—"
                                    SummaryItem("Most Common", mostCommon, Color(0xFFFF9800))
                                }
                            }

                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }

                    else -> {}
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════
// Cleveland Dot Plot — Custom Canvas
// Shows multiple dots in same month if same disease appears multiple times
// ═══════════════════════════════════════════════════

@Composable
private fun ClevelandDotPlot(
    diagnosisData: Map<String, List<Int>>,
    colorMap: Map<String, Color>,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val leftPadding = 60.dp.toPx()
        val topPadding = 16.dp.toPx()
        val bottomPadding = 16.dp.toPx()
        val chartWidth = size.width - leftPadding - 16.dp.toPx()
        val chartHeight = size.height - topPadding - bottomPadding
        val rowHeight = chartHeight / 12f

        // Draw horizontal guide lines + month labels on Y-axis
        for (monthIdx in 0 until 12) {
            val y = topPadding + monthIdx * rowHeight + rowHeight / 2
            // Guide line
            drawLine(
                color = Color.Gray.copy(alpha = 0.15f),
                start = Offset(leftPadding, y),
                end = Offset(size.width - 16.dp.toPx(), y),
                strokeWidth = 1.dp.toPx(),
            )
            // Month label
            drawContext.canvas.nativeCanvas.drawText(
                monthNames[monthIdx],
                leftPadding - 12.dp.toPx(),
                y + 5.dp.toPx(),
                android.graphics.Paint().apply {
                    color = android.graphics.Color.GRAY
                    textSize = 11.sp.toPx()
                    textAlign = android.graphics.Paint.Align.RIGHT
                }
            )
        }

        // Build a flat list of all dots: (monthIdx, diagnosisName, occurrenceIndex)
        // so we can position each dot properly
        data class DotInfo(val month: Int, val diagnosis: String, val color: Color)

        val allDots = mutableListOf<DotInfo>()
        diagnosisData.forEach { (diagnosis, months) ->
            val dotColor = colorMap[diagnosis] ?: Color.Gray
            months.forEach { monthIdx ->
                allDots.add(DotInfo(monthIdx, diagnosis, dotColor))
            }
        }

        // Group dots by month, then position them spread across X
        val dotsByMonth = allDots.groupBy { it.month }
        val dotRadius = 7.dp.toPx()

        dotsByMonth.forEach { (monthIdx, dots) ->
            val y = topPadding + monthIdx * rowHeight + rowHeight / 2
            val totalDots = dots.size
            val spacing = if (totalDots <= 1) 0f else (chartWidth * 0.8f) / (totalDots - 1)
            val startX = leftPadding + chartWidth * 0.1f

            dots.forEachIndexed { idx, dot ->
                val x = if (totalDots <= 1) {
                    leftPadding + chartWidth / 2f
                } else {
                    startX + idx * spacing
                }

                // Filled dot
                drawCircle(
                    color = dot.color,
                    radius = dotRadius,
                    center = Offset(x.coerceIn(leftPadding, size.width - 16.dp.toPx()), y),
                )
                // White border for clarity
                drawCircle(
                    color = Color.White,
                    radius = dotRadius,
                    center = Offset(x.coerceIn(leftPadding, size.width - 16.dp.toPx()), y),
                    style = Stroke(width = 1.5.dp.toPx()),
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════
// Pie Chart — Custom Canvas with percentage labels
// ═══════════════════════════════════════════════════

@Composable
private fun PieChartView(
    diagnosisData: Map<String, List<Int>>,
    colorMap: Map<String, Color>,
    modifier: Modifier = Modifier,
) {
    val totalOccurrences = diagnosisData.values.sumOf { it.size }.toFloat()

    Canvas(modifier = modifier) {
        if (totalOccurrences == 0f) return@Canvas

        val diameter = minOf(size.width, size.height) * 0.65f
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val topLeft = Offset(
            (size.width - diameter) / 2f,
            (size.height - diameter) / 2f,
        )

        var startAngle = -90f

        diagnosisData.forEach { (diagnosis, months) ->
            val sweepAngle = (months.size / totalOccurrences) * 360f
            val percentage = (months.size / totalOccurrences) * 100f
            val color = colorMap[diagnosis] ?: Color.Gray

            // Draw slice
            drawArc(
                color = color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                topLeft = topLeft,
                size = Size(diameter, diameter),
            )
            // White divider
            drawArc(
                color = Color.White,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                topLeft = topLeft,
                size = Size(diameter, diameter),
                style = Stroke(width = 2.dp.toPx()),
            )

            // Draw percentage label on the slice
            if (percentage >= 3f) { // Only show label if slice is big enough
                val midAngle = Math.toRadians((startAngle + sweepAngle / 2f).toDouble())
                val labelRadius = diameter * 0.35f // Position label inside the slice
                val labelX = centerX + (labelRadius * cos(midAngle)).toFloat()
                val labelY = centerY + (labelRadius * sin(midAngle)).toFloat()

                val labelText = "${percentage.toInt()}%"

                drawContext.canvas.nativeCanvas.drawText(
                    labelText,
                    labelX,
                    labelY + 5.dp.toPx(),
                    android.graphics.Paint().apply {
                        this.color = android.graphics.Color.WHITE
                        textSize = 12.sp.toPx()
                        textAlign = android.graphics.Paint.Align.CENTER
                        isFakeBoldText = true
                        setShadowLayer(3f, 1f, 1f, android.graphics.Color.BLACK)
                    }
                )
            }

            startAngle += sweepAngle
        }

        // Center hole (donut)
        val holeRadius = diameter * 0.22f
        drawCircle(
            color = Color.White,
            radius = holeRadius,
            center = Offset(centerX, centerY),
        )

        // Center text: total count
        drawContext.canvas.nativeCanvas.drawText(
            "${totalOccurrences.toInt()}",
            centerX,
            centerY + 5.dp.toPx(),
            android.graphics.Paint().apply {
                color = android.graphics.Color.DKGRAY
                textSize = 14.sp.toPx()
                textAlign = android.graphics.Paint.Align.CENTER
                isFakeBoldText = true
            }
        )
    }
}

// ═══════════════════════════════════════════════════
// Year Selector
// ═══════════════════════════════════════════════════

@Composable
private fun YearSelector(
    years: List<Int>,
    selectedYear: Int,
    onYearSelected: (Int) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        TextButton(onClick = { expanded = true }) {
            Text(
                "📅  $selectedYear",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3F51B5),
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            years.forEach { year ->
                DropdownMenuItem(
                    text = {
                        Text(
                            "$year",
                            fontWeight = if (year == selectedYear) FontWeight.Bold else FontWeight.Normal,
                            color = if (year == selectedYear) Color(0xFF3F51B5) else Color.Unspecified,
                        )
                    },
                    onClick = {
                        onYearSelected(year)
                        expanded = false
                    },
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════
// Helpers
// ═══════════════════════════════════════════════════

/**
 * Builds a map of Individual Disease → list of month indices (0-11) from prescriptions.
 * Properly splits comma-separated diagnosis strings into individual diseases.
 * 
 * e.g. prescription with diagnosis "fever,cold,headache" on Feb → 
 *   "fever" → [1], "cold" → [1], "headache" → [1]
 * 
 * If "fever" appears in both Feb and May prescriptions →
 *   "fever" → [1, 4]
 */
private fun buildDiagnosisData(prescriptions: List<Prescription>): Map<String, List<Int>> {
    val result = mutableMapOf<String, MutableList<Int>>()

    prescriptions.forEach { prescription ->
        val month = Calendar.getInstance().apply { timeInMillis = prescription.visitDate }
            .get(Calendar.MONTH)

        // Collect all diagnosis sources and split by comma
        val allDiagnoses = mutableSetOf<String>()
        
        // From the diagnosis string (comma-separated)
        prescription.diagnosis?.let { diagStr ->
            diagStr.split(",").forEach { d ->
                val trimmed = d.trim().lowercase()
                if (trimmed.isNotBlank()) allDiagnoses.add(trimmed)
            }
        }
        
        // From the diagnoses list (each entry may also be comma-separated)
        prescription.diagnoses.forEach { diagStr ->
            diagStr.split(",").forEach { d ->
                val trimmed = d.trim().lowercase()
                if (trimmed.isNotBlank()) allDiagnoses.add(trimmed)
            }
        }

        allDiagnoses.forEach { d ->
            result.getOrPut(d) { mutableListOf() }.add(month)
        }
    }

    return result
}

@Composable
private fun SummaryItem(label: String, value: String, valueColor: Color = Color(0xFF424242)) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, fontSize = 13.sp, color = Color(0xFF9E9E9E))
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = valueColor)
    }
}
