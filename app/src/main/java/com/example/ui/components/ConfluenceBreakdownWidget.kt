package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ConfluenceItem
import com.example.model.TradeSetup
import com.example.ui.theme.TradeAmber
import com.example.ui.theme.TradeCyan
import com.example.ui.theme.TradeGreen
import com.example.ui.theme.TradePurple
import com.example.ui.theme.TradeRed
import com.example.ui.theme.TradingDarkBg
import com.example.ui.theme.TradingSurfaceElevated
import com.example.ui.theme.TradingSurfaceHighlight

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ConfluenceBreakdownWidget(
    setup: TradeSetup,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(TradingSurfaceElevated, RoundedCornerShape(16.dp))
            .border(1.dp, TradingSurfaceHighlight, RoundedCornerShape(16.dp))
            .padding(16.dp)
            .testTag("confluence_breakdown_widget")
    ) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Analytics,
                contentDescription = "Confluences",
                tint = TradeCyan,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "TECHNICAL CONFLUENCES",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "${setup.confluences.size} ALIGNED",
                color = TradeCyan,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Detected SMC & Indicators Chips
        if (setup.smcConceptsFound.isNotEmpty() || setup.indicatorsFound.isNotEmpty()) {
            Text(
                text = "DETECTED SIGNALS & STRUCTURE",
                color = Color(0xFF64748B),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(6.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                setup.smcConceptsFound.forEach { smc ->
                    SignalChip(text = smc, color = TradePurple)
                }
                setup.indicatorsFound.forEach { ind ->
                    SignalChip(text = ind, color = TradeCyan)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        // Detailed Confluences List
        Text(
            text = "MULTI-FACTOR ANALYSIS",
            color = Color(0xFF64748B),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        setup.confluences.forEach { confluence ->
            ConfluenceItemRow(confluence)
            Spacer(modifier = Modifier.height(6.dp))
        }

        // Market Structure Note
        if (setup.marketStructure.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TradingDarkBg, RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Row {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Market Structure",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Market Structure Context",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = setup.marketStructure,
                            color = Color(0xFFE2E8F0),
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                    }
                }
            }
        }

        // Step-by-Step Execution Plan
        if (setup.executionPlan.isNotBlank()) {
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TradeCyan.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                    .border(1.dp, TradeCyan.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Row {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = "Execution Plan",
                        tint = TradeCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Actionable Execution Plan",
                            color = TradeCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = setup.executionPlan,
                            color = Color(0xFFE2E8F0),
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                    }
                }
            }
        }

        // Invalidation Condition
        if (setup.invalidationCondition.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TradeRed.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                    .border(1.dp, TradeRed.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Row {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Invalidation",
                        tint = TradeRed,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Invalidation Condition",
                            color = TradeRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = setup.invalidationCondition,
                            color = Color(0xFFE2E8F0),
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SignalChip(text: String, color: Color) {
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
            .border(0.5.dp, color.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ConfluenceItemRow(item: ConfluenceItem) {
    val biasColor = when (item.bias.uppercase()) {
        "BULLISH" -> TradeGreen
        "BEARISH" -> TradeRed
        else -> TradeAmber
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(TradingDarkBg.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = 4.dp)
                .size(8.dp)
                .background(biasColor, CircleShape)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = item.name,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )

                Box(
                    modifier = Modifier
                        .background(biasColor.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = item.bias,
                        color = biasColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = item.detail,
                color = Color(0xFF94A3B8),
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }
    }
}
