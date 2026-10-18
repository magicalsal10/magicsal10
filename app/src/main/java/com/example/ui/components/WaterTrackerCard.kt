package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.WaterLogEntry
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GoldAutophagy
import com.example.ui.theme.SkyBlue
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateDark
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.SlateSurfaceVariant
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.theme.TealGlow
import com.example.ui.theme.TealPrimary
import com.example.ui.viewmodel.DailyWaterState
import com.example.ui.viewmodel.WaterUnit
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WaterTrackerCard(
    waterState: DailyWaterState,
    onAddWater: (amountMl: Int, note: String?) -> Unit,
    onDeleteWater: (id: Long) -> Unit,
    onOpenCustomDialog: () -> Unit,
    onOpenGoalDialog: () -> Unit,
    onToggleUnit: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showLogHistory by remember { mutableStateOf(true) }
    val timeFormat = remember { SimpleDateFormat("h:mm a", Locale.getDefault()) }

    val animatedProgress by animateFloatAsState(
        targetValue = waterState.progress.coerceIn(0f, 1f),
        animationSpec = tween(600),
        label = "water_progress"
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = SlateSurface),
        shape = RoundedCornerShape(20.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(SkyBlue.copy(alpha = 0.35f))
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("water_tracker_card")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(SkyBlue.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = null,
                            tint = SkyBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "DAILY HYDRATION",
                            style = MaterialTheme.typography.labelSmall,
                            color = SkyBlue,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.1.sp
                        )
                        Text(
                            text = "Water & Electrolyte Intake",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SlateTextPrimary
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Unit Toggle Chip
                    Surface(
                        color = SlateSurfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onToggleUnit() }
                            .testTag("toggle_water_unit_chip")
                    ) {
                        Text(
                            text = waterState.unit.label.uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SkyBlue,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Goal Edit Button
                    IconButton(
                        onClick = onOpenGoalDialog,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("edit_water_goal_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit daily goal",
                            tint = SlateTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Progress Bar & Intake Numbers
            val displayCurrent = if (waterState.unit == WaterUnit.OZ) {
                String.format(Locale.US, "%.1f", waterState.currentIntakeMl * 0.033814f)
            } else {
                "${waterState.currentIntakeMl}"
            }

            val displayGoal = if (waterState.unit == WaterUnit.OZ) {
                String.format(Locale.US, "%.1f %s", waterState.goalMl * 0.033814f, waterState.unit.label)
            } else {
                "${waterState.goalMl} ${waterState.unit.label}"
            }

            val percentInt = (waterState.progress * 100).toInt()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = displayCurrent,
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (waterState.isGoalReached) EmeraldGreen else SlateTextPrimary
                    )
                    Text(
                        text = " / $displayGoal",
                        style = MaterialTheme.typography.titleMedium,
                        color = SlateTextSecondary,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                Surface(
                    color = if (waterState.isGoalReached) EmeraldGreen.copy(alpha = 0.2f) else SkyBlue.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "$percentInt%",
                        color = if (waterState.isGoalReached) EmeraldGreen else SkyBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Custom Animated Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(SlateSurfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = if (waterState.isGoalReached) {
                                    listOf(TealPrimary, EmeraldGreen)
                                } else {
                                    listOf(SkyBlue, TealPrimary)
                                }
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Remaining or Goal Achieved label
            if (waterState.isGoalReached) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = EmeraldGreen,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Daily goal reached! Hydration supports cellular autophagy waste excretion.",
                        fontSize = 11.sp,
                        color = EmeraldGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            } else {
                val remainingStr = if (waterState.unit == WaterUnit.OZ) {
                    String.format(Locale.US, "%.1f oz", waterState.remainingMl * 0.033814f)
                } else {
                    "${waterState.remainingMl} ml"
                }
                Text(
                    text = "$remainingStr remaining to hit your daily target",
                    fontSize = 11.sp,
                    color = SlateTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Data Entry Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // +250ml (Glass)
                QuickAddButton(
                    amountLabel = if (waterState.unit == WaterUnit.OZ) "+8 oz" else "+250 ml",
                    subLabel = "Glass",
                    onClick = { onAddWater(250, "Glass of water") },
                    modifier = Modifier.weight(1f)
                )

                // +500ml (Bottle)
                QuickAddButton(
                    amountLabel = if (waterState.unit == WaterUnit.OZ) "+17 oz" else "+500 ml",
                    subLabel = "Bottle",
                    onClick = { onAddWater(500, "Water bottle") },
                    modifier = Modifier.weight(1f)
                )

                // +750ml (Flask)
                QuickAddButton(
                    amountLabel = if (waterState.unit == WaterUnit.OZ) "+25 oz" else "+750 ml",
                    subLabel = "Flask",
                    onClick = { onAddWater(750, "Large flask") },
                    modifier = Modifier.weight(1f)
                )

                // Custom Entry Button
                Surface(
                    color = SkyBlue.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SkyBlue.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onOpenCustomDialog() }
                        .testTag("quick_add_water_custom")
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Custom",
                            tint = SkyBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Custom",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SkyBlue
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Visual Log History Accordion Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { showLogHistory = !showLogHistory }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = SlateTextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Today's Visual Intake Logs (${waterState.logs.size})",
                        style = MaterialTheme.typography.labelMedium,
                        color = SlateTextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Icon(
                    imageVector = if (showLogHistory) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = SlateTextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Visual Log History List
            AnimatedVisibility(visible = showLogHistory) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (waterState.logs.isEmpty()) {
                        Surface(
                            color = SlateSurfaceVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "No water logged yet today. Use the buttons above to record hydration!",
                                fontSize = 11.sp,
                                color = SlateTextSecondary,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    } else {
                        waterState.logs.forEach { entry ->
                            val amountStr = if (waterState.unit == WaterUnit.OZ) {
                                String.format(Locale.US, "+%.1f oz", entry.amountOz)
                            } else {
                                "+${entry.amountMl} ml"
                            }

                            Surface(
                                color = SlateSurfaceVariant.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 7.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.WaterDrop,
                                            contentDescription = null,
                                            tint = SkyBlue,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = amountStr,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SlateTextPrimary
                                        )
                                        entry.notes?.let { note ->
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Surface(
                                                color = SkyBlue.copy(alpha = 0.15f),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = note,
                                                    fontSize = 10.sp,
                                                    color = SkyBlue,
                                                    fontWeight = FontWeight.Medium,
                                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                                )
                                            }
                                        }
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = timeFormat.format(Date(entry.timestampMillis)),
                                            fontSize = 11.sp,
                                            color = SlateTextSecondary
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        IconButton(
                                            onClick = { onDeleteWater(entry.id) },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete entry",
                                                tint = SlateTextSecondary.copy(alpha = 0.6f),
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuickAddButton(
    amountLabel: String,
    subLabel: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = SlateSurfaceVariant,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = amountLabel,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = SlateTextPrimary
            )
            Text(
                text = subLabel,
                fontSize = 10.sp,
                color = SlateTextSecondary
            )
        }
    }
}
