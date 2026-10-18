package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FastingProtocol
import com.example.ui.theme.GoldAutophagy
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.SlateSurfaceVariant
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.theme.TealGlow
import com.example.ui.theme.TealPrimary

@Composable
fun ProtocolSelector(
    selectedProtocol: FastingProtocol,
    customHours: Float,
    onProtocolSelected: (FastingProtocol) -> Unit,
    onCustomHoursChanged: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "SELECT FASTING PROTOCOL",
            style = MaterialTheme.typography.labelSmall,
            color = SlateTextSecondary,
            letterSpacing = 1.2.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)
        )

        // Horizontal Protocol Cards Carousel
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(FastingProtocol.entries) { protocol ->
                val isSelected = protocol == selectedProtocol

                val borderColor = if (isSelected) TealPrimary else SlateBorder.copy(alpha = 0.5f)
                val bgColor = if (isSelected) SlateSurfaceVariant else SlateSurface

                Card(
                    colors = CardDefaults.cardColors(containerColor = bgColor),
                    shape = RoundedCornerShape(16.dp),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(borderColor)
                    ),
                    modifier = Modifier
                        .width(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onProtocolSelected(protocol) }
                        .testTag("protocol_card_${protocol.id}")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = protocol.ratio,
                                style = MaterialTheme.typography.titleLarge,
                                color = if (isSelected) TealGlow else SlateTextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(TealPrimary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = protocol.title,
                            style = MaterialTheme.typography.labelMedium,
                            color = SlateTextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = protocol.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = SlateTextSecondary,
                            fontSize = 11.sp,
                            maxLines = 2,
                            lineHeight = 15.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Autophagy Pill
                        Surface(
                            color = if (protocol.fastingHours >= 16f) GoldAutophagy.copy(alpha = 0.15f) else SlateBorder.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = if (protocol.fastingHours >= 16f) "Autophagy Ready" else "Digestive Rest",
                                color = if (protocol.fastingHours >= 16f) GoldAutophagy else SlateTextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        // Expanded details for the selected protocol
        Spacer(modifier = Modifier.height(12.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = SlateSurface),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${selectedProtocol.title} (${selectedProtocol.ratio})",
                        style = MaterialTheme.typography.titleMedium,
                        color = SlateTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Surface(
                        color = TealPrimary.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = selectedProtocol.difficulty,
                            color = TealGlow,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = selectedProtocol.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = SlateTextSecondary,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Insulin Drop Badge
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(SlateSurfaceVariant, RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "INSULIN DROP",
                            fontSize = 9.sp,
                            color = SlateTextSecondary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = selectedProtocol.insulinReduction,
                            fontSize = 12.sp,
                            color = TealPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Autophagy Impact Badge
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(SlateSurfaceVariant, RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "AUTOPHAGY POTENTIAL",
                            fontSize = 9.sp,
                            color = SlateTextSecondary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = selectedProtocol.autophagyLevel,
                            fontSize = 12.sp,
                            color = GoldAutophagy,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // If Custom Protocol is selected, display slider
                AnimatedVisibility(visible = selectedProtocol == FastingProtocol.CUSTOM) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = null,
                                    tint = TealPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Target Fasting Duration:",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = SlateTextPrimary
                                )
                            }
                            Text(
                                text = "${customHours.toInt()} Hours",
                                style = MaterialTheme.typography.titleMedium,
                                color = TealGlow,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Slider(
                            value = customHours,
                            onValueChange = onCustomHoursChanged,
                            valueRange = 8f..72f,
                            steps = 63,
                            colors = SliderDefaults.colors(
                                thumbColor = TealPrimary,
                                activeTrackColor = TealPrimary,
                                inactiveTrackColor = SlateBorder
                            ),
                            modifier = Modifier.testTag("custom_hours_slider")
                        )
                    }
                }
            }
        }
    }
}
