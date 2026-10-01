package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TradeDecision
import com.example.model.TradeSetup
import com.example.ui.theme.TradeAmber
import com.example.ui.theme.TradeCyan
import com.example.ui.theme.TradeGreen
import com.example.ui.theme.TradeRed
import com.example.ui.theme.TradingDarkBg
import com.example.ui.theme.TradingSurfaceElevated
import com.example.ui.theme.TradingSurfaceHighlight

@Composable
fun PriceLadderWidget(
    setup: TradeSetup,
    modifier: Modifier = Modifier
) {
    val isBuy = setup.decision == TradeDecision.BUY_NOW
    val isSell = setup.decision == TradeDecision.SELL_NOW
    val isWait = setup.decision == TradeDecision.WAIT
    val isNoTrade = setup.decision == TradeDecision.NO_TRADE

    val decisionColor = when (setup.decision) {
        TradeDecision.BUY_NOW -> TradeGreen
        TradeDecision.SELL_NOW -> TradeRed
        TradeDecision.WAIT -> TradeAmber
        TradeDecision.NO_TRADE -> Color(0xFF94A3B8)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(TradingSurfaceElevated, RoundedCornerShape(16.dp))
            .border(1.dp, TradingSurfaceHighlight, RoundedCornerShape(16.dp))
            .padding(16.dp)
            .testTag("price_ladder_widget")
    ) {
        // Main Decision Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(decisionColor.copy(alpha = 0.15f), CircleShape)
                    .border(1.5.dp, decisionColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (setup.decision) {
                        TradeDecision.BUY_NOW -> Icons.Default.ArrowUpward
                        TradeDecision.SELL_NOW -> Icons.Default.ArrowDownward
                        TradeDecision.WAIT -> Icons.Default.Pause
                        TradeDecision.NO_TRADE -> Icons.Default.Shield
                    },
                    contentDescription = setup.decision.label,
                    tint = decisionColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = setup.decision.label,
                    color = decisionColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.testTag("trade_decision_label")
                )
                Text(
                    text = setup.decision.subtitle,
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Confluence Score Meter
            Box(
                modifier = Modifier
                    .background(TradingDarkBg, RoundedCornerShape(8.dp))
                    .border(1.dp, TradingSurfaceHighlight, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${setup.confluenceScore}%",
                        color = if (setup.confluenceScore >= 70) TradeGreen else if (setup.confluenceScore >= 50) TradeAmber else TradeRed,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "CONFLUENCE",
                        color = Color(0xFF64748B),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Decision Reason Highlight
        if (setup.decisionReason.isNotBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TradingDarkBg.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Text(
                    text = setup.decisionReason,
                    color = Color(0xFFE2E8F0),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Exact Price Action Ladder
        Text(
            text = "EXECUTION PARAMETERS",
            color = Color(0xFF64748B),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Take Profit 2
        LadderStepRow(
            label = "TAKE PROFIT 2 (TP2)",
            sublabel = "Extended Target (R:R 1 : ${setup.riskRewardRatio2})",
            price = setup.takeProfit2,
            percentChange = setup.profitPercent2,
            isPositive = true,
            color = Color(0xFF00E676),
            isTarget = true
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Take Profit 1
        LadderStepRow(
            label = "TAKE PROFIT 1 (TP1)",
            sublabel = "Primary Target (R:R 1 : ${setup.riskRewardRatio1})",
            price = setup.takeProfit1,
            percentChange = setup.profitPercent1,
            isPositive = true,
            color = Color(0xFF4ADE80),
            isTarget = true
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Entry Price / Zone
        val entryText = if (setup.entryZoneLow != null && setup.entryZoneHigh != null) {
            "${formatNumber(setup.entryZoneLow)} – ${formatNumber(setup.entryZoneHigh)}"
        } else {
            formatNumber(setup.entryPrice)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(TradeCyan.copy(alpha = 0.10f), RoundedCornerShape(8.dp))
                .border(1.5.dp, TradeCyan, RoundedCornerShape(8.dp))
                .padding(10.dp)
                .testTag("entry_price_row")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(TradeCyan, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (setup.entryType == "ZONE") "ENTRY ZONE" else "ENTRY PRICE",
                            color = TradeCyan,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Text(
                        text = if (isWait) "Wait for price trigger inside zone" else "Optimal fill level",
                        color = Color(0xFF94A3B8),
                        fontSize = 10.sp
                    )
                }

                Text(
                    text = entryText,
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Stop Loss
        LadderStepRow(
            label = "STOP LOSS (SL)",
            sublabel = "Risk Cutoff (-${setup.riskPercent}%)",
            price = setup.stopLoss,
            percentChange = -setup.riskPercent,
            isPositive = false,
            color = TradeRed,
            isTarget = false
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Risk to Reward Visual Summary Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(TradingDarkBg, RoundedCornerShape(8.dp))
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "RISK : REWARD (TP1)",
                    color = Color(0xFF64748B),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "1 : ${setup.riskRewardRatio1}",
                    color = if (setup.riskRewardRatio1 >= 2.0) TradeGreen else if (setup.riskRewardRatio1 >= 1.5) TradeAmber else TradeRed,
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(30.dp)
                    .background(TradingSurfaceHighlight)
            )

            Column {
                Text(
                    text = "RISK : REWARD (TP2)",
                    color = Color(0xFF64748B),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "1 : ${setup.riskRewardRatio2}",
                    color = TradeGreen,
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(30.dp)
                    .background(TradingSurfaceHighlight)
            )

            Column {
                Text(
                    text = "MAX RISK",
                    color = Color(0xFF64748B),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "-${setup.riskPercent}%",
                    color = TradeRed,
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
private fun LadderStepRow(
    label: String,
    sublabel: String,
    price: Double,
    percentChange: Double,
    isPositive: Boolean,
    color: Color,
    isTarget: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(TradingDarkBg.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(color, CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = label,
                    color = color,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp
                )
            }
            Text(
                text = sublabel,
                color = Color(0xFF64748B),
                fontSize = 10.sp
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = formatNumber(price),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace
            )
            val prefix = if (percentChange > 0) "+" else ""
            Text(
                text = "$prefix${String.format("%.2f", percentChange)}%",
                color = if (isPositive) TradeGreen else TradeRed,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

private fun formatNumber(value: Double): String {
    return if (value >= 1000) {
        String.format("%,.2f", value)
    } else if (value >= 1) {
        String.format("%.4f", value)
    } else {
        String.format("%.5f", value)
    }
}
