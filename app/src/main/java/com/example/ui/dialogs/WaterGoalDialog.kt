package com.example.ui.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SkyBlue
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateDark
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.SlateSurfaceVariant
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.viewmodel.WaterUnit

@Composable
fun WaterGoalDialog(
    currentGoalMl: Int,
    unit: WaterUnit,
    onConfirm: (goalMl: Int) -> Unit,
    onDismiss: () -> Unit
) {
    var goalFloat by remember { mutableFloatStateOf(currentGoalMl.toFloat()) }
    val presetGoals = listOf(2000, 2500, 3000, 3500)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SlateSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Flag,
                    contentDescription = null,
                    tint = SkyBlue,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Daily Hydration Target",
                    style = MaterialTheme.typography.titleLarge,
                    color = SlateTextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Staying adequately hydrated during intermittent fasting optimizes cellular waste clearance, suppresses false hunger signals, and maintains electrolyte balance.",
                    style = MaterialTheme.typography.bodySmall,
                    color = SlateTextSecondary,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Target Goal:",
                        style = MaterialTheme.typography.titleMedium,
                        color = SlateTextPrimary
                    )
                    val displayGoal = if (unit == WaterUnit.OZ) {
                        String.format(java.util.Locale.US, "%.1f oz", goalFloat * 0.033814f)
                    } else {
                        "${goalFloat.toInt()} ml"
                    }
                    Text(
                        text = displayGoal,
                        style = MaterialTheme.typography.headlineSmall,
                        color = SkyBlue,
                        fontWeight = FontWeight.Bold
                    )
                }

                Slider(
                    value = goalFloat,
                    onValueChange = { goalFloat = it },
                    valueRange = 1000f..5000f,
                    steps = 15,
                    colors = SliderDefaults.colors(
                        thumbColor = SkyBlue,
                        activeTrackColor = SkyBlue,
                        inactiveTrackColor = SlateBorder
                    ),
                    modifier = Modifier.testTag("water_goal_slider")
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    presetGoals.forEach { preset ->
                        val isSelected = goalFloat.toInt() == preset
                        Surface(
                            color = if (isSelected) SkyBlue.copy(alpha = 0.2f) else SlateSurfaceVariant,
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) SkyBlue else SlateBorder
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { goalFloat = preset.toFloat() }
                        ) {
                            Text(
                                text = "${preset / 1000}L",
                                fontSize = 11.sp,
                                color = if (isSelected) SkyBlue else SlateTextSecondary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(vertical = 6.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(goalFloat.toInt()) },
                colors = ButtonDefaults.buttonColors(containerColor = SkyBlue),
                modifier = Modifier.testTag("confirm_water_goal_button")
            ) {
                Text("Update Target", color = SlateDark, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = SlateTextSecondary)
            ) {
                Text("Cancel")
            }
        }
    )
}
