package com.example.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateDark
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.theme.TealGlow
import com.example.ui.theme.TealPrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdjustStartTimeDialog(
    currentStartTime: Long,
    onConfirm: (newStartTime: Long) -> Unit,
    onDismiss: () -> Unit
) {
    val now = System.currentTimeMillis()
    var hoursAgo by remember { mutableFloatStateOf(1f) }

    val computedStartTime = now - (hoursAgo * 3600 * 1000).toLong()
    val timeFormat = SimpleDateFormat("h:mm a, MMM d", Locale.getDefault())

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SlateSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = TealPrimary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "Adjust Fast Start Time",
                    style = MaterialTheme.typography.titleLarge,
                    color = SlateTextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Forgot to press start? You can set your start time to when your last meal actually ended.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SlateTextSecondary,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Started:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SlateTextSecondary
                    )
                    Text(
                        text = "${hoursAgo.toInt()} hour(s) ago",
                        style = MaterialTheme.typography.titleMedium,
                        color = TealGlow,
                        fontWeight = FontWeight.Bold
                    )
                }

                Slider(
                    value = hoursAgo,
                    onValueChange = { hoursAgo = it },
                    valueRange = 0.5f..12f,
                    steps = 22,
                    colors = SliderDefaults.colors(
                        thumbColor = TealPrimary,
                        activeTrackColor = TealPrimary,
                        inactiveTrackColor = SlateBorder
                    ),
                    modifier = Modifier.testTag("adjust_start_slider")
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "New Start Time: ${timeFormat.format(Date(computedStartTime))}",
                    style = MaterialTheme.typography.bodySmall,
                    color = SlateTextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(computedStartTime) },
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                modifier = Modifier.testTag("confirm_adjust_start_time_button")
            ) {
                Text("Update Start Time", color = SlateDark, fontWeight = FontWeight.Bold)
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
