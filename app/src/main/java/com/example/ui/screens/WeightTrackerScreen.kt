package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.entity.WeightEntry
import com.example.ui.components.WeightLineChart
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GoldAutophagy
import com.example.ui.theme.RoseError
import com.example.ui.theme.SlateBackground
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateDark
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.SlateSurfaceVariant
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.theme.TealGlow
import com.example.ui.theme.TealPrimary
import com.example.ui.viewmodel.ChartRange
import com.example.ui.viewmodel.FastingViewModel
import com.example.ui.viewmodel.WeightUnit
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightTrackerScreen(
    viewModel: FastingViewModel,
    modifier: Modifier = Modifier
) {
    val isMetric by viewModel.isMetric.collectAsStateWithLifecycle()
    val weightUnit by viewModel.weightUnit.collectAsStateWithLifecycle()
    val chartRange by viewModel.chartRange.collectAsStateWithLifecycle()
    val chartPoints by viewModel.weightChartData.collectAsStateWithLifecycle()
    val latestEntry by viewModel.latestWeight.collectAsStateWithLifecycle()
    val sevenDayAvg by viewModel.sevenDayMovingAverage.collectAsStateWithLifecycle()
    val allWeights by viewModel.allWeightsDesc.collectAsStateWithLifecycle()

    val unitLabel = if (isMetric) "kg" else "lbs"

    var weightInput by remember {
        val initialVal = if (latestEntry != null) {
            if (isMetric) latestEntry!!.weightKg else latestEntry!!.weightLbs
        } else {
            if (isMetric) 68.0f else 150.0f
        }
        mutableStateOf(String.format(Locale.US, "%.1f", initialVal))
    }

    var initialWeightLoaded by remember { mutableStateOf(false) }
    LaunchedEffect(latestEntry) {
        if (!initialWeightLoaded && latestEntry != null) {
            val defaultVal = if (isMetric) latestEntry!!.weightKg else latestEntry!!.weightLbs
            weightInput = String.format(Locale.US, "%.1f", defaultVal)
            initialWeightLoaded = true
        }
    }

    var previousMetric by remember { mutableStateOf(isMetric) }
    LaunchedEffect(isMetric) {
        if (previousMetric != isMetric) {
            val currentVal = weightInput.toFloatOrNull()
            if (currentVal != null) {
                val converted = if (isMetric) {
                    // Converting lbs to kg: kg = lbs * 0.45359237
                    currentVal * 0.45359237f
                } else {
                    // Converting kg to lbs: lbs = kg / 0.45359237
                    currentVal / 0.45359237f
                }
                weightInput = String.format(Locale.US, "%.1f", converted)
            }
            previousMetric = isMetric
        }
    }

    var notesInput by remember { mutableStateOf("") }
    var isLoggingOpen by remember { mutableStateOf(false) }

    val dateFormat = remember { SimpleDateFormat("EEE, MMM d, yyyy • h:mm a", Locale.getDefault()) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground)
            .padding(horizontal = 18.dp)
            .testTag("weight_tracker_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .padding(end = 12.dp)
                ) {
                    Text(
                        text = "Weight & Body Composition",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = SlateTextPrimary
                    )
                    Text(
                        text = "Track true fat loss through rolling averages",
                        style = MaterialTheme.typography.bodySmall,
                        color = SlateTextSecondary
                    )
                }

                // Custom Segmented Pill Toggle: lbs and kg
                Surface(
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
                    modifier = Modifier.testTag("unit_toggle_container")
                ) {
                    Row(
                        modifier = Modifier.padding(3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // lbs option
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (!isMetric) Color(0xFF14B8A6) else Color(0xFF1E293B))
                                .clickable { viewModel.setMetric(false) }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                                .testTag("unit_toggle_lbs"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "lbs",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (!isMetric) Color(0xFF0F172A) else Color(0xFF94A3B8)
                            )
                        }

                        // kg option
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isMetric) Color(0xFF14B8A6) else Color(0xFF1E293B))
                                .clickable { viewModel.setMetric(true) }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                                .testTag("unit_toggle_kg"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "kg",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isMetric) Color(0xFF0F172A) else Color(0xFF94A3B8)
                            )
                        }
                    }
                }
            }
        }

        // Summary Metric Cards
        item {
            val latestVal = latestEntry?.let {
                if (isMetric) it.weightKg else it.weightLbs
            }

            val earliestVal = allWeights.lastOrNull()?.let {
                if (isMetric) it.weightKg else it.weightLbs
            }

            val totalDelta = if (latestVal != null && earliestVal != null && allWeights.size > 1) {
                latestVal - earliestVal
            } else null

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Card 1: Latest Scale Weight
                Card(
                    colors = CardDefaults.cardColors(containerColor = SlateSurface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "LATEST WEIGHT",
                            fontSize = 10.sp,
                            color = SlateTextSecondary,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (latestVal != null) String.format(Locale.US, "%.1f %s", latestVal, unitLabel) else "--",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = SlateTextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Raw scale reading",
                            fontSize = 10.sp,
                            color = SlateTextSecondary
                        )
                    }
                }

                // Card 2: 7-Day Moving Average
                Card(
                    colors = CardDefaults.cardColors(containerColor = SlateSurface),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldAutophagy.copy(alpha = 0.4f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "7-DAY AVERAGE",
                                fontSize = 10.sp,
                                color = GoldAutophagy,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (sevenDayAvg != null) String.format(Locale.US, "%.1f %s", sevenDayAvg, unitLabel) else "--",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = GoldAutophagy
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Smoothed fat trend",
                            fontSize = 10.sp,
                            color = SlateTextSecondary
                        )
                    }
                }

                // Card 3: Total Change
                Card(
                    colors = CardDefaults.cardColors(containerColor = SlateSurface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "TOTAL CHANGE",
                            fontSize = 10.sp,
                            color = SlateTextSecondary,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        val isLoss = totalDelta != null && totalDelta <= 0f
                        Text(
                            text = if (totalDelta != null) String.format(Locale.US, "%+.1f %s", totalDelta, unitLabel) else "--",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (isLoss) EmeraldGreen else SlateTextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isLoss) "Net reduction" else "From day 1",
                            fontSize = 10.sp,
                            color = SlateTextSecondary
                        )
                    }
                }
            }
        }

        // Quick Log Weight Card / Form
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateSurface),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.FitnessCenter,
                                contentDescription = null,
                                tint = TealPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Log Today's Weight",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = SlateTextPrimary
                            )
                        }

                        Surface(
                            color = TealPrimary.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "Daily Routine",
                                color = TealGlow,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Stepper & Direct numeric input
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // Minus 1.0 button
                        OutlinedButton(
                            onClick = {
                                val current = weightInput.toFloatOrNull() ?: 70f
                                weightInput = String.format(Locale.US, "%.1f", (current - 1.0f).coerceAtLeast(20f))
                            },
                            shape = CircleShape,
                            modifier = Modifier.size(42.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                        ) {
                            Text("-1", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Minus 0.1 button
                        OutlinedButton(
                            onClick = {
                                val current = weightInput.toFloatOrNull() ?: 70f
                                weightInput = String.format(Locale.US, "%.1f", (current - 0.1f).coerceAtLeast(20f))
                            },
                            shape = CircleShape,
                            modifier = Modifier.size(42.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease 0.1", modifier = Modifier.size(16.dp))
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Value Text Field
                        OutlinedTextField(
                            value = weightInput,
                            onValueChange = { weightInput = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.headlineMedium.copy(
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                color = SlateTextPrimary
                            ),
                            suffix = {
                                Text(
                                    text = unitLabel,
                                    color = TealPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = TealPrimary,
                                unfocusedBorderColor = SlateBorder
                            ),
                            modifier = Modifier
                                .width(150.dp)
                                .testTag("weight_numeric_input")
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        // Plus 0.1 button
                        OutlinedButton(
                            onClick = {
                                val current = weightInput.toFloatOrNull() ?: 70f
                                weightInput = String.format(Locale.US, "%.1f", (current + 0.1f).coerceAtMost(300f))
                            },
                            shape = CircleShape,
                            modifier = Modifier.size(42.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Increase 0.1", modifier = Modifier.size(16.dp))
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Plus 1.0 button
                        OutlinedButton(
                            onClick = {
                                val current = weightInput.toFloatOrNull() ?: 70f
                                weightInput = String.format(Locale.US, "%.1f", (current + 1.0f).coerceAtMost(300f))
                            },
                            shape = CircleShape,
                            modifier = Modifier.size(42.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                        ) {
                            Text("+1", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = notesInput,
                        onValueChange = { notesInput = it },
                        placeholder = { Text("Notes (e.g. post-morning water, 18h fast completed)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TealPrimary,
                            unfocusedBorderColor = SlateBorder,
                            focusedTextColor = SlateTextPrimary,
                            unfocusedTextColor = SlateTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            val value = weightInput.toFloatOrNull()
                            if (value != null && value > 0) {
                                viewModel.logWeight(
                                    weightValue = value,
                                    notes = notesInput.ifBlank { null }
                                )
                                notesInput = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("save_weight_button")
                    ) {
                        Text(
                            text = "Save Entry",
                            color = SlateDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }

        // Chart Range Selector (7D, 30D, 90D, All)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TREND ANALYSIS",
                    style = MaterialTheme.typography.labelSmall,
                    color = SlateTextSecondary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.1.sp
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    ChartRange.entries.forEach { range ->
                        val isSelected = range == chartRange
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setChartRange(range) },
                            label = { Text(range.label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TealPrimary,
                                selectedLabelColor = SlateDark
                            ),
                            border = null,
                            modifier = Modifier.height(30.dp)
                        )
                    }
                }
            }
        }

        // Custom Canvas Line Chart
        item {
            WeightLineChart(
                chartPoints = chartPoints,
                unit = weightUnit
            )
        }

        // History Log List Header
        item {
            Text(
                text = "LOGGED WEIGHT HISTORY",
                style = MaterialTheme.typography.labelSmall,
                color = SlateTextSecondary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.1.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Weight Entries History List
        if (allWeights.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SlateSurface),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No weights logged yet. Log your first weight above!",
                            color = SlateTextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        } else {
            items(allWeights, key = { it.id }) { entry ->
                val displayWeight = if (isMetric) entry.weightKg else entry.weightLbs
                Card(
                    colors = CardDefaults.cardColors(containerColor = SlateSurface),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("weight_log_item_${entry.id}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = String.format(Locale.US, "%.1f %s", displayWeight, unitLabel),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = SlateTextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = dateFormat.format(Date(entry.timestampMillis)),
                                style = MaterialTheme.typography.bodySmall,
                                color = SlateTextSecondary,
                                fontSize = 11.sp
                            )
                            entry.notes?.let { n ->
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = n,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TealGlow,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        IconButton(
                            onClick = { viewModel.deleteWeight(entry.id) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete weight entry",
                                tint = SlateTextSecondary.copy(alpha = 0.7f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
