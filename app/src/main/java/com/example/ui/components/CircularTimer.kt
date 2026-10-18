package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MetabolicStage
import com.example.ui.theme.GoldAutophagy
import com.example.ui.theme.OrangeFlame
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateDark
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.theme.TealGlow
import com.example.ui.theme.TealPrimary
import com.example.ui.viewmodel.FastingTimerUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CircularTimer(
    timerState: FastingTimerUiState,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = timerState.progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 600),
        label = "timer_progress"
    )

    val timeFormatter = SimpleDateFormat("h:mm a", Locale.getDefault())

    Box(
        modifier = modifier
            .size(280.dp)
            .testTag("circular_timer_box"),
        contentAlignment = Alignment.Center
    ) {
        // Custom Canvas for the Multi-layered Circular Timer
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 14.dp.toPx()
            val diameter = size.minDimension - strokeWidth
            val arcSize = Size(diameter, diameter)
            val topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f)

            // 1. Background Track
            drawArc(
                color = SlateBorder.copy(alpha = 0.45f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // 2. Active Progress Arc
            val primaryArcColor = if (timerState.isOvertime) GoldAutophagy else TealPrimary
            val secondaryArcColor = if (timerState.isOvertime) OrangeFlame else TealGlow

            val sweepAngle = if (timerState.isFasting) {
                animatedProgress * 360f
            } else 0f

            if (sweepAngle > 0f) {
                drawArc(
                    brush = Brush.sweepGradient(
                        0.0f to primaryArcColor,
                        0.7f to secondaryArcColor,
                        1.0f to primaryArcColor
                    ),
                    startAngle = -90f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }

            // 3. Autophagy 16h Threshold marker (if target >= 16h)
            val targetHours = timerState.customHours.coerceAtLeast(1f)
            if (targetHours >= 16f) {
                val autophagyAngleDeg = -90f + (16f / targetHours) * 360f
                val angleRad = Math.toRadians(autophagyAngleDeg.toDouble())
                val center = size.minDimension / 2f
                val radius = diameter / 2f

                val markerX = center + (radius * cos(angleRad)).toFloat()
                val markerY = center + (radius * sin(angleRad)).toFloat()

                drawCircle(
                    color = GoldAutophagy,
                    radius = 4.5.dp.toPx(),
                    center = Offset(markerX, markerY)
                )
            }
        }

        // Center Content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            if (timerState.isFasting) {
                // Active Fasting Mode
                val stage = timerState.currentStage
                val isAutophagyZone = stage == MetabolicStage.AUTOPHAGY || stage == MetabolicStage.DEEP_RENEWAL

                Surface(
                    color = if (isAutophagyZone) GoldAutophagy.copy(alpha = 0.2f) else stage.accentColor.copy(alpha = 0.18f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = if (isAutophagyZone) Icons.Default.AutoAwesome else Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = if (isAutophagyZone) GoldAutophagy else stage.accentColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        Text(
                            text = if (timerState.isOvertime) "AUTOPHAGY BONUS" else stage.shortName.uppercase(),
                            color = if (isAutophagyZone) GoldAutophagy else stage.accentColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Big Elapsed Timer String
                val elapsedSec = timerState.elapsedMillis / 1000
                val hours = elapsedSec / 3600
                val minutes = (elapsedSec % 3600) / 60
                val seconds = elapsedSec % 60
                val formattedTime = String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)

                Text(
                    text = formattedTime,
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = "ELAPSED TIME",
                    style = MaterialTheme.typography.labelSmall,
                    color = SlateTextSecondary,
                    fontSize = 10.sp,
                    letterSpacing = 1.2.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Remaining or Overtime String
                if (timerState.isOvertime) {
                    val otSec = timerState.overtimeMillis / 1000
                    val otH = otSec / 3600
                    val otM = (otSec % 3600) / 60
                    Text(
                        text = "+${otH}h ${otM}m over goal",
                        style = MaterialTheme.typography.labelLarge,
                        color = GoldAutophagy,
                        fontWeight = FontWeight.SemiBold
                    )
                } else {
                    val remSec = timerState.remainingMillis / 1000
                    val remH = remSec / 3600
                    val remM = (remSec % 3600) / 60
                    Text(
                        text = "${remH}h ${remM}m remaining",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SlateTextSecondary
                    )
                }

                timerState.session?.let { s ->
                    Text(
                        text = "Goal: ${timeFormatter.format(Date(s.targetEndTimeMillis))}",
                        style = MaterialTheme.typography.labelSmall,
                        color = SlateTextSecondary.copy(alpha = 0.8f),
                        fontSize = 10.sp
                    )
                }
            } else {
                // Ready / Eating Window Mode
                Surface(
                    color = TealPrimary.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "READY TO FAST",
                        color = TealGlow,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = timerState.selectedProtocol.ratio,
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = "${timerState.customHours.toInt()}h Target Fast",
                    style = MaterialTheme.typography.titleMedium,
                    color = TealPrimary,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = timerState.selectedProtocol.title,
                    style = MaterialTheme.typography.bodySmall,
                    color = SlateTextSecondary
                )
            }
        }
    }
}
