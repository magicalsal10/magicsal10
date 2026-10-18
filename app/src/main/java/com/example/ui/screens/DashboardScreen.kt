package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HistoryToggleOff
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import com.example.ui.dialogs.NotificationSettingsDialog
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.MetabolicStage
import com.example.ui.components.CircularTimer
import com.example.ui.components.MetabolicStageCard
import com.example.ui.components.ProtocolSelector
import com.example.ui.components.WaterTrackerCard
import com.example.ui.dialogs.AdjustStartTimeDialog
import com.example.ui.dialogs.CustomWaterDialog
import com.example.ui.dialogs.FinishFastDialog
import com.example.ui.dialogs.WaterGoalDialog
import com.example.ui.theme.GoldAutophagy
import com.example.ui.theme.OrangeFlame
import com.example.ui.theme.RoseError
import com.example.ui.theme.SlateBackground
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateDark
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.SlateSurfaceVariant
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.theme.TealDark
import com.example.ui.theme.TealGlow
import com.example.ui.theme.TealPrimary
import com.example.ui.viewmodel.FastingViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: FastingViewModel,
    modifier: Modifier = Modifier
) {
    val timerState by viewModel.timerUiState.collectAsStateWithLifecycle()
    val weeklyStats by viewModel.weeklyStats.collectAsStateWithLifecycle()
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsStateWithLifecycle()
    val waterState by viewModel.dailyWaterState.collectAsStateWithLifecycle()

    var showFinishDialog by remember { mutableStateOf(false) }
    var showAdjustStartDialog by remember { mutableStateOf(false) }
    var showCancelConfirmDialog by remember { mutableStateOf(false) }
    var showNotificationSettings by remember { mutableStateOf(false) }
    var showCustomWaterDialog by remember { mutableStateOf(false) }
    var showWaterGoalDialog by remember { mutableStateOf(false) }

    val dateFormat = remember { SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()) }
    val currentDateStr = remember { dateFormat.format(Date()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 12.dp)
            .testTag("dashboard_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "FastPulse",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = SlateTextPrimary
                )
                Text(
                    text = currentDateStr,
                    style = MaterialTheme.typography.bodySmall,
                    color = SlateTextSecondary
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Notification Settings Button
                IconButton(
                    onClick = { showNotificationSettings = true },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(SlateSurfaceVariant)
                        .testTag("notification_settings_button")
                ) {
                    Icon(
                        imageVector = if (notificationsEnabled) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                        contentDescription = "Notification Settings",
                        tint = if (notificationsEnabled) TealGlow else SlateTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Autophagy weekly badge
                Surface(
                    color = GoldAutophagy.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(GoldAutophagy.copy(alpha = 0.5f))
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = GoldAutophagy,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${String.format(Locale.US, "%.1f", weeklyStats.autophagyZoneHours)}h Autophagy",
                            color = GoldAutophagy,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Center Circular Timer
        CircularTimer(timerState = timerState)

        Spacer(modifier = Modifier.height(20.dp))

        // Fasting Actions
        if (timerState.isFasting) {
            // Active Fast Controls
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { showFinishDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (timerState.isOvertime) GoldAutophagy else TealPrimary
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("end_fast_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = null,
                        tint = SlateDark,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "End Fast & Log Session",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SlateDark
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { showAdjustStartDialog = true },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = SlateTextPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("adjust_start_time_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = TealGlow,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Edit Start", fontSize = 13.sp)
                    }

                    OutlinedButton(
                        onClick = { showCancelConfirmDialog = true },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = RoseError),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("cancel_fast_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            tint = RoseError,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Cancel Fast", fontSize = 13.sp)
                    }
                }

                if (notificationsEnabled) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = SlateSurfaceVariant.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = TealGlow,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Reminder active for ${timerState.customHours.toInt()}h goal completion" +
                                        if (timerState.customHours >= 16f) " (+ 16h Autophagy alert)" else "",
                                style = MaterialTheme.typography.bodySmall,
                                color = SlateTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        } else {
            // Ready / Not Fasting Controls
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { viewModel.startFast() },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("start_fast_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = SlateDark,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Start ${timerState.selectedProtocol.ratio} Fast",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SlateDark
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = { showAdjustStartDialog = true },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SlateTextSecondary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("start_earlier_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.HistoryToggleOff,
                        contentDescription = null,
                        tint = SlateTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("I started fasting earlier...", fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Physiological Metabolic Stage Card
        MetabolicStageCard(
            currentStage = timerState.currentStage,
            elapsedMillis = timerState.elapsedMillis,
            isFasting = timerState.isFasting,
            millisToNextStage = timerState.millisToNextStage
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Daily Water Intake System with Progress Bar & Visual Log History
        WaterTrackerCard(
            waterState = waterState,
            onAddWater = { amountMl, note -> viewModel.logWater(amountMl, note) },
            onDeleteWater = { id -> viewModel.deleteWaterLog(id) },
            onOpenCustomDialog = { showCustomWaterDialog = true },
            onOpenGoalDialog = { showWaterGoalDialog = true },
            onToggleUnit = { viewModel.toggleWaterUnit() }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Protocol Selector & Educational Cards
        ProtocolSelector(
            selectedProtocol = timerState.selectedProtocol,
            customHours = timerState.customHours,
            onProtocolSelected = { viewModel.selectProtocol(it) },
            onCustomHoursChanged = { viewModel.setCustomHours(it) }
        )

        Spacer(modifier = Modifier.height(28.dp))
    }

    // Finish Fast Dialog
    if (showFinishDialog) {
        val elapsedHours = timerState.elapsedMillis / (1000f * 60f * 60f)
        timerState.session?.let { session ->
            FinishFastDialog(
                session = session,
                elapsedHours = elapsedHours,
                onConfirm = { notes, feeling ->
                    viewModel.completeFast(notes, feeling)
                    showFinishDialog = false
                },
                onDismiss = { showFinishDialog = false }
            )
        }
    }

    // Adjust Start Time Dialog
    if (showAdjustStartDialog) {
        val currentStart = timerState.session?.startTimeMillis ?: System.currentTimeMillis()
        AdjustStartTimeDialog(
            currentStartTime = currentStart,
            onConfirm = { newStartTime ->
                if (timerState.isFasting) {
                    viewModel.adjustStartTime(newStartTime)
                } else {
                    viewModel.startFast(newStartTime)
                }
                showAdjustStartDialog = false
            },
            onDismiss = { showAdjustStartDialog = false }
        )
    }

    // Cancel Fast Confirmation Dialog
    if (showCancelConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showCancelConfirmDialog = false },
            containerColor = SlateSurface,
            title = {
                Text(
                    text = "Cancel Current Fast?",
                    style = MaterialTheme.typography.titleLarge,
                    color = SlateTextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "This will delete the current in-progress timer without saving it to your completed history.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SlateTextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.cancelFast()
                        showCancelConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoseError)
                ) {
                    Text("Yes, Cancel Fast", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showCancelConfirmDialog = false },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SlateTextSecondary)
                ) {
                    Text("Keep Fasting")
                }
            }
        )
    }

    // Notification Settings Dialog
    if (showNotificationSettings) {
        NotificationSettingsDialog(
            viewModel = viewModel,
            onDismiss = { showNotificationSettings = false }
        )
    }

    // Custom Water Intake Dialog
    if (showCustomWaterDialog) {
        CustomWaterDialog(
            unit = waterState.unit,
            onConfirm = { amountMl, note ->
                viewModel.logWater(amountMl, note)
                showCustomWaterDialog = false
            },
            onDismiss = { showCustomWaterDialog = false }
        )
    }

    // Water Goal Dialog
    if (showWaterGoalDialog) {
        WaterGoalDialog(
            currentGoalMl = waterState.goalMl,
            unit = waterState.unit,
            onConfirm = { newGoal ->
                viewModel.setWaterGoal(newGoal)
                showWaterGoalDialog = false
            },
            onDismiss = { showWaterGoalDialog = false }
        )
    }
}
