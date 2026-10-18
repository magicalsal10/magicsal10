package com.example.ui.components

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GoldAutophagy
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.theme.TealGlow
import com.example.ui.theme.TealPrimary
import com.example.ui.viewmodel.WeightChartPoint
import com.example.ui.viewmodel.WeightUnit
import java.util.Locale

@Composable
fun WeightLineChart(
    chartPoints: List<WeightChartPoint>,
    unit: WeightUnit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SlateSurface),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("weight_trend_chart_card")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Chart Header & Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "WEIGHT & ROLLING TREND",
                        style = MaterialTheme.typography.labelSmall,
                        color = SlateTextSecondary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.1.sp
                    )
                    Text(
                        text = "Water-Weight Smoothed Trend",
                        style = MaterialTheme.typography.titleMedium,
                        color = SlateTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Legend
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(TealPrimary)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Daily", fontSize = 11.sp, color = SlateTextSecondary)

                    Spacer(modifier = Modifier.width(10.dp))

                    Box(
                        modifier = Modifier
                            .width(12.dp)
                            .height(3.dp)
                            .clip(RoundedCornerShape(1.5.dp))
                            .background(GoldAutophagy)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "7D Avg", fontSize = 11.sp, color = GoldAutophagy, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (chartPoints.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Log at least two weights to visualize your trend.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SlateTextSecondary
                    )
                }
            } else {
                // Interactive Canvas Chart
                val allWeights = chartPoints.map { it.rawWeight } + chartPoints.mapNotNull { it.rollingAverage }
                val minWeight = (allWeights.minOrNull() ?: 70f) - 1.0f
                val maxWeight = (allWeights.maxOrNull() ?: 80f) + 1.0f
                val weightRange = (maxWeight - minWeight).coerceAtLeast(1f)

                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height
                            val paddingLeft = 32.dp.toPx()
                            val paddingRight = 16.dp.toPx()
                            val paddingTop = 16.dp.toPx()
                            val paddingBottom = 24.dp.toPx()

                            val chartW = w - paddingLeft - paddingRight
                            val chartH = h - paddingTop - paddingBottom

                            // Horizontal Grid Lines (3 lines: min, mid, max)
                            val gridLines = 3
                            for (i in 0..gridLines) {
                                val y = paddingTop + (chartH * (i.toFloat() / gridLines))
                                drawLine(
                                    color = SlateBorder.copy(alpha = 0.35f),
                                    start = Offset(paddingLeft, y),
                                    end = Offset(w - paddingRight, y),
                                    strokeWidth = 1.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                                )
                            }

                            if (chartPoints.size == 1) {
                                // Single point
                                val x = paddingLeft + (chartW / 2f)
                                val ptY = paddingTop + chartH - ((chartPoints[0].rawWeight - minWeight) / weightRange * chartH)
                                drawCircle(
                                    color = TealPrimary,
                                    radius = 6.dp.toPx(),
                                    center = Offset(x, ptY)
                                )
                                return@Canvas
                            }

                            // Calculate X and Y coordinates for raw points
                            val xStep = chartW / (chartPoints.size - 1).coerceAtLeast(1)

                            val rawPoints = chartPoints.mapIndexed { index, pt ->
                                val x = paddingLeft + (index * xStep)
                                val y = paddingTop + chartH - ((pt.rawWeight - minWeight) / weightRange * chartH)
                                Offset(x, y)
                            }

                            // Calculate X and Y for rolling average points
                            val avgPoints = chartPoints.mapIndexedNotNull { index, pt ->
                                val avg = pt.rollingAverage ?: return@mapIndexedNotNull null
                                val x = paddingLeft + (index * xStep)
                                val y = paddingTop + chartH - ((avg - minWeight) / weightRange * chartH)
                                Offset(x, y)
                            }

                            // 1. Draw Raw Weight Area Fill (Subtle gradient under daily points)
                            if (rawPoints.size > 1) {
                                val areaPath = Path().apply {
                                    moveTo(rawPoints.first().x, paddingTop + chartH)
                                    rawPoints.forEach { lineTo(it.x, it.y) }
                                    lineTo(rawPoints.last().x, paddingTop + chartH)
                                    close()
                                }
                                drawPath(
                                    path = areaPath,
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            TealPrimary.copy(alpha = 0.18f),
                                            Color.Transparent
                                        ),
                                        startY = paddingTop,
                                        endY = paddingTop + chartH
                                    )
                                )

                                // Draw Raw Weight Connecting Line
                                val rawPath = Path().apply {
                                    moveTo(rawPoints.first().x, rawPoints.first().y)
                                    for (i in 1 until rawPoints.size) {
                                        lineTo(rawPoints[i].x, rawPoints[i].y)
                                    }
                                }
                                drawPath(
                                    path = rawPath,
                                    color = TealPrimary.copy(alpha = 0.5f),
                                    style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                                )

                                // Draw Raw Data Dots
                                rawPoints.forEach { pt ->
                                    drawCircle(
                                        color = SlateSurface,
                                        radius = 5.dp.toPx(),
                                        center = pt
                                    )
                                    drawCircle(
                                        color = TealGlow,
                                        radius = 3.5.dp.toPx(),
                                        center = pt
                                    )
                                }
                            }

                            // 2. Draw 7-Day Rolling Average Line (Smooth thick golden curve)
                            if (avgPoints.size > 1) {
                                val avgPath = Path().apply {
                                    moveTo(avgPoints.first().x, avgPoints.first().y)
                                    for (i in 1 until avgPoints.size) {
                                        // Cubic bezier or straight smooth connection
                                        val prev = avgPoints[i - 1]
                                        val curr = avgPoints[i]
                                        val cX = (prev.x + curr.x) / 2f
                                        cubicTo(cX, prev.y, cX, curr.y, curr.x, curr.y)
                                    }
                                }
                                drawPath(
                                    path = avgPath,
                                    color = GoldAutophagy,
                                    style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
                                )
                            }
                        }
                    }

                    // Bottom Date Labels
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 32.dp, end = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val firstLabel = chartPoints.firstOrNull()?.dateLabel ?: ""
                        val midIndex = chartPoints.size / 2
                        val midLabel = chartPoints.getOrNull(midIndex)?.dateLabel ?: ""
                        val lastLabel = chartPoints.lastOrNull()?.dateLabel ?: ""

                        Text(text = firstLabel, fontSize = 10.sp, color = SlateTextSecondary)
                        if (chartPoints.size > 2) {
                            Text(text = midLabel, fontSize = 10.sp, color = SlateTextSecondary)
                        }
                        Text(text = lastLabel, fontSize = 10.sp, color = SlateTextSecondary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Insight footnote
            Surface(
                color = SlateBorder.copy(alpha = 0.25f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Tip: Daily body weight fluctuates ±3-5 lbs due to sodium, water retention, and digestion. Trust the golden 7-Day Rolling Average curve for true fat loss trends.",
                    style = MaterialTheme.typography.bodySmall,
                    color = SlateTextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                )
            }
        }
    }
}
